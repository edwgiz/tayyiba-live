package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class StringSqlType {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public final static StringSqlType STR = new StringSqlType();


    private StringSqlType() {
    }


    public static String get(final ResultSet rs, final int columnIndex, StringSqlType ignored) throws SQLException {
        final var result = rs.getString(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("Null value by " + columnIndex + " column index");
        }
        return result;
    }


    public static void set(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
        ps.setString(parameterIndex, value);
    }
}
