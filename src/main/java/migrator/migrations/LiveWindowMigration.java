package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class LiveWindowMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        removeImport(cu, "org.wpilib.livewindow.LiveWindow");

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("disableAllTelemetry")
                    || call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("LiveWindow")) {
                continue;
            }

            Node parent = call.getParentNode().orElse(null);
            if (parent instanceof ExpressionStmt) {
                parent.remove();
            }
        }
    }
}
