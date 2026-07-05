package migrator.migrations;

import java.lang.classfile.attribute.CompilationIDAttribute;

import com.github.javaparser.ast.CompilationUnit;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class MotorSimMigration extends AbstractMigration{
    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        renameInstanceMethod(cu, context,
        "DCMotorSim",
        "getCurrentDrawAmps",
        "getCurrentDraw");

renameInstanceMethod(cu, context,
        "DCMotorSim",
        "getAngularPositionRad",
        "getAngularPosition");

renameInstanceMethod(cu, context,
        "DCMotorSim",
        "getAngularVelocityRadPerSec",
        "getAngularVelocity");

renameInstanceMethod(cu, context,
        "DCMotorSim",
        "getAngularAccelerationRadPerSecSq",
        "getAngularAcceleration");

renameInstanceMethod(cu, context,
        "DCMotorSim",
        "getTorqueNewtonMeters",
        "getTorque");


// FlywheelSim
renameInstanceMethod(cu, context,
        "FlywheelSim",
        "getCurrentDrawAmps",
        "getCurrentDraw");

renameInstanceMethod(cu, context,
        "FlywheelSim",
        "getAngularPositionRad",
        "getAngularPosition");

renameInstanceMethod(cu, context,
        "FlywheelSim",
        "getAngularVelocityRadPerSec",
        "getAngularVelocity");

renameInstanceMethod(cu, context,
        "FlywheelSim",
        "getAngularAccelerationRadPerSecSq",
        "getAngularAcceleration");

renameInstanceMethod(cu, context,
        "FlywheelSim",
        "getTorqueNewtonMeters",
        "getTorque");
    }
}
