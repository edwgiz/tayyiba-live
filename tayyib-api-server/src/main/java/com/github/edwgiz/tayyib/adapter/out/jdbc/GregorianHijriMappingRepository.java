package com.github.edwgiz.tayyib.adapter.out.jdbc;


import com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashMapResultSetExtractor;
import com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.LocalDateSqlType;
import com.github.edwgiz.tayyib.domain.model.HijriMethod;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.UpdateJdbcUtils.update;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.EnumSqlType.set;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.LocalDateSqlType.set;


@Component
public class GregorianHijriMappingRepository {

    public HashMap<LocalDate, LocalDate> findBetween(
            final HijriMethod method,
            final LocalDate gregorianStart,
            final LocalDate gregorianEnd,
            final Connection tx
    ) {
        return HashMapResultSetExtractor.query("""
                        select
                            gregorian,--> 1
                            hijri--> 2
                        from gregorian_hijri_mapping
                        where hijri_method=?::hijri_method--< 1
                          and gregorian between ? and ?--< 2, 3""",
                ps -> {
                    set(ps, 1, method);
                    set(ps, 2, gregorianStart);
                    set(ps, 3, gregorianEnd);
                },
                HashMapResultSetExtractor.toHashMap((rs, result) -> result.put(
                        LocalDateSqlType.get(rs, 1, LocalDateSqlType.LOCAL_DATE),
                        LocalDateSqlType.get(rs, 2, LocalDateSqlType.LOCAL_DATE))),
                tx);
    }

    public void save(
            final HijriMethod method,
            final LocalDate[] gregorian,
            final LocalDate[] hijri,
            final Connection tx) {

        update("""
                        insert into gregorian_hijri_mapping(
                          hijri_method,--< 1
                          gregorian,--< 2
                          hijri)--< 3
                        select ?, unnest(?), unnest(?)
                        on conflict on constraint gregorian_hijri_mapping_pk do update
                        set hijri = excluded.hijri""",
                ps -> {
                    set(ps, 1, method);
                    set(ps, 2, gregorian);
                    set(ps, 3, hijri);
                },
                tx);
    }
}
