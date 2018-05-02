package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;

import java.util.List;

/**
 * Listener interface for successful event store appends
 */
public interface AppendListener {
    /**
     * Called when events have been successfully and durably stored in the event store
     *
     * Useful in read models for listening new events. Register with
     * {@link org.mlt.eso.stores.NotifyingEventStore#addAppendListener(AppendListener)}
     *
     * @param events list of stored events
     */
    void eventsAppended(List<StorableEvent> events);
}
