package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.Set;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class CorvusCommandSwerveDrivetrainMigration extends AbstractMigration {

    private static final Set<String> REMOVED_OVERLOADS = Set.of(
            "addVisionMeasurement",
            "AddVisionMeasurement",
            "samplePoseAt",
            "SamplePoseAt");

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (!clazz.getNameAsString().equals("CommandSwerveDrivetrain")) {
                continue;
            }

            for (MethodDeclaration method : clazz.getMethods().stream().toList()) {
                if (isRemovedGeneratedOverload(method)) {
                    method.remove();
                }
            }
        }
    }

    private boolean isRemovedGeneratedOverload(MethodDeclaration method) {
        if (!REMOVED_OVERLOADS.contains(method.getNameAsString())
                || method.getBody().isEmpty()) {
            return false;
        }

        String body = method.getBody().get().toString();
        String lowerName = Character.toLowerCase(method.getNameAsString().charAt(0))
                + method.getNameAsString().substring(1);

        return body.contains("Utils.fpgaToCurrentTime")
                || body.contains("Utils.currentTimeToFPGATime")
                || (body.contains("super." + method.getNameAsString() + "(")
                && method.getBody().get().getStatements().size() <= 2)
                || (body.contains("super." + lowerName + "(")
                && method.getBody().get().getStatements().size() <= 2);
    }
}
