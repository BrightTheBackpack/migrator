package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.Type;
import java.util.Set;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class ShuffleboardMigration extends AbstractMigration {

    private static final Set<String> SHUFFLEBOARD_TYPES = Set.of(
            "ComplexWidget",
            "Shuffleboard",
            "ShuffleboardComponent",
            "ShuffleboardContainer",
            "ShuffleboardLayout",
            "ShuffleboardTab",
            "SimpleWidget",
            "SuppliedValueWidget");

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        removeShuffleboardImports(cu);
        removeShuffleboardFields(cu);
        removeShuffleboardMethods(cu);
        removeShuffleboardLocalDeclarations(cu);
        removeShuffleboardStatements(cu, context);
    }

    private void removeShuffleboardImports(CompilationUnit cu) {
        for (ImportDeclaration importDecl : cu.getImports().stream().toList()) {
            if (importDecl.getNameAsString().equals("org.wpilib.shuffleboard")
                    || importDecl.getNameAsString().startsWith("org.wpilib.shuffleboard.")) {
                importDecl.remove();
            }
        }
    }

    private void removeShuffleboardFields(CompilationUnit cu) {
        for (FieldDeclaration field : cu.findAll(FieldDeclaration.class)) {
            if (isShuffleboardType(field.getElementType())) {
                field.remove();
            }
        }
    }

    private void removeShuffleboardMethods(CompilationUnit cu) {
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            if (isShuffleboardType(method.getType())
                    || method.getParameters().stream()
                            .map(Parameter::getType)
                            .anyMatch(this::isShuffleboardType)) {
                method.remove();
            }
        }
    }

    private void removeShuffleboardLocalDeclarations(CompilationUnit cu) {
        for (VariableDeclarationExpr declaration : cu.findAll(VariableDeclarationExpr.class)) {
            if (!isShuffleboardType(declaration.getElementType())) {
                continue;
            }

            if (declaration.getParentNode().isPresent()
                    && declaration.getParentNode().get() instanceof ExpressionStmt statement) {
                statement.remove();
            }
        }
    }

    private void removeShuffleboardStatements(CompilationUnit cu, MigrationContext context) {
        for (ExpressionStmt statement : cu.findAll(ExpressionStmt.class)) {
            if (statement.findAll(MethodCallExpr.class).stream()
                    .anyMatch(call -> isShuffleboardCall(call, context))) {
                statement.remove();
            }
        }
    }

    private boolean isShuffleboardCall(MethodCallExpr call, MigrationContext context) {
        if (call.getScope().isEmpty()) {
            return false;
        }

        Expression scope = call.getScope().get();

        if (scope instanceof NameExpr name
                && name.getNameAsString().equals("Shuffleboard")) {
            return true;
        }

        if (scope instanceof MethodCallExpr scopedCall) {
            return isShuffleboardCall(scopedCall, context);
        }

        return SHUFFLEBOARD_TYPES.stream()
                .anyMatch(type -> context.isExpressionA(scope, type));
    }

    private boolean isShuffleboardType(Type type) {
        String typeName = type.asString();

        if (typeName.startsWith("org.wpilib.shuffleboard.")) {
            return true;
        }

        return SHUFFLEBOARD_TYPES.stream()
                .anyMatch(shuffleboardType -> typeName.equals(shuffleboardType)
                        || typeName.endsWith("." + shuffleboardType));
    }
}
