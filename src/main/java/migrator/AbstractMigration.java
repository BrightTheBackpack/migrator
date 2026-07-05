package migrator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractMigration implements Migration {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Finds all method calls with the given name.
     */
    protected List<MethodCallExpr> findMethodCalls(
            CompilationUnit cu,
            String methodName) {

        return cu.findAll(MethodCallExpr.class).stream()
                .filter(call -> call.getNameAsString().equals(methodName))
                .collect(Collectors.toList());
    }

    /**
     * Finds all object creations of the given class.
     */
    protected List<ObjectCreationExpr> findObjectCreations(
            CompilationUnit cu,
            String className) {

        return cu.findAll(ObjectCreationExpr.class).stream()
                .filter(obj -> obj.getType().getNameAsString().equals(className))
                .collect(Collectors.toList());
    }

    /**
     * Returns true if the compilation unit imports the given class.
     */
    protected boolean hasImport(
            CompilationUnit cu,
            String qualifiedName) {

        return cu.getImports().stream()
                .anyMatch(i -> i.getNameAsString().equals(qualifiedName));
    }

    /**
     * Adds an import if it does not already exist.
     */
    protected void ensureImport(
            CompilationUnit cu,
            String qualifiedName) {

        if (!hasImport(cu, qualifiedName)) {
            cu.addImport(qualifiedName);
        }
    }

    /**
     * Removes an import if it exists.
     */
    protected void removeImport(
            CompilationUnit cu,
            String qualifiedName) {

        Optional<ImportDeclaration> importDecl =
                cu.getImports().stream()
                        .filter(i -> i.getNameAsString().equals(qualifiedName))
                        .findFirst();

        importDecl.ifPresent(ImportDeclaration::remove);
    }
    private void renameInTypeTree(
            CompilationUnit cu,
            ClassOrInterfaceType cit,
            String oldSimple,
            String newQualified) {

        if (cit.getNameAsString().equals(oldSimple)) {

            String newSimple = ensureImportOrGetQualifiedName(cu, newQualified);
            cit.setName(newSimple);
        }

        cit.getTypeArguments().ifPresent(typeArguments ->
                typeArguments.forEach(typeArgument -> {
                    if (typeArgument.isClassOrInterfaceType()) {
                        renameInTypeTree(cu, typeArgument.asClassOrInterfaceType(), oldSimple, newQualified);
                    }
                }));
        }
    protected void renameType(
            CompilationUnit cu,
            String oldQualifiedName,
            String newQualifiedName) {

        String oldSimple = simpleName(oldQualifiedName);

        // 1. Rename every ClassOrInterfaceType node, including ones nested inside
        //    generics, arrays, wildcards, etc. findAll already recurses into
        //    type arguments on its own — no manual tree-walk required.
        cu.findAll(ClassOrInterfaceType.class)
                .forEach(cit -> {
                        
                    renameInTypeTree(cu, cit, oldSimple, newQualifiedName);});

        // 2. Object creation (new X())
        cu.findAll(ObjectCreationExpr.class)
                .forEach(n -> {
                    if (n.getType().getNameAsString().equals(oldSimple)) {
                        String newSimple = ensureImportOrGetQualifiedName(cu, newQualifiedName);
                        n.getType().setName(newSimple);
                    }

                    if (n.getType().getTypeArguments().isPresent()) {
                        n.getType().getTypeArguments().get()
                                .forEach(arg -> {
                                    if (arg.isClassOrInterfaceType()) {
                                        renameInTypeTree(cu, arg.asClassOrInterfaceType(), oldSimple, newQualifiedName);
                                    }
                        });
                    }
                });

        // 3. Static class usages (X.CONSTANT, X.Nested.CONSTANT, X.staticCall())
        renameStaticTypeScopes(cu, oldSimple, newQualifiedName);

        // 4. remove old import
        removeImport(cu, oldQualifiedName);
    }

    private void renameStaticTypeScopes(
            CompilationUnit cu,
            String oldSimple,
            String newQualifiedName) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            if (field.getScope() instanceof NameExpr scope
                    && scope.getNameAsString().equals(oldSimple)) {
                field.setScope(StaticJavaParser.parseExpression(
                        ensureImportOrGetQualifiedName(cu, newQualifiedName)));
            }
        }

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (call.getScope().isPresent()
                    && call.getScope().get() instanceof NameExpr scope
                    && scope.getNameAsString().equals(oldSimple)) {
                call.setScope(StaticJavaParser.parseExpression(
                        ensureImportOrGetQualifiedName(cu, newQualifiedName)));
            }
        }
    }
    private String simpleName(String qualifiedName) {
    return qualifiedName.substring(
            qualifiedName.lastIndexOf('.') + 1);
    }
    protected boolean hasConflictingImport(
        CompilationUnit cu,
        String qualifiedName) {

        String simpleName =
                qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);

        return cu.getImports().stream()
                .map(i -> i.getNameAsString())
                .filter(name -> name.endsWith("." + simpleName))
                .anyMatch(name -> !name.equals(qualifiedName));
    }
    protected String ensureImportOrGetQualifiedName(
        CompilationUnit cu,
        String qualifiedName) {

        if (hasImport(cu, qualifiedName)) {
            return simpleName(qualifiedName);
        }

        if (hasConflictingImport(cu, qualifiedName)) {
            return qualifiedName;
        }

        cu.addImport(qualifiedName);

        return simpleName(qualifiedName);
    }
    protected void moveStaticMethod(
        CompilationUnit cu,
        String oldClass,
        String oldMethod,
        String newClass,
        String newMethod,
        String importName
    ) {

        for (MethodCallExpr call : findMethodCalls(cu, oldMethod)) {

            if (call.getScope().isEmpty()) {
                continue;
            }

            if (!(call.getScope().get() instanceof NameExpr scope)) {
                continue;
            }

            if (!scope.getNameAsString().equals(oldClass)) {
                continue;
            }
            String className = newClass;
            if(!importName.equals("")){
                className = ensureImportOrGetQualifiedName(cu, importName + "."+newClass);
            }
            scope.setName(className);
    
            call.setName(newMethod);
        }
        for (MethodReferenceExpr ref : cu.findAll(MethodReferenceExpr.class)) {
    String scopeName;

    if (ref.getScope() instanceof TypeExpr typeExpr) {
        scopeName = typeExpr.getType().asString();
    } else if (ref.getScope() instanceof NameExpr nameExpr) {
        scopeName = nameExpr.getNameAsString();
    } else {
        continue;
    }

    if (!scopeName.equals(oldClass))
        continue;

    if (!ref.getIdentifier().equals(oldMethod))
        continue;

    String newSimple = ensureImportOrGetQualifiedName(cu, importName + "."+newClass);

    if (ref.getScope() instanceof TypeExpr typeExpr) {
        typeExpr.setType(StaticJavaParser.parseClassOrInterfaceType(newSimple));
    } else {
        ((NameExpr) ref.getScope()).setName(newSimple);
    }

    ref.setIdentifier(newMethod);
    }

    }
    protected void moveStaticMethod(
        CompilationUnit cu,
        String oldClass,
        String oldMethod,
        String newClass,
        String newMethod
    ){
        moveStaticMethod(cu, oldClass, oldMethod, newClass, newMethod, "");
    }

    protected static String camelToScreamingSnake(String input) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }

            sb.append(Character.toUpperCase(c));
        }

        return sb.toString();
    }
    public static Set<String> getStaticFields(Path javaFile) throws IOException {

    CompilationUnit cu = StaticJavaParser.parse(javaFile);

    Set<String> fields = new HashSet<>();

    for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {

        if (!field.isStatic()) {
            continue;
        }

        for (VariableDeclarator variable : field.getVariables()) {
            fields.add(variable.getNameAsString());
        }
    }

    return fields;
}
    protected static boolean hasStaticField(
        Class<?> clazz,
        String fieldName) {

        try {

            Field field = clazz.getField(fieldName);

            return Modifier.isStatic(field.getModifiers());

        } catch (NoSuchFieldException e) {

            return false;

        }
    }
    protected void rewriteStaticMethod(
            CompilationUnit cu,
            String className,
            String methodName,
            Function<MethodCallExpr, Expression> transformer) {

        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {

            if (call.getScope().isEmpty()) {
                continue;
            }

            Expression scope = call.getScope().get();

            if (!(scope instanceof NameExpr name)) {
                continue;
            }

            if (!name.getNameAsString().equals(className)) {
                continue;
            }

            if (!call.getNameAsString().equals(methodName)) {
                continue;
            }

            Expression replacement = transformer.apply(call);

            if (replacement != null) {
                call.replace(replacement);
            }
        }
    }
