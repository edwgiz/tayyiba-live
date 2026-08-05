package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;

import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.AbstractJdbcUtils.prepareAndSet;


public abstract class QueryJdbcUtils {

    public static <R> R query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final NonnullResultSetExtractor<R> resultSetExtractor) {

        try (final var ps = prepareAndSet(sql, preparedStatementSetter, tx)) {
            try (final var rs = ps.executeQuery()) {
                return resultSetExtractor.extractData(rs);
            }
        } catch (final SQLException ex) {
            throw new UncategorizedSQLException(ex.getMessage(), sql, ex);
        }
    }


    public static <R> @Nullable R query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final NullableResultSetExtractor<R> resultSetExtractor) {

        try (final var ps = prepareAndSet(sql, preparedStatementSetter, tx)) {
            try (final var rs = ps.executeQuery()) {
                return resultSetExtractor.extractData(rs);
            }
        } catch (final SQLException ex) {
            throw new UncategorizedSQLException(ex.getMessage(), sql, ex);
        }
    }
}
