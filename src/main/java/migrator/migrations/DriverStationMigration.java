package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class DriverStationMigration extends AbstractMigration {
    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        moveStaticMethod(
            cu,
            "DriverStation",
            "reportError",
            "DriverStationErrors",
            "reportError",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "reportWarning",
            "DriverStationErrors",
            "reportWarning",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getAlliance",
            "MatchState",
            "getAlliance",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getGameSpecificMessage",
            "MatchState",
            "getGameData",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getMatchType",
            "MatchState",
            "getMatchType",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getMatchNumber",
            "MatchState",
            "getMatchNumber",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getReplayNumber",
            "MatchState",
            "getReplayNumber",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getLocation",
            "MatchState",
            "getLocation",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isEnabled",
            "RobotState",
            "isEnabled",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isDisabled",
            "RobotState",
            "isDisabled",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isEStopped",
            "RobotState",
            "isEStopped",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isAutonomous",
            "RobotState",
            "isAutonomous",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isAutonomousEnabled",
            "RobotState",
            "isAutonomousEnabled",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isTeleop",
            "RobotState",
            "isTeleop",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isTeleopEnabled",
            "RobotState",
            "isTeleopEnabled",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isTest",
            "RobotState",
            "isUtility",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isTestEnabled",
            "RobotState",
            "isUtilityEnabled",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isDSAttached",
            "RobotState",
            "isDSAttached",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isFMSAttached",
            "RobotState",
            "isFMSAttached",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getMatchTime",
            "MatchState",
            "getMatchTime",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickButton",
            "GenericHID",
            "getRawButton",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickButtonPressed",
            "GenericHID",
            "getRawButtonPressed",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickButtonReleased",
            "GenericHID",
            "getRawButtonReleased",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickAxis",
            "GenericHID",
            "getRawAxis",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickPOV",
            "GenericHID",
            "getPOV",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickAxisCount",
            "GenericHID",
            "getAxesAvailable",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickPOVCount",
            "GenericHID",
            "getPOVsAvailable",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getStickButtonCount",
            "GenericHID",
            "getButtonsMaximumIndex",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getJoystickIsXbox",
            "GenericHID",
            "getGamepadType",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getJoystickType",
            "GenericHID",
            "getGamepadType",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getJoystickName",
            "GenericHID",
            "getName",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isJoystickConnected",
            "GenericHID",
            "isConnected",
            "org.wpilib.driverstation"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "refreshControlWordFromCache",
            "DriverStationBackend",
            "refreshControlWordFromCache",
            "org.wpilib.driverstation.internal"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "getRawAllianceStation",
            "DriverStationBackend",
            "getRawAllianceStation",
            "org.wpilib.driverstation.internal"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "silenceJoystickConnectionWarning",
            "DriverStationBackend",
            "silenceJoystickConnectionWarning",
            "org.wpilib.driverstation.internal"
        );
        moveStaticMethod(
            cu,
            "DriverStation",
            "isJoystickConnectionWarningSilenced",
            "DriverStationBackend",
            "isJoystickConnectionWarningSilenced",
            "org.wpilib.driverstation.internal"
        );

        rewriteQualifiedDriverStationCalls(cu);
        qualifyRobotStateCalls(cu);
        wrapGameDataStringCalls(cu);

    }

    private void rewriteQualifiedDriverStationCalls(CompilationUnit cu) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("org.wpilib.driverstation.DriverStation")) {
                continue;
            }

            switch (call.getNameAsString()) {
                case "getAlliance":
                    ensureImport(cu, "org.wpilib.driverstation.MatchState");
                    call.setScope(new NameExpr("MatchState"));
                    break;
                case "getGameSpecificMessage":
                    ensureImport(cu, "org.wpilib.driverstation.MatchState");
                    call.setScope(new NameExpr("MatchState"));
                    call.setName("getGameData");
                    break;
            }
        }

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            if (!field.getScope().toString().equals("org.wpilib.driverstation.DriverStation.Alliance")) {
                continue;
            }

            ensureImport(cu, "org.wpilib.driverstation.Alliance");
            field.setScope(new NameExpr("Alliance"));
            if (field.getNameAsString().equals("Red")) {
                field.setName("RED");
            } else if (field.getNameAsString().equals("Blue")) {
                field.setName("BLUE");
            }
        }
    }

    private void qualifyRobotStateCalls(CompilationUnit cu) {
        if (!hasImport(cu, "org.wpilib.driverstation.RobotState")) {
            return;
        }

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("RobotState")) {
                continue;
            }

            switch (call.getNameAsString()) {
                case "isEnabled":
                case "isDisabled":
                case "isEStopped":
                case "isAutonomous":
                case "isAutonomousEnabled":
                case "isTeleop":
                case "isTeleopEnabled":
                case "isUtility":
                case "isUtilityEnabled":
                case "isDSAttached":
                case "isFMSAttached":
                    call.setScope(StaticJavaParser.parseExpression("org.wpilib.driverstation.RobotState"));
                    break;
            }
        }

        removeImport(cu, "org.wpilib.driverstation.RobotState");
    }

    private void wrapGameDataStringCalls(CompilationUnit cu) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("getGameData")
                    || call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("MatchState")) {
                continue;
            }

            if (call.getParentNode().isPresent()
                    && call.getParentNode().get() instanceof MethodCallExpr parent
                    && parent.getScope().isPresent()
                    && parent.getScope().get() == call) {
                continue;
            }

            MethodCallExpr replacement = new MethodCallExpr(call.clone(), "orElse");
            replacement.addArgument("\"\"");
            call.replace(replacement);
        }
    }
}
