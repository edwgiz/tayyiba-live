package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.QueryJdbcUtils;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Connection;
import java.util.HashMap;


@FunctionalInterface
public interface HashMapResultSetExtractor<K, V> extends AbstractMapResultSetExtractor<K, V, HashMap<K, V>> {

    static <K, V> HashMapResultSetExtractor<K, V> toHashMap(final HashMapResultSetExtractor<K, V> resultSetExtractor) {
        return resultSetExtractor;
    }

    static <K, V> HashMap<K, V> query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final HashMapResultSetExtractor<K, V> resultSetExtractor) {
        return QueryJdbcUtils.query(sql, preparedStatementSetter, resultSetExtractor, tx);
    }

    default HashMap<K, V> createMap() {
        return new HashMap<>();
    }
}
