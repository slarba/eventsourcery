package org.mlt.eso.stores.file;

public class EventMemTable extends MemTable<AggregateKey> {
    public EventMemTable(int sizeThreshold) {
        super(sizeThreshold);
    }
}
