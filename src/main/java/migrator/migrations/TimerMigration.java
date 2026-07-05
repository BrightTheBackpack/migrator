package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class TimerMigration extends AbstractMigration{

    @Override
    public void apply(CompilationUnit cu, MigrationContext context){
        moveStaticMethod(
    cu,
    "Timer",
    "getFPGATimestamp",
    "Timer",
    "getMonotonicTimestamp");

            moveStaticMethod(
    cu,
    "RobotController",
    "getFPGATime",
    "RobotController",
    "getMonotonicTime");

    }

}