package com.github.edwgiz.tayyib.adapter.out.jdbc.core;

import org.springframework.jdbc.core.ConnectionCallback;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public abstract class JdbcUtils {


    public static <R> R tx(final DataSource dataSource, final ConnectionCallback<R> connectionCallback) {
        try (var c = dataSource.getConnection()) {
            boolean autoCommitBackup = c.getAutoCommit();
            if (autoCommitBackup) {
                c.setAutoCommit(false);
            }
            final R result;
            boolean success = false;
            try {
                result = connectionCallback.doInConnection(c);
                success = true;
            } finally {
                if (success) {
                    c.commit();
                } else {
                    c.rollback();
                }
            }
            if (!autoCommitBackup) {
                c.setAutoCommit(true);
            }
            return result;
        } catch (final SQLException ex) {
            throw new RuntimeException("Can't finish jdbc transaction", ex);
        }
    }

    public static void txWithoutResult(final DataSource dataSource, final Consumer<Connection> connectionCallback) {
        try (var c = dataSource.getConnection()) {
            boolean autoCommitBackup = c.getAutoCommit();
            if (autoCommitBackup) {
                c.setAutoCommit(false);
            }
            boolean success = false;
            try {
                connectionCallback.accept(c);
                success = true;
            } finally {
                if (success) {
                    c.commit();
                } else {
                    c.rollback();
                }
            }
            if (!autoCommitBackup) {
                c.setAutoCommit(true);
            }
        } catch (final SQLException ex) {
            throw new RuntimeException("Can't finish jdbc transaction", ex);
        }
    }
}
