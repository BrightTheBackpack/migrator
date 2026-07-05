package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class TimedRobotOverrideMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (method.getNameAsString().equals("loopFunc") && method.getParameters().isEmpty()) {
                method.remove();
                continue;
            }

            switch (method.getNameAsString()) {
                case "testInit":
                    method.setName("utilityInit");
                    break;
                case "testPeriodic":
                    method.setName("utilityPeriodic");
                    break;
                case "testExit":
                    method.setName("utilityExit");
                    break;
            }
        }
    }
}
