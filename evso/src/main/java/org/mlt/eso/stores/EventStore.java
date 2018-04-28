package org.mlt.eso.stores;

import org.mlt.eso.serialization.StorableEvent;

import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public interface EventStore {
    List<StorableEvent> loadEventsForAggregate(UUID uuid);
    List<StorableEvent> loadEvents(int startindex, int count);
    List<StorableEvent> loadEventsOfType(String[] types, int startindex, int count);
    void append(List<StorableEvent> events);
}
