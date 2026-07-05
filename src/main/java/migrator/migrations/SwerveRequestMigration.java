package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class SwerveRequestMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        renameType(
                cu,
                "com.ctre.phoenix6.swerve.SwerveRequest.ApplyRobotSpeeds",
                "com.ctre.phoenix6.swerve.SwerveRequest.ApplyRobotVelocity");
        renameType(
                cu,
                "com.ctre.phoenix6.swerve.SwerveRequest.ApplyFieldSpeeds",
                "com.ctre.phoenix6.swerve.SwerveRequest.ApplyFieldVelocity");

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            switch (call.getNameAsString()) {
                case "withSpeeds":
                    call.setName("withVelocity");
                    break;
                case "withDesaturateWheelSpeeds":
                    call.setName("withDesaturateWheelVelocities");
                    break;
            }
        }

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            if (!isSwerveRequestField(field, context)) {
                continue;
            }

            switch (field.getNameAsString()) {
                case "Speeds":
                    field.setName("Velocity");
                    break;
                case "DesaturateWheelSpeeds":
                    field.setName("DesaturateWheelVelocities");
                    break;
            }
        }
    }

    private boolean isSwerveRequestField(FieldAccessExpr field, MigrationContext context) {
        Expression scope = field.getScope();

        return context.isExpressionA(scope, "ApplyRobotSpeeds")
                || context.isExpressionA(scope, "ApplyRobotVelocity")
                || context.isExpressionA(scope, "ApplyFieldSpeeds")
                || context.isExpressionA(scope, "ApplyFieldVelocity");
    }
}
