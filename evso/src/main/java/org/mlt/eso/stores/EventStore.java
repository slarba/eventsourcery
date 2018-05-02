package org.mlt.eso.stores;

import org.mlt.eso.Identity;
import org.mlt.eso.serialization.StorableEvent;

import java.util.List;
import java.util.UUID;

/**
 * Interface for event store implementations
 */
public interface EventStore {
    /**
     * Load all events occurred to given aggregate, in order of occurrence
     *
     * @param id aggregate identity for loading aggregate's events
     * @return list of stored events
     */
    List<StorableEvent> loadEventsForAggregate(Identity id);

    /**
     * Load upto count events starting from given index. Useful for paging through all events produced
     *
     * @param startindex 0-based start index
     * @param count maximum number of events to load
     * @return list of stored events. if size()==count, there's probably more events in the store
     */
    List<StorableEvent> loadEvents(int startindex, int count);

    /**
     * Filter event stream for spesific types of events
     *
     * @param types array of event names to filter for
     * @param startindex starting index (in the filtered list i.e. not all events in the store)
     * @param count maximum number of events to return
     * @return list of stored events. if size()==count, there's probably more events in the store
     */
    List<StorableEvent> loadEventsOfType(String[] types, int startindex, int count);

    /**
     * Similar to {@link #loadEventsOfType(String[], int, int)} but for one event type
     *
     * @see #loadEventsOfType(String[], int, int)
     * @param type name of event to filter for
     * @param startindex starting index
     * @param count max. number of events to load
     * @return list of stored events
     */
    List<StorableEvent> loadEventsOfType(String type, int startindex, int count);

    /**
     * Append collected events to event store.
     *
     * @param events list of collected, storable events
     */
    void append(List<StorableEvent> events);
}
