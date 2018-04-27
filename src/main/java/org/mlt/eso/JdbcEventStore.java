package org.mlt.eso;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcEventStore extends NotifyingEventStore {

    private final DataSource dataSource;

    public JdbcEventStore(DataSource ds) {
        this.dataSource = ds;
    }

    private void executeSql(String sql) {
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

    public void createSchema() {
        executeSql("CREATE TABLE IF NOT EXISTS events("
                    + "id integer identity primary key,"
                    + "aggregateId uuid not null,"
                    + "version integer not null,"
                    + "occurred timestamp not null,"
                    + "type varchar(128) not null,"
                    + "data clob not null"
                    + ")");
        executeSql("CREATE UNIQUE INDEX IF NOT EXISTS event_pk ON events (aggregateId, version)");
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(UUID uuid) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        try {
            Connection c = dataSource.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT data FROM events WHERE aggregateId=? ORDER BY version ASC");
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            List<StorableEvent> events = new ArrayList<>();
            while(rs.next()) {
                events.add(serializer.jsonToEvent(rs.getString("data")));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException("sql error", e);
        }
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        try {
            Connection c = dataSource.getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT data FROM events WHERE id=>? ORDER BY id ASC LIMIT ?");
            stmt.setInt(1, startindex);
            stmt.setInt(2, count);
            ResultSet rs = stmt.executeQuery();
            List<StorableEvent> events = new ArrayList<>();
            while(rs.next()) {
                events.add(serializer.jsonToEvent(rs.getString("data")));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException("sql error", e);
        }
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        try {
            Connection c = dataSource.getConnection();
            PreparedStatement stmt = c.prepareStatement("INSERT INTO events (aggregateId, version, occurred, type, data) VALUES (?,?,?,?,?)");
            for(StorableEvent e : events) {
                stmt.setObject(1, e.getAggregateId());
                stmt.setLong(2, e.getVersion());
                stmt.setTimestamp(3, new Timestamp(e.getOccurred()));
                stmt.setString(4, e.getData().getType());
                stmt.setString(5, serializer.eventToJson(e));
                stmt.execute();
            }
            stmt.close();
            c.close();
            notifyListeners(events);
        } catch(SQLException e) {
            throw new RuntimeException("sql error appending events: ", e);
        }
    }
}
