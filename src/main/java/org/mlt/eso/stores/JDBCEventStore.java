package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class JDBCEventStore extends NotifyingEventStore {

    private final JDBCUtil jdbc;

    public JDBCEventStore(DataSource ds) {
        this.jdbc = new JDBCUtil(ds);
    }

    public void createSchema() {
        jdbc.executeSql("CREATE TABLE IF NOT EXISTS events("
                    + "id integer identity primary key,"
                    + "aggregateId uuid not null,"
                    + "version integer not null,"
                    + "occurred timestamp not null,"
                    + "type varchar(128) not null,"
                    + "data clob not null"
                    + ")");
        jdbc.executeSql("CREATE UNIQUE INDEX IF NOT EXISTS event_pk ON events (aggregateId, version)");
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(UUID uuid) {
        return jdbc.withQuery("SELECT data FROM events WHERE aggregateId=? ORDER BY version ASC", (stmt) -> {
            stmt.setObject(1, uuid);
        });
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        return jdbc.withQuery("SELECT data FROM events WHERE id=>? ORDER BY id ASC LIMIT ?", (stmt) -> {
            stmt.setInt(1, startindex);
            stmt.setInt(2, count);
        });
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String[] types, int startindex, int count) {
        return jdbc.withQuery(String.format("SELECT data FROM events WHERE type IN (%s) ORDER BY id ASC LIMIT ? OFFSET ?",
                joinAsStrings(types)), (stmt) -> {
            stmt.setInt(1, count);
            stmt.setInt(2, startindex);
        });
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        jdbc.withUpdate("INSERT INTO events (aggregateId, version, occurred, type, data) VALUES (?,?,?,?,?)", (stmt) -> {
            for(StorableEvent e : events) {
                stmt.setObject(1, e.getAggregateId());
                stmt.setLong(2, e.getVersion());
                stmt.setTimestamp(3, new Timestamp(e.getOccurred()));
                stmt.setString(4, e.getData().getType());
                stmt.setString(5, serializer.eventToJson(e));
                stmt.executeUpdate();
            }
        });
        notifyListeners(events);
    }

    private String joinAsStrings(String[] items) {
        return "'" + String.join("','", items) + "'";
    }
}
