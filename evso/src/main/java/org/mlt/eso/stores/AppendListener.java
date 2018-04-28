package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;

import java.util.List;

public interface AppendListener {
    void eventsAppended(List<StorableEvent> events);
}
