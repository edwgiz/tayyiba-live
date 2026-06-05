package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;

import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.AbstractJdbcUtils.prepareAndSet;


public abstract class QueryJdbcUtils {

    public static <R> R query(
            @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final ResultSetExtractor<R> resultSetExtractor,
            final Connection tx) {

        try (final var ps = prepareAndSet(sql, preparedStatementSetter, tx)) {
            try (final var rs = ps.executeQuery()) {
                return resultSetExtractor.extractData(rs);
            }
        } catch (final SQLException ex) {
            throw new UncategorizedSQLException(null, sql, ex);
        }
    }
}
