package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;

import java.sql.ResultSet;
import java.sql.SQLException;


public final class SqlTypeUtils {

    public static <T> T getNonnullObject(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException {
        final var result = rs.getObject(columnIndex, clazz);
        if (rs.wasNull()) {
            throw new NullPointerException("Null value by " + columnIndex + " column index");
        }
        return result;
    }
}
