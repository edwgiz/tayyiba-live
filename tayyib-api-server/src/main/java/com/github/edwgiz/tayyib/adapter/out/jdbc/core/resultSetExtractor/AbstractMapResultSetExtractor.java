package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.NonnullResultSetExtractor;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public interface AbstractMapResultSetExtractor<K, V, R extends Map<K, V>> extends NonnullResultSetExtractor<R> {

    @Override
    default R extractData(final ResultSet rs) throws SQLException {
        final var result = createMap();
        while (rs.next()) {
            mapRow(rs, result);
        }
        return result;
    }


    R createMap();


    void mapRow(ResultSet rs, R result) throws SQLException;
}
