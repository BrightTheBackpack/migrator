package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class PhoenixMotorControllerMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        renameTalonMethod(cu, context, "set", "setThrottle");
        renameTalonMethod(cu, context, "get", "getThrottle");
        renameTalonMethod(cu, context, "setNeutralMode", "configNeutralMode");
    }

    private void renameTalonMethod(
            CompilationUnit cu,
            MigrationContext context,
            String oldMethod,
            String newMethod) {

        renameInstanceMethod(cu, context, "TalonFX", oldMethod, newMethod);
        renameInstanceMethod(cu, context, "TalonFXS", oldMethod, newMethod);
    }
}
