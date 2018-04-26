package org.mlt.eso;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateRepository {
    private EventStore store;
    private int count = 0;

    public AggregateRepository(EventStore store) {
        this.store = store;
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
}
