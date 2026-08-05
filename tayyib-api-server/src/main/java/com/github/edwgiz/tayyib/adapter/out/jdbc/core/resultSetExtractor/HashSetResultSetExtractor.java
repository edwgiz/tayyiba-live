package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.NonnullResultSetExtractor;
import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.QueryJdbcUtils;
import org.intellij.lang.annotations.Language;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;


@FunctionalInterface
public interface HashSetResultSetExtractor<T> extends NonnullResultSetExtractor<HashSet<T>>, RowMapper<T> {

    static <T> HashSet<T> query(
            final Connection tx, @Language("PostgreSQL") final String sql,
            final HashSetResultSetExtractor<T> resultSetExtractor) {
        return QueryJdbcUtils.query(tx, sql, _ -> {}, resultSetExtractor);
    }


    @Override
    default HashSet<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final var result = new HashSet<T>();
        int rowNum = 0;
        while (rs.next()) {
            result.add(mapRow(rs, rowNum));
            rowNum++;
        }
        return result;
    }
}
