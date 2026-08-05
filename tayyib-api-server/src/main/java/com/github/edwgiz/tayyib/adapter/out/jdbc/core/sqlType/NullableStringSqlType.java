package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;

import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public final class NullableStringSqlType {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public final static NullableStringSqlType NULL_STR = new NullableStringSqlType();


    private NullableStringSqlType() {
    }


    public static @Nullable String get(final ResultSet rs, final int columnIndex, NullableStringSqlType ignored) throws SQLException {
        return rs.getString(columnIndex);
    }


    public static void set(PreparedStatement ps, int parameterIndex, @Nullable String value) throws SQLException {
        ps.setString(parameterIndex, value);
    }
}
