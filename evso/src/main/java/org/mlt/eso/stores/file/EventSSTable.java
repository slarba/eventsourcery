package org.mlt.eso.stores.file;

import com.google.common.hash.Funnel;
import org.mlt.eso.stores.file.lsm.SSTable;

import java.io.File;

public class EventSSTable extends SSTable<AggregateKey> {
    public EventSSTable(File file, int expectedElements) {
        super(file, expectedElements);
    }

    @Override
    protected Funnel<AggregateKey> createFunnel() {
        return new AggregateKeyFunnel();
    }
}
