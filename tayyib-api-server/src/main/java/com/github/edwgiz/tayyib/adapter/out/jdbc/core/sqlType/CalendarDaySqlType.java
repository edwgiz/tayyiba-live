package com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType;


import com.github.edwgiz.tayyib.domain.model.CalendarDay;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public final class CalendarDaySqlType {

    @SuppressWarnings("InstantiationOfUtilityClass")
    public final static CalendarDaySqlType CALENDAR_DAY = new CalendarDaySqlType();


    private CalendarDaySqlType() {
    }


    public static CalendarDay get(final ResultSet rs, final int columnIndex, final CalendarDaySqlType ignored) throws SQLException {
        return new CalendarDay(
                rs.getInt(columnIndex),
                rs.getInt(columnIndex + 1),
                rs.getInt(columnIndex + 2));
    }


    public static void set(
            final PreparedStatement ps,
            final int parameterIndex,
            final CalendarDay calendarDay) throws SQLException {
        ps.setInt(parameterIndex, calendarDay.year());
        ps.setInt(parameterIndex + 1, calendarDay.month());
        ps.setInt(parameterIndex + 2, calendarDay.day());
    }


    public static void set(
            final PreparedStatement ps,
            final int parameterIndex,
            final CalendarDay[] calendarDays) throws SQLException {

        final var years = new Integer[calendarDays.length];
        final var months = new Integer[calendarDays.length];
        final var days = new Integer[calendarDays.length];
        for (int i = 0; i < calendarDays.length; i++) {
            final var localDate = calendarDays[i];
            years[i] = localDate.year();
            months[i] = localDate.month();
            days[i] = localDate.day();
        }
        ps.setArray(parameterIndex, ps.getConnection().createArrayOf("int", years));
        ps.setArray(parameterIndex+1, ps.getConnection().createArrayOf("int", months));
        ps.setArray(parameterIndex+2, ps.getConnection().createArrayOf("int", days));
    }
}
