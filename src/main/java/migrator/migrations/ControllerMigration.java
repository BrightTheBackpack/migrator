package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ControllerMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        renameType(
                cu,
                "org.wpilib.command2.button.CommandXboxController",
                "org.wpilib.command2.button.CommandNiDsXboxController");

        renameType(
                cu,
                "org.wpilib.command2.button.CommandPS4Controller",
                "org.wpilib.command2.button.CommandNiDsPS4Controller");

        renameType(
                cu,
                "org.wpilib.command2.button.CommandPS5Controller",
                "org.wpilib.command2.button.CommandNiDsPS5Controller");

        renameType(
                cu,
                "org.wpilib.command2.button.CommandStadiaController",
                "org.wpilib.command2.button.CommandNiDsStadiaController");

        renameType(
                cu,
                "org.wpilib.driverstation.XboxController",
                "org.wpilib.driverstation.NiDsXboxController");

        renameType(
                cu,
                "org.wpilib.driverstation.PS4Controller",
                "org.wpilib.driverstation.NiDsPS4Controller");

        renameType(
                cu,
                "org.wpilib.driverstation.PS5Controller",
                "org.wpilib.driverstation.NiDsPS5Controller");

        renameType(
                cu,
                "org.wpilib.driverstation.StadiaController",
                "org.wpilib.driverstation.NiDsStadiaController");
        renameStaticField(cu, "RumbleType", "kBothRumble", "LEFT_RUMBLE");
        renameStaticField(cu, "RumbleType", "kLeftRumble", "LEFT_RUMBLE");
        renameStaticField(cu, "RumbleType", "kRightRumble", "RIGHT_RUMBLE");
        renameGenericHidRumbleType(cu, "kBothRumble", "LEFT_RUMBLE");
        renameGenericHidRumbleType(cu, "kLeftRumble", "LEFT_RUMBLE");
        renameGenericHidRumbleType(cu, "kRightRumble", "RIGHT_RUMBLE");
    }

    private void renameGenericHidRumbleType(
            CompilationUnit cu,
            String oldField,
            String newField) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            if (!field.getNameAsString().equals(oldField)) {
                continue;
            }

            if (!field.getScope().toString().equals("GenericHID.RumbleType")) {
                continue;
            }

            field.setName(newField);
        }
    }
}
