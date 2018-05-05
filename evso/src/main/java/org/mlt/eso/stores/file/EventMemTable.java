package org.mlt.eso.stores.file;

import org.mlt.eso.stores.file.lsm.MemTable;

public class EventMemTable extends MemTable<AggregateKey> {
    public EventMemTable(int sizeThreshold) {
        super(sizeThreshold);
    }
}
