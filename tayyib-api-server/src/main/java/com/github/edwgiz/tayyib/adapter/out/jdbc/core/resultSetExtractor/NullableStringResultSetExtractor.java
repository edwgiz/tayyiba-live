package com.github.edwgiz.tayyib.adapter.out.jdbc.core.resultSetExtractor;

import com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons.NullableResultSetExtractor;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.NullableStringSqlType.NULL_STR;
import static com.github.edwgiz.tayyib.adapter.out.jdbc.core.sqlType.NullableStringSqlType.get;


public final class NullableStringResultSetExtractor implements NullableResultSetExtractor<String> {

    private static final NullableStringResultSetExtractor INSTANCE = new NullableStringResultSetExtractor();


    public static NullableStringResultSetExtractor toNullableString() {
        return INSTANCE;
    }


    private NullableStringResultSetExtractor() {
    }


    public @Nullable String extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            return get(rs, 1, NULL_STR);
        }
        return null;
    }
}
