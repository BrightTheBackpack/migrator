package migrator.migrations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ColorMigration extends AbstractMigration {

    private Set<String> validColors;

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {

        if (validColors == null) {
            validColors = getStaticFieldsFromSource(
                    context.readResource("resources/Color.java"));
        }

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

            if (!(field.getScope() instanceof NameExpr scope))
                continue;

            if (!scope.getNameAsString().equals("Color"))
                continue;

            String oldName = field.getNameAsString();

            if (!oldName.startsWith("k"))
                continue;

            String candidate =
                    camelToScreamingSnake(oldName.substring(1));

            if (validColors.contains(candidate)) {
                field.setName(candidate);
            }
        }
    }
    public static Set<String> getStaticFieldsFromSource(String source) {

    CompilationUnit cu = StaticJavaParser.parse(source);

    Set<String> result = new HashSet<>();

    for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {

        // must be static
        if (!field.isStatic())
            continue;

        // optionally require public (recommended for Color constants)
        if (!field.isPublic())
            continue;

        for (VariableDeclarator var : field.getVariables()) {
            result.add(var.getNameAsString());
        }
    }

    return result;
}
}