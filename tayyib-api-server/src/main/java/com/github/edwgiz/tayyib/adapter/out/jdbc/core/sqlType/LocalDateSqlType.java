package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.SqlTypeUtils.getNonnullObject;


public final class LocalDateSqlType {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public final static LocalDateSqlType LOCAL_DATE = new LocalDateSqlType();


    private LocalDateSqlType() {
    }


    public static LocalDate get(final ResultSet rs, final int columnIndex, final LocalDateSqlType ignored) throws SQLException {
        return getNonnullObject(rs, columnIndex, LocalDate.class);
    }


    public static void set(
            final PreparedStatement ps,
            final int parameterIndex,
            final LocalDate localDate) throws SQLException {
        ps.setObject(parameterIndex, localDate, Types.DATE);
    }


    public static void set(
            final PreparedStatement ps,
            final int parameterIndex,
            final LocalDate[] localDates) throws SQLException {

        final var array = ps.getConnection().createArrayOf("date", localDates);
        ps.setArray(parameterIndex, array);
    }
}
