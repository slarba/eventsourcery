package org.mlt.eso.stores.file;

import org.mlt.eso.stores.file.lsm.Row;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class EventRow extends Row<AggregateKey> {
    static Charset charSet = Charset.forName("UTF-8");

    public EventRow() {}

    public EventRow(AggregateKey key, String data) {
        super(key, data.getBytes(charSet));
    }

    @Override
    protected AggregateKey createEmptyKey() {
        return new AggregateKey();
    }
}
