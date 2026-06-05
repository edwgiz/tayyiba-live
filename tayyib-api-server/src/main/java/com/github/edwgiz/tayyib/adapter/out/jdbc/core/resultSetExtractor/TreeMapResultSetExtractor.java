package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.QueryJdbcUtils;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.Connection;
import java.util.TreeMap;


@FunctionalInterface
public interface TreeMapResultSetExtractor<K extends Comparable<K>, V>
        extends AbstractMapResultSetExtractor<K, V, TreeMap<K, V>> {

    static <K extends Comparable<K>, V> TreeMapResultSetExtractor<K, V> toTreeMap(final TreeMapResultSetExtractor<K, V> resultSetExtractor) {
        return resultSetExtractor;
    }


    static <K extends Comparable<K>, V> TreeMap<K, V> query(
            @Language("PostgreSQL") final String sql,
            final PreparedStatementSetter preparedStatementSetter,
            final TreeMapResultSetExtractor<K, V> resultSetExtractor,
            final Connection tx) {
        return QueryJdbcUtils.query(sql, preparedStatementSetter, (ResultSetExtractor<TreeMap<K, V>>) resultSetExtractor, tx);
    }


    default TreeMap<K, V> createMap() {
        return new TreeMap<>();
    }
}
