package migrator;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Represents a single source-code migration.
 */
public interface Migration {

    /**
     * Human-readable name for logging.
     */
    String getName();

    /**
     * Applies this migration to the given compilation unit.
     *
     * @param cu The parsed Java source file.
     * @return true if the file was modified.
     */
    void apply(CompilationUnit cu, MigrationContext context);
}