package org.mlt.evsosample.infrastructure;

import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.EventStore;
import org.mlt.evsosample.domain.Product;
import org.mlt.evsosample.domain.ProductId;
import org.mlt.evsosample.domain.ProductRepository;

import java.util.List;

public class EventSourcingProductRepository implements ProductRepository {
    private final EventStore store;

    public EventSourcingProductRepository(EventStore store) {
        this.store = store;
    }

    @Override
    public Product findById(ProductId id) {
        List<StorableEvent> events = store.loadEventsForAggregate(id);
        if(events.size()==0) return null;
        EventReplayer replayer = new EventReplayer();
        Product prod = new Product();
        replayer.rehydrate(prod, events);
        if(prod.isDeleted()) return null;
        return prod;
    }
}
