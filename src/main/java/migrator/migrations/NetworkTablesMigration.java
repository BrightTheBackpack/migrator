package migrator.migrations;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import migrator.AbstractMigration;
import migrator.MigrationContext;

public class NetworkTablesMigration extends AbstractMigration {

    @Override
    public void apply(CompilationUnit cu, MigrationContext context) {
        for (MethodCallExpr call : cu.findAll(MethodCallExpr.class)) {
            if (!call.getNameAsString().equals("sendAll")) {
                continue;
            }

            if (call.getScope().isEmpty()
                    || !call.getScope().get().toString().equals("PubSubOption")) {
                continue;
            }

            boolean sendAll = call.getArguments().isEmpty()
                    || !call.getArgument(0).isBooleanLiteralExpr()
                    || call.getArgument(0).asBooleanLiteralExpr().getValue();

            call.replace(new FieldAccessExpr(
                    new NameExpr("PubSubOption"),
                    sendAll ? "SEND_ALL" : "SEND_CHANGES"));
        }

        for (FieldAccessExpr field : cu.findAll(FieldAccessExpr.class)) {
            String replacement = mapNetworkTableEventKind(field.getNameAsString());
            if (replacement == null) {
                continue;
            }

            String scope = field.getScope().toString();
            if (scope.equals("Kind") || scope.equals("NetworkTableEvent.Kind")) {
                field.setName(replacement);
            }
        }
    }

    private String mapNetworkTableEventKind(String oldName) {
        return switch (oldName) {
            case "kImmediate" -> "IMMEDIATE";
            case "kConnected" -> "CONNECTED";
            case "kDisconnected" -> "DISCONNECTED";
            case "kConnection" -> "CONNECTION";
            case "kPublish" -> "PUBLISH";
            case "kUnpublish" -> "UNPUBLISH";
            case "kProperties" -> "PROPERTIES";
            case "kTopic" -> "TOPIC";
            case "kValueRemote" -> "VALUE_REMOTE";
            case "kValueLocal" -> "VALUE_LOCAL";
            case "kValueAll" -> "VALUE_ALL";
            case "kLogMessage" -> "LOG_MESSAGE";
            case "kTimeSync" -> "TIME_SYNC";
            default -> null;
        };
    }
}
