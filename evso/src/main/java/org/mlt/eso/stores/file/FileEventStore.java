package org.mlt.eso.stores.file;

import org.mlt.eso.Identity;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;
import org.mlt.eso.stores.EventStore;

import java.util.List;

public class FileEventStore implements EventStore {
    private static final int DEFAULT_BUFFER_SIZE = 10*1024;
    private EventMemTable memTable;

    public FileEventStore() {
        newMemTable();
    }

    private EventMemTable newMemTable() {
        EventMemTable old = memTable;
        memTable = new EventMemTable(DEFAULT_BUFFER_SIZE);
        return old;
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(Identity id) {
        return null;
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        return null;
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String[] types, int startindex, int count) {
        return null;
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String type, int startindex, int count) {
        return null;
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        events.forEach((e) -> memTable.put(new AggregateKey(e.getAggregateId(), e.getVersion()),
                serializer.eventToJson(e)));
        if(memTable.isSizeThresholdExceeded()) {
            flushToFile();
        }
    }

    private void flushToFile() {
        EventMemTable table = newMemTable();

    }
}
