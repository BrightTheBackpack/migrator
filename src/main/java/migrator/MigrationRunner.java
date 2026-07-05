package migrator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class MigrationRunner {

    private final List<Migration> migrations;
    TypeHierarchy hierarchy = new TypeHierarchy();

    public MigrationRunner(List<Migration> migrations) {
        this.migrations = migrations;
    }

    public void run(Path projectRoot) throws IOException {

        if (!Files.exists(projectRoot)) {
            throw new IOException("Project does not exist: " + projectRoot);
        }
        try (Stream<Path> files = Files.walk(projectRoot)) {

            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(file -> {

                        try {
                            CompilationUnit cu =
                                StaticJavaParser.parse(Files.readString(file));

                            hierarchy.index(cu);

                        } catch (Exception ignored) {
                        }
                    });
        }
        try (Stream<Path> files = Files.walk(projectRoot)) {

            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(this::processFile);

        }
    }

    private void processFile(Path file) {

        try {
            
            String original = Files.readString(file);

            CompilationUnit cu = StaticJavaParser.parse(original);
            VariableTypes variableTypes = new VariableTypes();
            variableTypes.index(cu);
            LexicalPreservingPrinter.setup(cu);
            MigrationContext context = new MigrationContext(hierarchy, variableTypes);
            String current = LexicalPreservingPrinter.print(cu);
ClassOrInterfaceDeclaration clazz =
        cu.findFirst(ClassOrInterfaceDeclaration.class).orElse(null);

if (clazz != null) {
    context.setCurrentClassName(clazz.getNameAsString());
}
            for (Migration migration : migrations) {

                migration.apply(cu, context);
                String dump = cu.toString();
                if (dump.contains("ChassisSpeeds") || dump.contains("ChassisVelocities")) {
                    // System.out.println(">>> contains ChassisSpeeds: " + dump.contains("ChassisSpeeds"));
                    // System.out.println(">>> contains ChassisVelocities: " + dump.contains("ChassisVelocities"));
                }

                String after = LexicalPreservingPrinter.print(cu);

                if (!current.equals(after)) {

                    System.out.printf(
                            "  [%s] %s%n",
                            migration.getName(),
                            file.getFileName());

                    current = after;
                }
            }

            // String updated = LexicalPreservingPrinter.print(cu);
String updated = cu.toString();

            if (!original.equals(updated)) {

                Files.writeString(file, updated);

                System.out.println("Updated " + file);

            }

        } catch (Exception e) {

            System.err.println("Failed to process " + file);
            e.printStackTrace();

        }

    }

}