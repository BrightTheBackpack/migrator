package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class AllianceMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

            if (!field.getNameAsString().equals("Alliance")) {
                continue;
            }

            if (!(field.getScope() instanceof NameExpr scope)) {
                continue;
            }

            if (!scope.getNameAsString().equals("DriverStation")) {
                continue;
            }
            removeImport(
                    cu,
                    "org.wpilib.driverstation.DriverStation.Alliance");

            ensureImport(
                    cu,
                    "org.wpilib.driverstation.Alliance");

            field.replace(new NameExpr("Alliance"));
        }
            for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

                if (!(field.getScope() instanceof NameExpr scope)) {
                    continue;
                }

                if (!scope.getNameAsString().equals("Alliance")) {
                    continue;
                }
            removeImport(
                    cu,
                    "org.wpilib.driverstation.DriverStation.Alliance");

            ensureImport(
                    cu,
                    "org.wpilib.driverstation.Alliance");


                switch (field.getNameAsString()) {
                    case "Blue":
                        field.setName("BLUE");
                        break;

                    case "Red":
                        field.setName("RED");
                        break;
                }
        }

    }
}