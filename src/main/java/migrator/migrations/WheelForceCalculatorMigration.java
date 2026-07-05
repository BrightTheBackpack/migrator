package migrator.migrations;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class WheelForceCalculatorMigration extends AbstractMigration {

    private static final String CHASSIS_ACCELERATIONS =
            "org.wpilib.math.kinematics.ChassisAccelerations";

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("calculate")) {
                continue;
            }

            if (call.getArguments().size() != 3) {
                continue;
            }

            if (!isWheelForceCalculatorCall(call, context)) {
                continue;
            }

            NodeList<Expression> oldArgs = call.getArguments();
            ObjectCreationExpr accelerations = new ObjectCreationExpr(
                    null,
                    StaticJavaParser.parseClassOrInterfaceType(
                            ensureImportOrGetQualifiedName(cu, CHASSIS_ACCELERATIONS)),
                    new NodeList<>(
                            oldArgs.get(0).clone(),
                            oldArgs.get(1).clone(),
                            oldArgs.get(2).clone()));

            call.setArguments(new NodeList<>(accelerations));
        }
    }

    private boolean isWheelForceCalculatorCall(MethodCallExpr call, MigrationContext context) {
        if (call.getScope().isEmpty()) {
            return context.getTypeHierarchy().isSubclassOf(
                    context.getCurrentClassName(),
                    "WheelForceCalculator");
        }

        Expression scope = call.getScope().get();

        if (scope instanceof NameExpr name
                && name.getNameAsString().equals("WheelForceCalculator")) {
            return true;
        }

        return context.isExpressionA(scope, "WheelForceCalculator");
    }
}
