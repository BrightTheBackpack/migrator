package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class AlertMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        boolean hadOldAlertImport = hasImport(cu, "org.wpilib.util.Alert");
        boolean hadOldAlertTypeImport = hasImport(cu, "org.wpilib.util.Alert.AlertType");

        if (hadOldAlertImport) {
            removeImport(cu, "org.wpilib.util.Alert");
            ensureImport(cu, "org.wpilib.driverstation.Alert");
        }

        if (hadOldAlertTypeImport) {
            removeImport(cu, "org.wpilib.util.Alert.AlertType");
            ensureImport(cu, "org.wpilib.driverstation.Alert.Level");
        }

        if (!hadOldAlertImport && !hadOldAlertTypeImport) {
            return;
        }

        for (ClassOrInterfaceType type : cu.findAll(ClassOrInterfaceType.class)) {
            if (type.getNameAsString().equals("AlertType")) {
                type.setName("Level");
            }
        }

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            if (field.getScope() instanceof NameExpr scope
                    && scope.getNameAsString().equals("AlertType")) {
                scope.setName("Level");
                field.setName(mapAlertLevel(field.getNameAsString()));
            } else if (field.getScope().toString().equals("Alert.AlertType")) {
                field.setScope(new NameExpr("Level"));
                field.setName(mapAlertLevel(field.getNameAsString()));
            }
        }
    }

    private String mapAlertLevel(String oldName) {
        return switch (oldName) {
            case "kError" -> "HIGH";
            case "kWarning" -> "MEDIUM";
            case "kInfo" -> "LOW";
            default -> oldName;
        };
    }
}
