package org.mlt.evsosample.infrastructure;

import org.mlt.eso.replay.EventReplayer;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.AppendListener;
import org.mlt.eso.stores.JDBCEventStore;
import org.mlt.evsosample.domain.InventoryItem;
import org.mlt.evsosample.domain.ProductAddedToInventory;
import org.mlt.evsosample.domain.ProductId;
import org.mlt.evsosample.domain.ProductRemovedFromInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSourcingInventory implements AppendListener {
    Map<ProductId, Integer> inventory = new HashMap<>();

    public EventSourcingInventory(JDBCEventStore eventStore) {
        eventStore.addAppendListener(this);
    }

    public synchronized void on(ProductAddedToInventory event) {
        ProductId id = event.getProductId();
        inventory.put(id, inventory.getOrDefault(id, 0) + event.getAmount());
    }

    public synchronized void on(ProductRemovedFromInventory event) {
        ProductId id = event.getProductId();
        inventory.put(id, inventory.getOrDefault(id, 0) - event.getAmount());
    }

    public synchronized List<InventoryItem> listAll() {
        List<InventoryItem> items = new ArrayList<>();
        for(ProductId id : inventory.keySet()) {
            items.add(new InventoryItem(id, inventory.get(id)));
        }
        return items;
    }

    @Override
    public void eventsAppended(List<StorableEvent> events) {
        EventReplayer replayer = new EventReplayer();
        replayer.dispatch(this, events);
    }
}
