package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class AddressableLedMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isEmpty()) {
                continue;
            }

            Expression scope = call.getScope().get();

            if (call.getNameAsString().equals("start")
                    && context.isExpressionA(scope, "AddressableLED")) {
                if (call.getParentNode().isPresent()
                        && call.getParentNode().get() instanceof ExpressionStmt statement) {
                    statement.remove();
                }
            }

            if (context.isExpressionA(scope, "LEDPattern")) {
                switch (call.getNameAsString()) {
                    case "scrollAtAbsoluteSpeed":
                        call.setName("scrollAtAbsoluteVelocity");
                        break;
                    case "scrollAtRelativeSpeed":
                        call.setName("scrollAtRelativeVelocity");
                        break;
                }
            }
        }
    }
}
