package org.mlt.eso;

import org.junit.Test;
import org.mlt.eso.serialization.StorableEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

/**
 * Created by Marko on 28.4.2018.
 */
public class EventCollectionTest {

    @Test
    public void testEventCollectionOrdering() {
        Aggregate sample = new SampleAggregate();
        List<StorableEvent> events = Events.collect(() -> {
            assertEquals(1, Events.getCollectionNestingLevel());

            Events.dispatch(sample, new SampleEvent(1));
            Events.dispatch(sample, new SampleEvent(2));
            Events.dispatch(sample, new SampleEvent(3));

            // aggregate version should now be 3
            assertEquals(3, sample.getVersion());

            // sub-call to collect should be inedpendent
            List<StorableEvent> subEvents = Events.collect(() -> {
                assertEquals(2, Events.getCollectionNestingLevel());
                Events.dispatch(sample, new SampleEvent(10));
                Events.dispatch(sample, new SampleEvent(20));
            });
            assertEquals(2, subEvents.size());
            assertEquals(new SampleEvent(10), subEvents.get(0).getData());
            assertEquals(new SampleEvent(20), subEvents.get(1).getData());
            // aggregate version should now be 5
            assertEquals(5, sample.getVersion());
            // source of the event should be the aggregate
            assertEquals(sample.getId().getUUID(), subEvents.get(0).getAggregateId());
        });

        assertEquals(3, events.size());
        assertEquals(new SampleEvent(1), events.get(0).getData());
        assertEquals(new SampleEvent(2), events.get(1).getData());
        assertEquals(new SampleEvent(3), events.get(2).getData());
        // source of the event should be the aggregate
        assertEquals(sample.getId().getUUID(), events.get(0).getAggregateId());
        // aggregate version should now be 5
        assertEquals(5, sample.getVersion());
    }

    @Test
    public void testEventCollectionMultiThreadIndependence() throws InterruptedException {
        Aggregate sample = new SampleAggregate();
        AtomicReference<List<StorableEvent>> ref = new AtomicReference<>();
        List<StorableEvent> thread1Events = Events.collect(() -> {
            assertEquals(1, Events.getCollectionNestingLevel());
            Events.dispatch(sample, new SampleEvent(10));
            Thread thread = new Thread(() -> {
                ref.set(Events.collect(() -> {
                    assertEquals(1, Events.getCollectionNestingLevel());
                    Events.dispatch(sample, new SampleEvent(1));
                    Events.dispatch(sample, new SampleEvent(2));
                }));
            });
            thread.start();
            thread.join();
        });
        assertEquals(2, ref.get().size());
        assertEquals(1, thread1Events.size());
    }

    private class SampleId extends Identity {
        public SampleId() {}
    }

    private class SampleAggregate extends Aggregate {
        protected SampleAggregate() { super(new SampleId()); }
    }

    private class SampleEvent extends Event {
        private int exampleField;

        protected SampleEvent() {}

        public SampleEvent(int value) {
            exampleField = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleEvent that = (SampleEvent) o;

            return exampleField == that.exampleField;
        }

        @Override
        public int hashCode() {
            return exampleField;
        }

        @Override
        public String getType() {
            return "SampleEvent";
        }
    }
}