protected void renameInstanceMethod(
        CompilationUnit cu,
        MigrationContext context,
        String targetType,
        String oldMethod,
        String newMethod) {

    for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {

        if (!call.getNameAsString().equals(oldMethod)) {
            continue;
        }

        if (call.getScope().isEmpty()) {
            continue;
        }

        if (!context.isExpressionA(call.getScope().get(), targetType)) {
            continue;
        }

        call.setName(newMethod);
    }
}    protected void renameStaticField(
            CompilationUnit cu,
            String className,
            String oldField,
            String newField) {

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {

            if (!(field.getScope() instanceof NameExpr scope)) {
                continue;
            }

            if (!scope.getNameAsString().equals(className)) {
                continue;
            }

            if (!field.getNameAsString().equals(oldField)) {
                continue;
            }

            field.setName(newField);
        }
    }

protected MethodCallExpr staticCall(
        CompilationUnit cu,
        String qualifiedClass,
        String method,
        Expression... args) {

    MethodCallExpr call = new MethodCallExpr(
            new NameExpr(ensureImportOrGetQualifiedName(cu, qualifiedClass)),
            method);

    for (Expression arg : args) {
        call.addArgument(arg.clone());
    }

    return call;
}
public boolean isVariableA(VariableTypes variableTypes, TypeHierarchy typeHierarchy,String variableName, String targetType) {

    String type = variableTypes.get(variableName);

    return typeHierarchy.isSubclassOf(type, targetType);
}



}
