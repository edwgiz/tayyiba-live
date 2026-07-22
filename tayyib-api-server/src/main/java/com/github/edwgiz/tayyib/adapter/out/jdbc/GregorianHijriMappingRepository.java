package com.github.edwgiz.tayyib.adapter.out.jdbc;


import com.github.edwgiz.tayyib.domain.model.CalendarDay;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.UpdateJdbcUtils.update;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashMapResultSetExtractor.query;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashMapResultSetExtractor.toHashMap;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.CalendarDaySqlType.CALENDAR_DAY;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.CalendarDaySqlType.get;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.CalendarDaySqlType.set;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.EnumSqlType.set;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.LocalDateSqlType.LOCAL_DATE;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.LocalDateSqlType.get;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.LocalDateSqlType.set;


@Component
public class GregorianHijriMappingRepository {

    public HashMap<LocalDate, CalendarDay> findBetween(
            final HijriMethod method,
            final LocalDate gregorianStart,
            final LocalDate gregorianEnd,
            final Connection tx
    ) {
        return query(tx, """
                        select
                            gregorian,--> 1
                            hijri_year,hijri_month,hijri_day--> 2,3,4
                        from gregorian_hijri_mapping
                        where hijri_method=?::hijri_method--< 1
                          and gregorian between ? and ?--< 2, 3""",
                ps -> {
                    set(ps, 1, method);
                    set(ps, 2, gregorianStart);
                    set(ps, 3, gregorianEnd);
                },
                toHashMap((rs, result) -> result.put(
                        get(rs, 1, LOCAL_DATE),
                        get(rs, 2, CALENDAR_DAY))));
    }


    public void save(
            final HijriMethod method,
            final LocalDate[] gregorian,
            final CalendarDay[] hijri,
            final Connection tx) {

        update(tx, """
                        insert into gregorian_hijri_mapping(
                          hijri_method,--< 1
                          gregorian,--< 2
                          hijri_year,hijri_month,hijri_day--< 3,4,5
                        )
                        select ?, unnest(?), unnest(?), unnest(?), unnest(?)
                        on conflict on constraint gregorian_hijri_mapping_pk do update
                        set hijri_year = excluded.hijri_year,
                            hijri_month = excluded.hijri_month,
                            hijri_day = excluded.hijri_day""",
                ps -> {
                    set(ps, 1, method);
                    set(ps, 2, gregorian);
                    set(ps, 3, hijri);
                });
    }
}
