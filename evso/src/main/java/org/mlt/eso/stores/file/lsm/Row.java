package org.mlt.eso.stores.file.lsm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public abstract class Row<K extends Key<K>> implements Comparable<Row<K>> {
    private K key;
    private byte[] data;

    public Row() {
        key = createEmptyKey();
    }

    protected abstract K createEmptyKey();

    public Row(K key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    @Override
    public int compareTo(Row<K> o) {
        return key.compareTo(o.key);
    }

    public void serialize(DataOutputStream out) throws IOException {
        key.serialize(out);
        out.writeInt(data.length);
        out.write(data);
    }

    public void deserialize(DataInputStream in) throws IOException {
        key.deserialize(in);
        int len = in.readInt();
        data = new byte[len];
        in.read(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Row)) return false;
        Row<?> row = (Row<?>) o;
        return Objects.equals(key, row.key) && Arrays.equals(data, row.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, data);
    }

    public K getKey() {
        return key;
    }

    public int sizeInBytes() {
        return key.sizeInBytes() + data.length;
    }

    public int dataLength() {
        return data.length;
    }
}
