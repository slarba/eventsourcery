package org.mlt.eso;

import org.junit.Test;
import org.mlt.eso.migration.EventMigration;
import org.mlt.eso.migration.EventMigrator;
import org.mlt.eso.serialization.StorableEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class EventMigrationTest {

    @Test
    public void testEventMigration() {
        Events.registerEventType("NotDeprecatedEvent", NotDeprecatedEvent.class);
        Events.registerEventType("DeprecatedEvent", DeprecatedEvent.class);
        Events.registerEventType("NewEvent1", NewEvent1.class);
        Events.registerEventType("NewEvent2", NewEvent2.class);

        SampleAggregate sample = new SampleAggregate();
        List<StorableEvent> events = Events.collect(() -> {
            Events.dispatch(sample, new NotDeprecatedEvent());
            Events.dispatch(sample, new DeprecatedEvent());
            Events.dispatch(sample, new NotDeprecatedEvent());
        });

        EventMigrator m = new EventMigrator();
        m.registerMigration("DeprecatedEvent", new DeprecatedEventMigration());
        List<StorableEvent> migrated = m.migrate(events);
        assertEquals(4, migrated.size());

        UUID uuid = sample.getId().getUUID();

        assertEquals(new StorableEvent(uuid, 0, 0, new NotDeprecatedEvent()), migrated.get(0));
        assertEquals(new StorableEvent(uuid, 1, 0, new NewEvent1()), migrated.get(1));
        assertEquals(new StorableEvent(uuid, 1, 0, new NewEvent2()), migrated.get(2));
        assertEquals(new StorableEvent(uuid, 2, 0, new NotDeprecatedEvent()), migrated.get(3));
    }

    public class SampleAggregateId extends Identity {}

    public class SampleAggregate extends Aggregate<SampleAggregateId> {
        public SampleAggregate() {
            super(new SampleAggregateId());
        }
    }

    public class NotDeprecatedEvent extends Event {
        @Override
        public boolean equals(Object o) {
            return (o instanceof NotDeprecatedEvent);
        }
    }

    public class DeprecatedEvent extends Event {
    }

    public class NewEvent1 extends Event {
        @Override
        public boolean equals(Object o) {
            return (o instanceof NewEvent1);
        }
    }

    public class NewEvent2 extends Event {
        @Override
        public boolean equals(Object o) {
            return (o instanceof NewEvent2);
        }
    }

    public class DeprecatedEventMigration implements EventMigration<DeprecatedEvent> {
        public List<Event> migrate(Event event) {
            DeprecatedEvent e = (DeprecatedEvent)event;
            return Arrays.asList(new NewEvent1(), new NewEvent2());
        }
    }
}
