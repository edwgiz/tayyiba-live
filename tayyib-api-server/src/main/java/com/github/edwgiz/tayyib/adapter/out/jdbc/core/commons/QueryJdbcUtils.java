package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;

import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.AbstractJdbcUtils.prepareAndSet;


public abstract class QueryJdbcUtils {

    public static <R> R query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final NonnullResultSetExtractor<R> resultSetExtractor) {

        R result = query(tx, sql, preparedStatementSetter, (ResultSetExtractor<R>) resultSetExtractor);
        assert result != null;
        return result;
    }


    public static <R> @Nullable R query(
            final Connection tx,
            final @Language("PostgreSQL") String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final ResultSetExtractor<@Nullable R> resultSetExtractor) {
        try (final var ps = prepareAndSet(sql, preparedStatementSetter, tx)) {
            try (final var rs = ps.executeQuery()) {
                return resultSetExtractor.extractData(rs);
            }
        } catch (final SQLException ex) {
            throw new UncategorizedSQLException(ex.getMessage(), sql, ex);
        }
    }
}
