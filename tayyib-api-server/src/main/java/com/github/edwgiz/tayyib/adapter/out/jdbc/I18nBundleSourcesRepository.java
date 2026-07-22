package com.github.edwgiz.tayyib.adapter.out.jdbc;


import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashMapResultSetExtractor.query;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashMapResultSetExtractor.toHashMap;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor.HashSetResultSetExtractor.query;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.StringSqlType.STR;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.StringSqlType.get;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.StringSqlType.set;


@Component
public class I18nBundleSourcesRepository {

    public Set<String> findAllLocales(final Connection tx) {
        return query(tx, "select distinct locale from i18n_bundles",
                (rs, _) -> get(rs, 1, STR)
        );
    }


    public HashMap<String, Object> findBundleSource(final String languageTag, final Connection tx) {
        return query(tx, "select code, value from i18n_bundles where locale=?",
                ps -> set(ps, 1, languageTag),
                toHashMap((rs, result) -> result.put(
                        get(rs, 1, STR),
                        get(rs, 2, STR)))
        );
    }
}
