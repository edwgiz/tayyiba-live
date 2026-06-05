package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;

import org.intellij.lang.annotations.Language;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


abstract class AbstractJdbcUtils {

    static PreparedStatement prepareAndSet(
            @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter, Connection tx) {
        final PreparedStatement ps;
        try {
            ps = tx.prepareStatement(sql);
        } catch (final SQLException ex) {
            throw new BadSqlGrammarException(null, sql, ex);
        }
        try {
            preparedStatementSetter.setValues(ps);
        } catch (final SQLException ex) {
            throw new TypeMismatchDataAccessException("Can't set values on prepared statement", ex);
        }
        return ps;
    }
}
