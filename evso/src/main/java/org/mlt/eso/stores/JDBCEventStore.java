package org.mlt.eso.stores;

import org.mlt.eso.Events;
import org.mlt.eso.Identity;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * JDBC event store implementation
 */
public class JDBCEventStore extends NotifyingEventStore {

    private final JDBCUtil jdbc;

    private String insertSql;

    /**
     * Construct from DataSource
     *
     * @param dataSource DataSource
     */
    public JDBCEventStore(DataSource dataSource) {
        this.jdbc = new JDBCUtil(dataSource);
    }

    /**
     * Create event store schema in the database. Does nothing if schema has already been created.
     */
    public void createSchema() {
        if(jdbc.getDbVendor().startsWith("PostgreSQL")) {
            jdbc.executeSql("CREATE TABLE IF NOT EXISTS events("
                    + "id serial primary key,"
                    + "aggregateId uuid not null,"
                    + "version integer not null,"
                    + "occurred timestamp not null,"
                    + "type varchar(64) not null,"
                    + "data jsonb not null"
                    + ")");
            insertSql = "INSERT INTO events (aggregateId, version, occurred, type, data) VALUES (?,?,?,?,?::jsonb)";
        } else {
            jdbc.executeSql("CREATE TABLE IF NOT EXISTS events("
                    + "id integer identity primary key,"
                    + "aggregateId uuid not null,"
                    + "version integer not null,"
                    + "occurred timestamp not null,"
                    + "type varchar(64) not null,"
                    + "data clob not null"
                    + ")");
            insertSql = "INSERT INTO events (aggregateId, version, occurred, type, data) VALUES (?,?,?,?,?)";
        }
        jdbc.executeSql("CREATE UNIQUE INDEX IF NOT EXISTS event_pk ON events (aggregateId, version ASC)");
        jdbc.executeSql("CREATE INDEX IF NOT EXISTS type_idx ON events (type, id ASC)");
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(Identity id) {
        return jdbc.withQuery("SELECT data FROM events WHERE aggregateId=? ORDER BY version ASC", (stmt) -> {
            stmt.setObject(1, id.getUUID());
        });
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(Identity id, long fromVersion) {
        return jdbc.withQuery("SELECT data FROM events WHERE aggregateId=? and version>=? ORDER BY version ASC", (stmt) -> {
            stmt.setObject(1, id.getUUID());
            stmt.setLong(2, fromVersion);
        });
    }

    @Override
    public Stream<StorableEvent> loadEventsForAggregateAsStream(Identity id) {
        EventSpliterator es = jdbc.spliteratorWithQuery("SELECT data FROM events WHERE aggregateId=? ORDER BY version ASC", (stmt) -> {
            stmt.setObject(1, id.getUUID());
        });
        return StreamSupport.stream(es, false);
    }

    @Override
    public Stream<StorableEvent> loadEventsForAggregateAsStream(Identity id, long fromVersion) {
        EventSpliterator es = jdbc.spliteratorWithQuery("SELECT data FROM events WHERE aggregateId=? and version>=? ORDER BY version ASC", (stmt) -> {
            stmt.setObject(1, id.getUUID());
            stmt.setLong(2, fromVersion);
        });
        return StreamSupport.stream(es, false);
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        return jdbc.withQuery("SELECT data FROM events WHERE id=>? ORDER BY id ASC LIMIT ?", (stmt) -> {
            stmt.setInt(1, startindex);
            stmt.setInt(2, count);
        });
    }

    @Override
    public Stream<StorableEvent> loadEventsAsStream() {
        EventSpliterator s = jdbc.spliteratorWithQuery("SELECT data FROM events ORDER BY id ASC", (stmt) -> {});
        return StreamSupport.stream(s, false);
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
    public Stream<StorableEvent> loadEventsOfTypeAsStream(String[] types) {
        EventSpliterator es = jdbc.spliteratorWithQuery(String.format("SELECT data FROM events WHERE type IN (%s) ORDER BY id ASC",
                joinAsStrings(types)), (stmt) -> {});
        return StreamSupport.stream(es, false);
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String type, int startindex, int count) {
        return jdbc.withQuery("SELECT data FROM events WHERE type=? ORDER BY id ASC LIMIT ? OFFSET ?", (stmt) -> {
            stmt.setString(1, type);
            stmt.setInt(2, count);
            stmt.setInt(3, startindex);
        });
    }

    @Override
    public Stream<StorableEvent> loadEventsOfTypeAsStream(String type) {
        EventSpliterator es = jdbc.spliteratorWithQuery(String.format("SELECT data FROM events WHERE type=? ORDER BY id ASC", type), (stmt) -> {});
        return StreamSupport.stream(es, false);
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        jdbc.withUpdate(insertSql, (stmt) -> {
            for(StorableEvent e : events) {
                stmt.setObject(1, e.getAggregateId());
                stmt.setLong(2, e.getVersion());
                stmt.setTimestamp(3, new Timestamp(e.getOccurred()));
                stmt.setString(4, Events.eventTypeForClass(e.getData().getClass().getName()));
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
