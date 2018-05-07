package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventSpliterator extends ResultSetSpliterator<StorableEvent> {
    private StorableEventSerializer serializer = new StorableEventSerializer();

    public EventSpliterator(ResultSet rs, PreparedStatement stmt, Connection c) {
        super(rs, stmt, c);
    }

    @Override
    protected StorableEvent handleRow(ResultSet rs) throws SQLException {
        return serializer.jsonToEvent(rs.getString("data"));
    }
}
