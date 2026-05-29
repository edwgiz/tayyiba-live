package com.github.edwgiz.tayyib.adapter.out.jdbcClient;


import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class I18nBundleSourcesRepository {

    private final JdbcClient jdbcClient;

    I18nBundleSourcesRepository(final JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Set<String> findAllLocales() {
        return jdbcClient.sql("select distinct locale from i18n_bundles")
                .query(rs -> {
                    var result = new HashSet<String>();
                    while (rs.next()) {
                        result.add(rs.getString(1));
                    }
                    return result;
                });
    }

    public Map<String, Object> findBundleSource(String languageTag) {
        return jdbcClient.sql("select code, value from i18n_bundles where locale=?")
                .param(1, languageTag)
                .query(rs -> {
                    var result = new HashMap<String, Object>(256);
                    while (rs.next()) {
                        result.put(rs.getString(1), rs.getString(2));
                    }
                    return result;
                });
    }
}
