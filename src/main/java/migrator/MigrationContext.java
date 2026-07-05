package migrator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;

public class MigrationContext {

    private final TypeHierarchy hierarchy;

    private final VariableTypes variableTypes;
    private String currentClassName;
    public MigrationContext(TypeHierarchy h, VariableTypes v){
        this.hierarchy = h;
        this.variableTypes = v;
    }

    public VariableTypes getVariableTypes(){
        return variableTypes;
    }
    public TypeHierarchy getTypeHierarchy(){
        return hierarchy;
    }
    public void setCurrentClassName(String name) {
    currentClassName = name;
}

public String getCurrentClassName() {
    return currentClassName;
}
    public boolean isVariableA(String variableName, String targetType) {

        String type = variableTypes.get(variableName);

        return hierarchy.isSubclassOf(type, targetType);
    }
public boolean isExpressionA(Expression expr, String targetType) {

    String type = getExpressionType(expr);

    return type != null
            && hierarchy.isSubclassOf(type, targetType);
}
public String getExpressionType(Expression expr) {

    if (expr instanceof NameExpr name) {

        String type = variableTypes.get(name.getNameAsString());

        if (type == null) {
            type = variableTypes.getFieldType(name.getNameAsString());
        }

        return type;
    }
    if (expr instanceof FieldAccessExpr field) {

        if (field.getScope() instanceof ThisExpr) {
            return variableTypes.getFieldType(field.getNameAsString());
        }

        return hierarchy.getFieldType(field.toString());
    }

    if (expr instanceof EnclosedExpr enclosed) {
        return getExpressionType(enclosed.getInner());
    }

    if (expr instanceof CastExpr cast) {
        return cast.getType().asString();
    }

    if (expr instanceof ThisExpr) {
        return currentClassName;
    }

    return null;
}
    public String readResource(String path) {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream(path)) {

            if (in == null) {
                throw new RuntimeException("Missing resource: " + path);
            }

            return new String(in.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
