package migrator;

import migrator.migrations.AllianceMigration;
import migrator.migrations.AlertMigration;
import migrator.migrations.AddressableLedMigration;
import migrator.migrations.ChassisSpeedConvertionMigration;
import migrator.migrations.ChassisVelocitiesFieldMigration;
import migrator.migrations.ColorMigration;
import migrator.migrations.ControllerMigration;
import migrator.migrations.CorvusCommandSwerveDrivetrainMigration;
import migrator.migrations.DriverStationMigration;
import migrator.migrations.FrameworkRobotStateMigration;
import migrator.migrations.LinSysMigration;
import migrator.migrations.LiveWindowMigration;
import migrator.migrations.MathMigration;
import migrator.migrations.MathSharedMigration;
import migrator.migrations.ModuleStatesMigration;
import migrator.migrations.MotorSimMigration;
import migrator.migrations.NetworkTablesMigration;
import migrator.migrations.PhoenixCanBusMigration;
import migrator.migrations.PhoenixMotorControllerMigration;
import migrator.migrations.PhoenixTimeMigration;
import migrator.migrations.PoseExpMigration;
import migrator.migrations.RevLibMigration;
import migrator.migrations.ScheduleMigration;
import migrator.migrations.ShuffleboardMigration;
import migrator.migrations.SwerveRequestMigration;
import migrator.migrations.SwerveDriveStateMigration;
import migrator.migrations.ThreadsMigration;
import migrator.migrations.VelocityMigration;
import migrator.migrations.WheelForceCalculatorMigration;
import migrator.migrations.TimerMigration;
import migrator.migrations.TimedRobotOverrideMigration;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
    StaticJavaParser.getParserConfiguration()
            .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);

        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("    java -jar robot-migrator.jar <project>");
            return;
        }

        Path project = Path.of(args[0]);

        List<Migration> migrations = List.of(
                new ScheduleMigration(),
                new AllianceMigration(),
                new DriverStationMigration(),
                new FrameworkRobotStateMigration(),
                new VelocityMigration(),
                new ModuleStatesMigration(),
                new NetworkTablesMigration(),
                new ColorMigration(),
                new ChassisVelocitiesFieldMigration(),
                new ControllerMigration(),
                new ChassisSpeedConvertionMigration(),
                new TimerMigration(),
                new MathMigration(),
                new MathSharedMigration(),
                new LinSysMigration(),
                new SwerveDriveStateMigration(),
                new SwerveRequestMigration(),
                new PhoenixCanBusMigration(),
                new PhoenixMotorControllerMigration(),
                new CorvusCommandSwerveDrivetrainMigration(),
                new PhoenixTimeMigration(),
                new WheelForceCalculatorMigration(),
                new RevLibMigration(),
                new PoseExpMigration(),
                new AlertMigration(),
                new AddressableLedMigration(),
                new ShuffleboardMigration(),
                new LiveWindowMigration(),
                new ThreadsMigration(),
                new TimedRobotOverrideMigration(),
                new MotorSimMigration()
        );

        MigrationRunner runner = new MigrationRunner(migrations);

        runner.run(project);
    }
}
