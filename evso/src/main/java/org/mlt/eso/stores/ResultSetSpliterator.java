package org.mlt.eso.stores;

import java.sql.*;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class ResultSetSpliterator<T> implements Spliterator<T> {
    private final ResultSet rs;
    private final Connection conn;
    private final PreparedStatement stmt;

    public ResultSetSpliterator(ResultSet rs, PreparedStatement stmt, Connection connection) {
        this.rs = rs;
        this.conn = connection;
        this.stmt = stmt;
    }

    protected abstract T handleRow(ResultSet rs) throws SQLException;

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            if(rs.next()) {
                action.accept(handleRow(rs));
                return true;
            } else {
                close();
                return false;
            }
        } catch(SQLException sqle) {
            close();
            throw new RuntimeException("sql exception during event streaming", sqle);
        }
    }

    private void close() {
        try {
            rs.close();
            stmt.close();
            conn.close();
        } catch(SQLException sqle) {
            throw new RuntimeException("sql exception closing resultset", sqle);
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return 0;
    }

    @Override
    public int characteristics() {
        return 0;
    }
}
