package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class LinSysMigration extends AbstractMigration {
    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        rewriteStaticMethod(
        cu,
        "LinearSystemId",
        "createDCMotorSystem",
        call -> {

            NodeList<Expression> args = call.getArguments();

            if (args.size() == 2) {

                MethodCallExpr replacement = new MethodCallExpr(
                        new NameExpr(ensureImportOrGetQualifiedName(
                                cu,
                                "org.wpilib.math.system.Models")),
                        "singleJointedArmFromSysId");

                replacement.addArgument(args.get(0).clone());
                replacement.addArgument(args.get(1).clone());

                return replacement;
            }

            if (args.size() == 3) {

                MethodCallExpr replacement = new MethodCallExpr(
                        new NameExpr(ensureImportOrGetQualifiedName(
                                cu,
                                "org.wpilib.math.system.Models")),
                        "singleJointedArmFromPhysicalConstants");

                replacement.addArgument(args.get(0).clone());
                replacement.addArgument(args.get(1).clone());
                replacement.addArgument(args.get(2).clone());

                return replacement;
            }

            return null;
        });

                rewriteStaticMethod(
        cu,
        "LinearSystemId",
        "createFlywheelSystem",
        call -> {

            NodeList<Expression> args = call.getArguments();

            if (args.size() == 2) {

                MethodCallExpr replacement = new MethodCallExpr(
                        new NameExpr(ensureImportOrGetQualifiedName(
                                cu,
                                "org.wpilib.math.system.Models")),
                        "flywheelFromSysId");

                replacement.addArgument(args.get(0).clone());
                replacement.addArgument(args.get(1).clone());

                return replacement;
            }

            if (args.size() == 3) {

                MethodCallExpr replacement = new MethodCallExpr(
                        new NameExpr(ensureImportOrGetQualifiedName(
                                cu,
                                "org.wpilib.math.system.Models")),
                        "flywheelFromPhysicalConstants");

                replacement.addArgument(args.get(0).clone());
                replacement.addArgument(args.get(1).clone());
                replacement.addArgument(args.get(2).clone());

                return replacement;
            }

            return null;
        });
    rewriteStaticMethod(
            cu,
            "LinearSystemId",
            "createSingleJointedArmSystem",
            call -> staticCall(
                    cu,
                    "org.wpilib.math.system.Models",
                    "singleJointedArmFromPhysicalConstants",
                    call.getArguments().toArray(new Expression[0])));

    rewriteStaticMethod(
            cu,
            "LinearSystemId",
            "createElevatorSystem",
            call -> staticCall(
                    cu,
                    "org.wpilib.math.system.Models",
                    "elevatorFromPhysicalConstants",
                    call.getArguments().toArray(new Expression[0])));

    rewriteStaticMethod(
            cu,
            "LinearSystemId",
            "createDrivetrainVelocitySystem",
            call -> staticCall(
                    cu,
                    "org.wpilib.math.system.Models",
                    "differentialDriveFromPhysicalConstants",
                    call.getArguments().toArray(new Expression[0])));

removeImport(cu, "org.wpilib.math.system.plant.LinearSystemId");
    }
}
