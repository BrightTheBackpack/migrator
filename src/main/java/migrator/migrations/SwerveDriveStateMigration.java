package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class SwerveDriveStateMigration extends AbstractMigration {

    @Override
    public String getName() {
        return "SwerveDriveStateMigration";
    }

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        renameField(cu, context, "SwerveDriveState", "Speeds", "Velocity");
        renameField(cu, context, "SwerveDriveState", "ModuleStates", "ModuleVelocities");
        renameField(cu, context, "SwerveControlParameters", "currentChassisSpeed", "currentChassisVelocity");
        renameField(cu, context, "ModuleState", "State", "Velocity");
        renameField(cu, context, "State", "speeds", "velocity");

        renameInstanceMethod(cu, context, "SwerveModule", "getCurrentState", "getCurrentVelocity");
        renameInstanceMethod(cu, context, "SwerveModule", "getTargetState", "getTargetVelocity");
    }

    private void renameField(
            CompilationUnit cu,
            MigrationContext context,
            String targetType,
            String oldName,
            String newName) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

            if (!field.getNameAsString().equals(oldName)) {
                continue;
            }

            if (isFieldOnType(field, context, targetType)) {
                field.setName(newName);
            }
        }
    }

    private boolean isFieldOnType(
            FieldAccessExpr field,
            MigrationContext context,
            String targetType) {

        Expression scope = field.getScope();

        if (scope instanceof NameExpr name) {
            return context.isVariableA(name.getNameAsString(), targetType);
        }

        if (scope instanceof ThisExpr) {
            return context.getTypeHierarchy().isSubclassOf(context.getCurrentClassName(), targetType);
        }

        return context.isExpressionA(scope, targetType);
    }
}
