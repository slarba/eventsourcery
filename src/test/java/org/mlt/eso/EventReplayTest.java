package org.mlt.eso;

import org.hsqldb.jdbc.JDBCDataSource;
import org.hsqldb.jdbc.JDBCPool;
import org.junit.Test;
import org.mlt.esotest.*;
import org.mlt.esotest.events.AggregateExampleCreated;
import org.mlt.esotest.events.AggregateExampleDeleted;
import org.mlt.esotest.events.CountIncreasedEvent;
import org.mlt.esotest.events.NameSetEvent;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Marko on 26.4.2018.
 */
public class EventReplayTest {
    private DataSource createHsqlDbDatasource() {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            JDBCDataSource ds = new JDBCDataSource();
            ds.setURL("jdbc:hsqldb:mem:events");
            return ds;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot load hsqldb jdbc driver");
        }
    }

    @Test
    public void testEventReplay() {
        DataSource ds = createHsqlDbDatasource();
        JdbcEventStore eventStore = new JdbcEventStore(ds);
        eventStore.createSchema();

        Events.registerEventType("AggregateExampleCreated", AggregateExampleCreated.class);
        Events.registerEventType("CountIncreased", CountIncreasedEvent.class);
        Events.registerEventType("NameSet", NameSetEvent.class);
        Events.registerEventType("AggregateDeleted", AggregateExampleDeleted.class);

        AggregateRepository repo = new AggregateRepository(eventStore);

        AtomicReference<UUID> originalId = new AtomicReference<>();

        List<StorableEvent> events = Events.collecting(() -> {
            AggregateExample ex = new AggregateExample(0, "kek");
            assertNotNull(ex.getId());
            originalId.set(ex.getId());
            assertEquals(1, ex.getVersion());

            ex.increaseCount(2);
            assertEquals(2, ex.getVersion());

            ex.setName("foo");
            assertEquals(3, ex.getVersion());

            ex.increaseCount(1);
            assertEquals(4, ex.getVersion());

            assertEquals("foo", ex.getName());
            assertEquals(3, ex.getCount());
        });

        eventStore.append(events);

        final AggregateExample result = repo.findById(originalId.get());

        assertEquals(originalId.get(), result.getId());
        assertEquals(4, result.getVersion());
        assertEquals("foo", result.getName());
        assertEquals(3, result.getCount());
        assertEquals(1, repo.getAggregateCount());

        events = Events.collecting(() -> {
            result.delete();
            assertTrue(result.isDeleted());
        });

        eventStore.append(events);

        AggregateExample result2 = repo.findById(originalId.get());
        assertNull(result2);
        assertEquals(0, repo.getAggregateCount());
    }
}
