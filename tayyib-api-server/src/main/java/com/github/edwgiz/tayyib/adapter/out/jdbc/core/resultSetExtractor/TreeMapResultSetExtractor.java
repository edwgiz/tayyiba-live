package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.QueryJdbcUtils;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.Connection;
import java.util.TreeMap;


@FunctionalInterface
public interface TreeMapResultSetExtractor<K extends Comparable<K>, V>
        extends AbstractMapResultSetExtractor<K, V, TreeMap<K, V>> {

    static <K extends Comparable<K>, V> TreeMapResultSetExtractor<K, V> toTreeMap(final TreeMapResultSetExtractor<K, V> resultSetExtractor) {
        return resultSetExtractor;
    }


    static <K extends Comparable<K>, V> TreeMap<K, V> query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final TreeMapResultSetExtractor<K, V> resultSetExtractor) {
        return QueryJdbcUtils.query(tx, sql, preparedStatementSetter, resultSetExtractor);
    }


    default TreeMap<K, V> createMap() {
        return new TreeMap<>();
    }
}
