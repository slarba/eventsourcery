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
    private final JDBCEventStore eventStore;
    Map<ProductId, Integer> inventory = new HashMap<>();
    int totalValue = 0;

    public EventSourcingInventory(JDBCEventStore eventStore) {
        this.eventStore = eventStore;
        // catch up state before listening to events
        catchUp();
        eventStore.addAppendListener(this);
    }

    private void catchUp() {
        int i=0;
        EventReplayer replayer = new EventReplayer();
        while(true) {
            List<StorableEvent> events = eventStore.loadEventsOfType(new String[]{"ProductAddedToInventory", "ProductRemovedFromInventory"}, i, 100);
            replayer.dispatch(this, events);
            if(events.size()<100) {
                break;
            }
            i+=events.size();
        }
    }

    public synchronized void on(ProductAddedToInventory event) {
        ProductId id = event.getProductId();
        inventory.put(id, inventory.getOrDefault(id, 0) + event.getAmount());
        totalValue += event.getAmount() * event.getProductUnitPrice();
    }

    public synchronized void on(ProductRemovedFromInventory event) {
        ProductId id = event.getProductId();
        inventory.put(id, inventory.getOrDefault(id, 0) - event.getAmount());
        totalValue -= event.getAmount() * event.getProductUnitPrice();
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

    public int totalValue() {
        return totalValue;
    }
}
