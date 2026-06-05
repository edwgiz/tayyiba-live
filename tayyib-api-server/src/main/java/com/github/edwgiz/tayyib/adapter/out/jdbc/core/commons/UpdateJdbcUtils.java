package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;

import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.AbstractJdbcUtils.prepareAndSet;


public class UpdateJdbcUtils {

    public static int update(
            @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final Connection tx) {

        try (final var ps = prepareAndSet(sql, preparedStatementSetter, tx)) {
            return ps.executeUpdate();
        } catch (final SQLException ex) {
            throw new UncategorizedSQLException(null, sql, ex);
        }
    }
}
