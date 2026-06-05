package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;


public class EnumSqlType {

    public static void set(final PreparedStatement ps, int parameterIndex, final Enum<?> value) throws SQLException {
        ps.setObject(parameterIndex, value.name(), Types.OTHER);
    }
}
