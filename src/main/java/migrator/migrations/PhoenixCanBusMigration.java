package migrator.migrations;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class PhoenixCanBusMigration extends AbstractMigration {

    private static final String CAN_BUS = "com.ctre.phoenix6.CANBus";

    private static final Pattern SYSTEMCORE_BUS = Pattern.compile("can_s([1-5])");
    private static final Pattern MOTIONCORE_BUS = Pattern.compile("can_m([1-9]|1[0-9]|20)");

    private static final Set<String> PHOENIX_DEVICE_TYPES = Set.of(
            "CANcoder",
            "CANdi",
            "CANdle",
            "CANrange",
            "Pigeon2",
            "TalonFX",
            "TalonFXS");

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (ObjectCreationExpr creation : cu.findAll(ObjectCreationExpr.class)) {
            if (creation.getType().getNameAsString().equals("CANBus")) {
                replaceCoreCanBusConstructor(cu, creation);
                continue;
            }

            if (!PHOENIX_DEVICE_TYPES.contains(creation.getType().getNameAsString())) {
                continue;
            }

            NodeList<Expression> args = creation.getArguments();

            if (args.size() == 1) {
                args.add(newCanBus(cu));
                continue;
            }

            if (args.size() >= 2 && shouldWrapAsCanBus(args.get(1), context)) {
                args.set(1, newCanBus(cu, args.get(1)));
            }
        }
    }

    private void replaceCoreCanBusConstructor(
            CompilationUnit cu,
            ObjectCreationExpr creation) {

        if (creation.getArguments().size() != 1
                || !(creation.getArgument(0) instanceof StringLiteralExpr literal)) {
            return;
        }

        Expression coreBus = coreBusFactory(cu, literal);

        if (coreBus != null) {
            creation.replace(coreBus);
        }
    }

    private boolean shouldWrapAsCanBus(Expression expression, MigrationContext context) {
        if (looksLikeCanBus(expression) || context.isExpressionA(expression, "CANBus")) {
            return false;
        }

        return expression instanceof StringLiteralExpr
                || context.isExpressionA(expression, "String");
    }

    private boolean looksLikeCanBus(Expression expression) {
        if (expression instanceof ObjectCreationExpr creation) {
            return creation.getType().getNameAsString().equals("CANBus");
        }

        if (expression instanceof MethodCallExpr call
                && call.getScope().isPresent()
                && call.getScope().get() instanceof NameExpr scope) {
            return scope.getNameAsString().equals("CANBus")
                    && (call.getNameAsString().equals("systemcore")
                    || call.getNameAsString().equals("motioncore"));
        }

        return false;
    }

    private Expression newCanBus(CompilationUnit cu) {
        String canBusClass = ensureImportOrGetQualifiedName(cu, CAN_BUS);
        ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(canBusClass);

        return new ObjectCreationExpr(null, type, new NodeList<>());
    }

    private Expression newCanBus(CompilationUnit cu, Expression bus) {
        String canBusClass = ensureImportOrGetQualifiedName(cu, CAN_BUS);

        if (bus instanceof StringLiteralExpr literal) {
            Expression coreBus = coreBusFactory(cu, literal);

            if (coreBus != null) {
                return coreBus;
            }
        }

        ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(canBusClass);
        NodeList<Expression> args = new NodeList<>();
        args.add(bus.clone());

        return new ObjectCreationExpr(null, type, args);
    }

    private Expression coreBusFactory(CompilationUnit cu, StringLiteralExpr literal) {
        String canBusClass = ensureImportOrGetQualifiedName(cu, CAN_BUS);
        Matcher systemcore = SYSTEMCORE_BUS.matcher(literal.getValue());

        if (systemcore.matches()) {
            return StaticJavaParser.parseExpression(
                    canBusClass + ".systemcore(" + systemcore.group(1) + ")");
        }

        Matcher motioncore = MOTIONCORE_BUS.matcher(literal.getValue());

        if (motioncore.matches()) {
            return StaticJavaParser.parseExpression(
                    canBusClass + ".motioncore(" + motioncore.group(1) + ")");
        }

        return null;
    }
}
