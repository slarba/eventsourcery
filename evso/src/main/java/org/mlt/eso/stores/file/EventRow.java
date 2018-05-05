package org.mlt.eso.stores.file;

import org.mlt.eso.stores.file.lsm.Row;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class EventRow extends Row<AggregateKey> {
    static Charset charSet = Charset.forName("UTF-8");

    public EventRow(AggregateKey key, String data) {
        super(key, data.getBytes(charSet));
    }

    static EventRow deserialize(DataInputStream in) throws IOException {
        AggregateKey k = AggregateKey.serializeFrom(in);
        int l = in.readInt();
        byte[] bs = new byte[l];
        int read = in.read(bs);
        if(l!=read) {
            throw new RuntimeException("unexpected end of file");
        }
        return new EventRow(k, new String(bs, charSet));
    }
}
