package org.mlt.eso;

import org.junit.Test;
import org.mlt.eso.replay.AggregateAlreadyDeletedException;
import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.replay.MissingEventHandlerException;
import org.mlt.eso.serialization.StorableEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Marko on 28.4.2018.
 */
public class EventReplayerTest {
    @Test
    public void testEventReplay() {
        SampleAggregate sample = new SampleAggregate();
        EventReplayer replayer = new EventReplayer();
        List<StorableEvent> events = Arrays.asList(
                toStorableEvent(new SampleEvent1()),
                toStorableEvent(new SampleEvent2()));
        replayer.rehydrate(sample, events);
        assertEquals(1, sample.event1Called);
        assertEquals(2, sample.event2Called);
        assertEquals(2, sample.getVersion());
    }

    @Test(expected = AggregateAlreadyDeletedException.class)
    public void testEventReplayToDeletedAggregate() {
        SampleAggregate sample = new SampleAggregate();
        EventReplayer replayer = new EventReplayer();
        List<StorableEvent> events = Arrays.asList(
                toStorableEvent(new SampleEvent1()),
                toStorableEvent(new SampleDeleteEvent()),
                toStorableEvent(new SampleEvent2()));
        replayer.rehydrate(sample, events);
    }

    @Test(expected = MissingEventHandlerException.class)
    public void testEventReplayWithMissingHandler() {
        SampleAggregate sample = new SampleAggregate();
        EventReplayer replayer = new EventReplayer();
        List<StorableEvent> events = Arrays.asList(toStorableEvent(new SampleEvent3()));
        replayer.rehydrate(sample, events);
        assertEquals(0, sample.event1Called);
        assertEquals(0, sample.event2Called);
        assertEquals(0, sample.getVersion());
    }

    private StorableEvent toStorableEvent(Event e) {
        // not interested in source aggregate uuid, use random
        return new StorableEvent(UUID.randomUUID(), 0, System.currentTimeMillis(), e);
    }

    public class SampleId extends Identity {
        public SampleId() {}
    }

    public class SampleAggregate extends Aggregate {
        public int event1Called;
        public int event2Called;
        public int counter = 1;

        public SampleAggregate() {
            super(new SampleId());
        }

        public void on(SampleEvent1 event) {
            event1Called = counter++;
        }
        public void on(SampleEvent2 event) {
            event2Called = counter++;
        }
        public void on(SampleDeleteEvent event) {
            setDeleted(true);
        }
    }

    private class SampleEvent1 extends Event {
        @Override
        public String getType() {
            return "Sample1";
        }
    }
    private class SampleEvent2 extends Event {
        @Override
        public String getType() {
            return "Sample2";
        }
    }
    private class SampleEvent3 extends Event {
        @Override
        public String getType() {
            return "Sample3";
        }
    }
    private class SampleDeleteEvent extends Event {
        @Override
        public String getType() {
            return "Delete";
        }
    }
}
