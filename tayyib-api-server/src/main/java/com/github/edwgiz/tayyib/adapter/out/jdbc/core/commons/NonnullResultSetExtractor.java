package com.github.edwgiz.tayyib.adapter.out.jdbc.core.commons;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;


@FunctionalInterface
public interface NonnullResultSetExtractor<T> extends ResultSetExtractor<T> {


    @Override
    T extractData(ResultSet rs) throws SQLException, DataAccessException;
}
