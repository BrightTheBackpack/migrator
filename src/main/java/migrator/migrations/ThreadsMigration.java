package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ThreadsMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("setCurrentThreadPriority")
                    || call.getArguments().size() != 2) {
                continue;
            }

            call.getArguments().remove(0);
        }
    }
}
