package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ModuleStatesMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

            if (!field.getNameAsString().equals("ModuleStates")) {
                continue;
            }

            field.setName("ModuleVelocities");
        }
    }
}