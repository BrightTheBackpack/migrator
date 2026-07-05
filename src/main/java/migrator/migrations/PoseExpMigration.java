package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class PoseExpMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("exp")
                    || call.getScope().isEmpty()
                    || call.getArguments().size() != 1) {
                continue;
            }

            MethodCallExpr transform = new MethodCallExpr(call.getArgument(0).clone(), "exp");
            MethodCallExpr replacement = new MethodCallExpr(call.getScope().get().clone(), "plus");
            replacement.addArgument(transform);
            call.replace(replacement);
        }
    }
}
