package org.mlt.eso.stores.file;

import org.mlt.eso.Identity;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;
import org.mlt.eso.stores.EventStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileEventStore implements EventStore {
    private static final int DEFAULT_BUFFER_SIZE = 10*1024;
    private EventMemTable memTable;
    private int fileIndex;
    private List<EventSSTable> tables = new ArrayList<>();

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
        List<StorableEvent> events = new ArrayList<>();
        for(EventSSTable table : tables) {
            if(table.isKeyProbablyInTable(new AggregateKey(id.getUUID(), 0))) {
                table.open();
                try {
                    Map<AggregateKey, Long> index = table.deserialize(AggregateKey.class);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } finally {
                    table.close();
                }
            }
        }

        return null;
    }

    @Override
    public List<StorableEvent> loadEventsForAggregate(Identity id, long fromVersion) {
        return null;
    }

    @Override
    public Stream<StorableEvent> loadEventsForAggregateAsStream(Identity id) {
        return null;
    }

    @Override
    public Stream<StorableEvent> loadEventsForAggregateAsStream(Identity id, long fromVersion) {
        return null;
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        return null;
    }

    @Override
    public Stream<StorableEvent> loadEventsAsStream() {
        return null;
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String[] types, int startindex, int count) {
        return null;
    }

    @Override
    public Stream<StorableEvent> loadEventsOfTypeAsStream(String[] types) {
        return null;
    }

    @Override
    public List<StorableEvent> loadEventsOfType(String type, int startindex, int count) {
        return null;
    }

    @Override
    public Stream<StorableEvent> loadEventsOfTypeAsStream(String type) {
        return null;
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        events.forEach((e) -> memTable.put(
                new EventRow(new AggregateKey(e.getAggregateId(), e.getVersion()),
                        serializer.eventToJson(e))));
        if(memTable.isSizeThresholdExceeded()) {
            flushToFile();
        }
    }

    private File newFile() {
        fileIndex++;
        return new File("sstable"+fileIndex);
    }

    private void flushToFile() {
        EventMemTable table = newMemTable();
        EventSSTable ssTable = new EventSSTable(newFile(), 100);
        ssTable.create();
        try {
            table.serialize(ssTable);
            ssTable.close();
            tables.add(ssTable);
        } catch (IOException e) {
            throw new RuntimeException("cannot flush memtable to file", e);
        }
    }
}
