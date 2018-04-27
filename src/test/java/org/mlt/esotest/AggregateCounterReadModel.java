package org.mlt.esotest;

import org.mlt.eso.stores.AppendListener;
import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.stores.NotifyingEventStore;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.esotest.events.AggregateExampleCreated;
import org.mlt.esotest.events.AggregateExampleDeleted;

import java.util.List;

public class AggregateCounterReadModel implements AppendListener {
    private final NotifyingEventStore store;
    private int streamOffset = 0;

    private int count = 0;

    public AggregateCounterReadModel(NotifyingEventStore store) {
        this.store = store;
        store.addAppendListener(this);
    }

    public void on(AggregateExampleCreated event) {
        count++;
    }

    public void on(AggregateExampleDeleted event) {
        count--;
    }

    @Override
    public void eventsAppended(List<StorableEvent> events) {
        List<StorableEvent> evts = store.loadEventsOfType(
                new String[] { "AggregateExampleCreated", "AggregateDeleted" },
                streamOffset, 100);
        streamOffset += evts.size();
        EventReplayer replayer = new EventReplayer();
        replayer.dispatch(this, evts);
    }

    public int getCount() {
        return count;
    }
}
