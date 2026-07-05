package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ScheduleMigration extends AbstractMigration {

    private static final String COMMAND_SCHEDULER =
            "org.wpilib.command2.CommandScheduler";

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        

        for (MethodCallExpr call : findMethodCalls(cu, "schedule")) {

            // Only migrate schedule() with no arguments
            if (!call.getArguments().isEmpty()) {
                continue;
            }

            // schedule() must have something before it
            if (call.getScope().isEmpty()) {
                continue;
            }
            Expression originalCommand = call.getScope().get().clone();

            MethodCallExpr getInstance =
                    new MethodCallExpr(
                            new NameExpr("CommandScheduler"),
                            "getInstance");

            MethodCallExpr replacement =
                    new MethodCallExpr(
                            getInstance,
                            "schedule");

            replacement.addArgument(originalCommand);

            call.replace(replacement);
            ensureImport(cu, COMMAND_SCHEDULER);
        }
    }

}