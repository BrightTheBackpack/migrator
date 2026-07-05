package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class VelocityMigration extends AbstractMigration {
        private static final String OLD_IMPORT =
            "org.wpilib.math.kinematics.ChassisSpeeds";

    private static final String NEW_IMPORT =
            "org.wpilib.math.kinematics.ChassisVelocities";
    private static final String OLD_MODULE_IMPORT =
            "org.wpilib.math.kinematics.SwerveModuleState";

    private static final String NEW_MODULE_IMPORT =
            "org.wpilib.math.kinematics.SwerveModuleVelocity";

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        renameType(cu, OLD_IMPORT, NEW_IMPORT);
        renameType(cu, OLD_MODULE_IMPORT, NEW_MODULE_IMPORT);
        renameInstanceMethod(
                cu,
                context,
                "SwerveDriveKinematics",
                "toChassisSpeeds",
                "toChassisVelocities");
        renameKinematicsMethod(cu, "toSwerveModuleStates", "toSwerveModuleVelocities");
        renameKinematicsMethod(cu, "desaturateWheelSpeeds", "desaturateWheelVelocities");
        renameKinematicsMethod(cu, "toChassisSpeeds", "toChassisVelocities");
        renameType(
    cu,
    "com.ctre.phoenix6.swerve.SwerveRequest.ApplyRobotSpeeds",
    "com.ctre.phoenix6.swerve.SwerveRequest.ApplyRobotVelocity");

renameType(
    cu,
    "com.ctre.phoenix6.swerve.SwerveRequest.ApplyFieldSpeeds",
    "com.ctre.phoenix6.swerve.SwerveRequest.ApplyFieldVelocity");
    }

    private void renameKinematicsMethod(
            CompilationUnit cu,
            String oldMethod,
            String newMethod) {

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getNameAsString().equals(oldMethod)) {
                call.setName(newMethod);
            }
        }
    }
}
