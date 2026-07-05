package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class MathMigration extends AbstractMigration{
    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        moveStaticMethod(
            cu,
            "MathUtil",
            "clamp",
            "Math",
            "clamp"
        );
        renameType(
            cu,
            "org.wpilib.math.system.plant.DCMotor",
            "org.wpilib.math.system.DCMotor");
            moveStaticMethod(
    cu,
    "MathUtil",
    "interpolate",
    "MathUtil",
    "lerp");
    }
}
