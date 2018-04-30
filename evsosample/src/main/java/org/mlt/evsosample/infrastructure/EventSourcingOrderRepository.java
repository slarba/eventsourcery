package org.mlt.evsosample.infrastructure;

import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.EventStore;
import org.mlt.evsosample.domain.Order;
import org.mlt.evsosample.domain.OrderId;
import org.mlt.evsosample.domain.OrderRepository;

import java.util.List;

public class EventSourcingOrderRepository implements OrderRepository {
    private final EventStore store;

    public EventSourcingOrderRepository(EventStore store) {
        this.store = store;
    }

    @Override
    public Order findById(OrderId id) {
        List<StorableEvent> events = store.loadEventsForAggregate(id);
        if(events.size()==0) return null;
        EventReplayer replayer = new EventReplayer();
        Order prod = new Order();
        replayer.rehydrate(prod, events);
        if(prod.isDeleted()) return null;
        return prod;
    }
}
