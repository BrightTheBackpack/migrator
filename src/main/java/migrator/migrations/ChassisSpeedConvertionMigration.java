package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import migrator.MigrationContext;
import migrator.TypeHierarchy;
import migrator.VariableTypes;
import migrator.AbstractMigration;

public class ChassisSpeedConvertionMigration extends AbstractMigration {
    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        rewriteStaticMethod(
        cu,
        "ChassisSpeeds",
        "fromRobotRelativeSpeeds",
        call -> {

            NodeList<Expression> args = call.getArguments();

            if (args.size() == 2) {

                MethodCallExpr replacement = new MethodCallExpr(
                        args.get(0).clone(),
                        "toFieldRelative");

                replacement.addArgument(args.get(1).clone());

                return replacement;
            }

            if (args.size() == 4) {

                ObjectCreationExpr velocities = new ObjectCreationExpr();

                velocities.setType("ChassisVelocities");

                velocities.addArgument(args.get(0).clone());
                velocities.addArgument(args.get(1).clone());
                velocities.addArgument(args.get(2).clone());

                MethodCallExpr replacement = new MethodCallExpr(
                        velocities,
                        "toFieldRelative");

                replacement.addArgument(args.get(3).clone());

                return replacement;
            }

            return null;
        });
        rewriteStaticMethod(
                cu,
                "ChassisSpeeds",
                "fromFieldRelativeSpeeds",
                call -> {

                    NodeList<Expression> args = call.getArguments();

                    if (args.size() == 2) {

                        MethodCallExpr replacement = new MethodCallExpr(
                                args.get(0).clone(),
                                "toRobotRelative");

                        replacement.addArgument(args.get(1).clone());

                        return replacement;
                    }

                    if (args.size() == 4) {

                        ObjectCreationExpr velocities = new ObjectCreationExpr();

                        velocities.setType("ChassisVelocities");

                        velocities.addArgument(args.get(0).clone());
                        velocities.addArgument(args.get(1).clone());
                        velocities.addArgument(args.get(2).clone());

                        MethodCallExpr replacement = new MethodCallExpr(
                                velocities,
                                "toRobotRelative");

                        replacement.addArgument(args.get(3).clone());

                        return replacement;
                    }

                    return null;
                });
                rewriteStaticMethod(
                cu,
                "ChassisVelocities",
                "fromFieldRelativeSpeeds",
                call -> {

                    NodeList<Expression> args = call.getArguments();

                    if (args.size() == 2) {

                        MethodCallExpr replacement = new MethodCallExpr(
                                args.get(0).clone(),
                                "toRobotRelative");

                        replacement.addArgument(args.get(1).clone());

                        return replacement;
                    }

                    if (args.size() == 4) {

                        ObjectCreationExpr velocities = new ObjectCreationExpr();

                        velocities.setType("ChassisVelocities");

                        velocities.addArgument(args.get(0).clone());
                        velocities.addArgument(args.get(1).clone());
                        velocities.addArgument(args.get(2).clone());

                        MethodCallExpr replacement = new MethodCallExpr(
                                velocities,
                                "toRobotRelative");

                        replacement.addArgument(args.get(3).clone());

                        return replacement;
                    }

                    return null;
                });

        rewriteStaticMethod(
                cu,
                "ChassisSpeeds",
                "discretize",
                call -> {
                    NodeList<Expression> args = call.getArguments();

                    if (args.size() != 2) {
                        return null;
                    }

                    MethodCallExpr replacement = new MethodCallExpr(
                            args.get(0).clone(),
                            "discretize");

                    replacement.addArgument(args.get(1).clone());

                    return replacement;
                });

                        rewriteStaticMethod(
                cu,
                "ChassisVelocities",
                "discretize",
                call -> {
                    NodeList<Expression> args = call.getArguments();

                    if (args.size() != 2) {
                        return null;
                    }

                    MethodCallExpr replacement = new MethodCallExpr(
                            args.get(0).clone(),
                            "discretize");

                    replacement.addArgument(args.get(1).clone());

                    return replacement;
                });

    }
}
