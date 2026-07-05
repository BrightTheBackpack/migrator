package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ChassisVelocitiesFieldMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            // if (!context.isExpressionA(field.getScope(), "ChassisVelocities")) {
            //     continue;
            // }
            switch (field.getNameAsString()) {

                case "vxMetersPerSecond":
                    field.setName("vx");
                    break;

                case "vyMetersPerSecond":
                    field.setName("vy");
                    break;

                case "omegaRadiansPerSecond":
                    field.setName("omega");
                    break;

                case "speedMetersPerSecond":
                    field.setName("velocity");
                    break;

                case "leftMetersPerSecond":
                    field.setName("left");
                    break;

                case "rightMetersPerSecond":
                    field.setName("right");
                    break;
            }
        }
    }
}
