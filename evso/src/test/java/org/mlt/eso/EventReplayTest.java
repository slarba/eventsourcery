package org.mlt.eso;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Test;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.JDBCEventStore;
import org.mlt.esotest.*;
import org.mlt.esotest.events.AggregateExampleCreated;
import org.mlt.esotest.events.AggregateExampleDeleted;
import org.mlt.esotest.events.CountIncreasedEvent;
import org.mlt.esotest.events.NameSetEvent;

import javax.sql.DataSource;
import java.util.List;
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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("cannot load hsqldb jdbc driver");
        }

        JDBCDataSource ds = new JDBCDataSource();
        ds.setURL("jdbc:hsqldb:mem:events");
        return ds;
    }

    @Test
    public void testEventReplay() {
        DataSource ds = createHsqlDbDatasource();
        JDBCEventStore eventStore = new JDBCEventStore(ds);
        eventStore.createSchema();

        Events.registerEventType("AggregateExampleCreated", AggregateExampleCreated.class);
        Events.registerEventType("CountIncreased", CountIncreasedEvent.class);
        Events.registerEventType("NameSet", NameSetEvent.class);
        Events.registerEventType("AggregateDeleted", AggregateExampleDeleted.class);

        AggregateRepository repo = new AggregateRepository(eventStore);

        AtomicReference<AggregateId> originalId = new AtomicReference<>();

        List<StorableEvent> events = Events.collect(() -> {
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

        events = Events.collect(() -> {
            result.delete();
            assertTrue(result.isDeleted());
        });

        eventStore.append(events);

        AggregateExample result2 = repo.findById(originalId.get());
        assertNull(result2);
        assertEquals(0, repo.getAggregateCount());

        // check that loading events starting from a spesific version works
        List<StorableEvent> e = eventStore.loadEventsForAggregate(originalId.get(), 3);
        assertEquals(2, e.size());
        assertEquals(new StorableEvent(originalId.get().getUUID(), 3, 0, new CountIncreasedEvent(1)), e.get(0));
        assertEquals(new StorableEvent(originalId.get().getUUID(), 4, 0, new AggregateExampleDeleted()), e.get(1));
        repo.close();
    }
}
