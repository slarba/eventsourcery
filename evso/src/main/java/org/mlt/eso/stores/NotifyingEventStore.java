package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class NotifyingEventStore implements EventStore {
    private List<AppendListener> listeners = new ArrayList<>();

    public NotifyingEventStore() {
    }

    protected void notifyListeners(List<StorableEvent> events) {
        for(AppendListener l : listeners) {
            l.eventsAppended(events);
        }
    }

    public void addAppendListener(AppendListener listener) {
        listeners.add(listener);
    }

    public void removeAppendListener(AppendListener listener) {
        listeners.remove(listener);
    }
}
