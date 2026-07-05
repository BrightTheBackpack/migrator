package migrator;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;

public class VariableTypes {

    private final Map<String, String> types = new HashMap<>();
        private final Map<String, String> fieldTypes = new HashMap<>();
    public void index(CompilationUnit cu) {

        cu.findAll(VariableDeclarator.class)
                .forEach(v ->
                        types.put(
                                v.getNameAsString(),
                                v.getType().asString()));

        cu.findAll(Parameter.class)
                .forEach(p ->
                        types.put(
                                p.getNameAsString(),
                                p.getType().asString()));
        cu.findAll(FieldDeclaration.class).forEach(field -> {
    String type = field.getElementType().asString();

    for (VariableDeclarator var : field.getVariables()) {
        fieldTypes.put(var.getNameAsString(), type);
    }
});
    }

    public String get(String name) {
        return types.get(name);
    }
    public String getFieldType(String name) {
    return fieldTypes.get(name);
}
}