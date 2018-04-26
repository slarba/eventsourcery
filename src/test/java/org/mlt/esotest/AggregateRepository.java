package org.mlt.esotest;

import org.mlt.eso.*;
import org.mlt.esotest.events.AggregateExampleCreated;
import org.mlt.esotest.events.AggregateExampleDeleted;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateRepository implements AppendListener {
    private EventStore store;
    private int count = 0;

    public AggregateRepository(NotifyingEventStore store) {
        this.store = store;
        store.addAppendListener(this);
    }

    public AggregateExample findById(UUID id) {
        EventReplayer replayer = new EventReplayer();
        List<StorableEvent> storedEvents = store.loadEventsForAggregate(id);
        if(storedEvents.isEmpty()) {
            return null;
        }
        AggregateExample result = new AggregateExample();
        replayer.replay(result, storedEvents);
        return result.isDeleted() ? null : result;
    }

    public int getAggregateCount() {
        return count;
    }

    public void on(AggregateExampleCreated event) {
        count++;
    }

    public void on(AggregateExampleDeleted event) {
        count--;
    }

    @Override
    public void eventsAppended(List<StorableEvent> events) {
        EventReplayer replayer = new EventReplayer();
        replayer.dispatch(this, events);
    }
}
