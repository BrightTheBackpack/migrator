package migrator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeHierarchy {

    private final Map<String, String> superclasses = new HashMap<>();
    private final Map<String, String> fieldTypes = new HashMap<>();

    public void index(CompilationUnit cu) {

        for (ClassOrInterfaceDeclaration clazz :
                cu.findAll(ClassOrInterfaceDeclaration.class)) {

            if (clazz.getExtendedTypes().isEmpty()) {
                continue;
            }

            String child = clazz.getNameAsString();
            String parent = clazz.getExtendedTypes().get(0).getNameAsString();

            superclasses.put(child, parent);
        }

        String packageName = cu.getPackageDeclaration()
                .map(pkg -> pkg.getNameAsString())
                .orElse("");

        for (ClassOrInterfaceDeclaration clazz :
                cu.findAll(ClassOrInterfaceDeclaration.class)) {

            String className = clazz.getNameAsString();
            String nestedClassName = nestedClassName(clazz);
            String packageClassName = packageName.isEmpty()
                    ? nestedClassName
                    : packageName + "." + nestedClassName;

            for (FieldDeclaration field : clazz.getFields()) {
                String type = field.getElementType().asString();

                for (VariableDeclarator variable : field.getVariables()) {
                    String fieldName = variable.getNameAsString();

                    fieldTypes.put(className + "." + fieldName, type);
                    fieldTypes.put(nestedClassName + "." + fieldName, type);
                    fieldTypes.put(packageClassName + "." + fieldName, type);
                }
            }
        }
    }

    public boolean isSubclassOf(String type, String target) {

        while (type != null) {

            if (namesMatch(type, target)) {
                return true;
            }

            type = superclasses.get(type);
        }

        return false;
    }

    private boolean namesMatch(String type, String target) {
        return type.equals(target)
                || simpleName(type).equals(simpleName(target));
    }

    private String simpleName(String type) {
        int lastDot = type.lastIndexOf('.');

        if (lastDot >= 0) {
            return type.substring(lastDot + 1);
        }

        return type;
    }

    public String getFieldType(String qualifiedFieldName) {
        return fieldTypes.get(qualifiedFieldName);
    }

    private String nestedClassName(ClassOrInterfaceDeclaration clazz) {
        String name = clazz.getNameAsString();
        Optional<Node> parent = clazz.getParentNode();

        while (parent.isPresent()) {
            Node parentNode = parent.get();

            if (parentNode instanceof ClassOrInterfaceDeclaration parentClass) {
                name = parentClass.getNameAsString() + "." + name;
            }

            parent = parentNode.getParentNode();
        }

        return name;
    }
}
