package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtil {
    private final DataSource dataSource;
    private String dbVendor;

    public JDBCUtil(DataSource ds) {
        this.dataSource = ds;
        try {
            dbVendor = ds.getConnection().getMetaData().getDatabaseProductName();
        } catch(SQLException e) {
            dbVendor = "generic";
        }
    }

    public String getDbVendor() {
        return dbVendor;
    }

    public void executeSql(String sql) {
        try {
            Connection c = dataSource.getConnection();
            Statement stmt = c.createStatement();
            stmt.execute(sql);
            stmt.close();
            c.close();
        } catch(SQLException e) {
            throw new RuntimeException("error executing sql: ", e);
        }
    }

    public List<StorableEvent> withQuery(String sql, StatementInitializer fn) {
        try {
            Connection c = dataSource.getConnection();
            PreparedStatement stmt = c.prepareStatement(sql);
            fn.apply(stmt);
            List<StorableEvent> result = collectResult(stmt.executeQuery());
            stmt.close();
            c.close();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("error executing sql: ", e);
        }
    }

    public void withUpdate(String sql, StatementInitializer fn) {
        try {
            Connection c = dataSource.getConnection();
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement(sql);
            try {
                fn.apply(stmt);
                c.commit();
            } catch(Throwable t) {
                c.rollback();
            } finally {
                stmt.close();
                c.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("error executing sql: ", e);
        }
    }

    private List<StorableEvent> collectResult(ResultSet rs) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        List<StorableEvent> events = new ArrayList<>();
        try {
            while(rs.next()) {
                events.add(serializer.jsonToEvent(rs.getString("data")));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException("error collecting events from resultset", e);
        }
    }

    public interface StatementInitializer {
        void apply(PreparedStatement stmt) throws SQLException;
    }

}
