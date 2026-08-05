package com.github.edwgiz.tayyib.adapter.out.jdbc;


import com.github.edwgiz.tayyib.adapter.in.dgs.fetcher.CalendarFetcher;
import com.github.edwgiz.tayyib.domain.model.PrayerTimeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Optional;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.QueryJdbcUtils.query;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.NullableStringResultSetExtractor.toNullableString;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.StringSqlType.set;
import static java.util.Optional.empty;


@Component
public class PrayerTimeMethodByCountryRepository {

    private static final Logger log = LoggerFactory.getLogger(CalendarFetcher.class);


    public Optional<PrayerTimeMethod> find(final Connection tx, String countryCode) {
        final var name = query(tx, "select prayer_time_method from prayer_time_method_by_country where iso3166_1_alpha2=?",
                ps -> set(ps, 1, countryCode),
                toNullableString());
        if (name != null) {
            try {
                return Optional.of(PrayerTimeMethod.valueOf(name));
            } catch (IllegalArgumentException _) {
                log.warn("Unsupported PrayerTimeMethod name: {}", name);
            }
        }
        return empty();
    }
}
