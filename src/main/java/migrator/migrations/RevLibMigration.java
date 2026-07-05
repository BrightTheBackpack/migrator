package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import java.util.Set;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class RevLibMigration extends AbstractMigration {

    private static final Set<String> SPARK_TYPES = Set.of(
            "SparkMax",
            "SparkFlex");

    private static final Set<String> REV_MOTOR_TYPES = Set.of(
            "A301",
            "SparkBase",
            "SparkMax",
            "SparkFlex");

    private static final Set<String> REV_SIGNAL_TYPES = Set.of(
            "A301",
            "RelativeEncoder",
            "ServoHub",
            "SparkBase",
            "SparkMax",
            "SparkFlex");

    private static final Set<String> SIGNAL_GETTERS = Set.of(
            "getAppliedOutput",
            "getBusVoltage",
            "getFaults",
            "getInverted",
            "getMotorCurrent",
            "getMotorTemperature",
            "getPosition",
            "getOutputCurrent",
            "getStickyFaults",
            "getStickyWarnings",
            "getVelocity",
            "getWarnings");

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        migrateConstructors(cu);
        migrateMethods(cu, context);
    }

    private void migrateConstructors(CompilationUnit cu) {
        for (ObjectCreationExpr creation : cu.findAll(ObjectCreationExpr.class)) {
            String type = simpleName(creation.getType().asString());
            NodeList<Expression> args = creation.getArguments();

            if (SPARK_TYPES.contains(type) && args.size() == 2) {
                args.add(0, new IntegerLiteralExpr("0"));
                continue;
            }

            if (type.equals("ServoHub") && args.size() == 1) {
                args.add(new StringLiteralExpr(""));
            }
        }
    }

    private void migrateMethods(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isEmpty()) {
                continue;
            }

            Expression scope = call.getScope().get();
            String methodName = call.getNameAsString();

            if (methodName.equals("set") && isRevMotor(scope, context)) {
                call.setName("setThrottle");
                continue;
            }

            // if (methodName.equals("getOutputCurrent") && isRevSignalSource(scope, context)) {
            //     call.setName("getMotorCurrent");
            //     methodName = "getMotorCurrent";
            // }

            if (SIGNAL_GETTERS.contains(methodName)
                    && isRevSignalSource(scope, context)
                    && shouldWrapSignalCall(call)) {
                call.replace(new MethodCallExpr(call.clone(), "get"));
            }
        }
    }

    private boolean isRevMotor(Expression expression, MigrationContext context) {
        return REV_MOTOR_TYPES.stream()
                .anyMatch(type -> context.isExpressionA(expression, type));
    }

    private boolean isRevSignalSource(Expression expression, MigrationContext context) {
        return REV_SIGNAL_TYPES.stream()
                .anyMatch(type -> context.isExpressionA(expression, type));
    }

    private boolean shouldWrapSignalCall(MethodCallExpr call) {
        if (call.getParentNode().isEmpty()) {
            return false;
        }

        Node parent = call.getParentNode().get();

        if (parent instanceof MethodCallExpr parentCall
                && parentCall.getScope().isPresent()
                && parentCall.getScope().get() == call
                && (parentCall.getNameAsString().equals("get")
                || parentCall.getNameAsString().equals("isValid"))) {
            return false;
        }

        if (parent instanceof VariableDeclarator variable
                && variable.getInitializer().isPresent()
                && variable.getInitializer().get() == call
                && simpleName(variable.getType().asString()).equals("Signal")) {
            return false;
        }

        return true;
    }

    private String simpleName(String name) {
        int lastDot = name.lastIndexOf('.');

        if (lastDot >= 0) {
            return name.substring(lastDot + 1);
        }

        return name;
    }
}
