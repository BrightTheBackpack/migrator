package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class PhoenixTimeMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            String methodName = call.getNameAsString();

            if (!methodName.equals("fpgaToCurrentTime")
                    && !methodName.equals("currentTimeToFPGATime")) {
                continue;
            }

            if (call.getScope().isEmpty() || !call.getScope().get().toString().equals("Utils")) {
                continue;
            }

            if (call.getArguments().size() == 1) {
                call.replace(call.getArgument(0).clone());
            }
        }
    }
}
