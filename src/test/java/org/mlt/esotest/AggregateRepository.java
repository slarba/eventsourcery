package org.mlt.esotest;

import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.EventStore;
import org.mlt.eso.stores.NotifyingEventStore;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateRepository {
    private EventStore store;
    private final AggregateCounterReadModel countModel;

    public AggregateRepository(NotifyingEventStore store) {
        this.store = store;
        countModel = new AggregateCounterReadModel(store);
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
        return countModel.getCount();
    }
}
