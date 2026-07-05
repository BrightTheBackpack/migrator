package migrator.migrations;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class FrameworkRobotStateMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("org.wpilib.framework.RobotState")) {
                continue;
            }

            call.setScope(StaticJavaParser.parseExpression("org.wpilib.driverstation.RobotState"));
        }
    }
}
