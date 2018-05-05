package org.mlt.eso.stores.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class EventRow extends Row<AggregateKey> {
    public EventRow(AggregateKey key, String data) {
        super(key, data);
    }

    static EventRow deserialize(DataInputStream in) throws IOException {
        AggregateKey k = AggregateKey.serializeFrom(in);
        int l = in.readInt();
        byte[] bs = new byte[l];
        int read = in.read(bs);
        return new EventRow(k, new String(bs, Charset.forName("UTF-8")));
    }
}
