package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class MathSharedMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        removeImport(cu, "org.wpilib.math.MathUsageId");

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (!method.getNameAsString().equals("reportUsage")
                    || method.getParameters().size() != 2) {
                continue;
            }

            String firstType = method.getParameter(0).getType().asString();
            if (firstType.equals("MathUsageId")
                    || firstType.equals("org.wpilib.math.MathUsageId")) {
                method.getParameter(0).setType("String");
                method.getParameter(1).setType("String");
            }
        }
    }
}
