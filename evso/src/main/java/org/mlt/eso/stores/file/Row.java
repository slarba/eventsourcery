package org.mlt.eso.stores.file;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Row<K extends Key<K>> implements Comparable<Row<K>> {
    private K key;
    private String data;

    public Row(K key, String data) {
        this.key = key;
        this.data = data;
    }

    @Override
    public int compareTo(Row<K> o) {
        return key.compareTo(o.key);
    }

    public void serialize(DataOutputStream out) throws IOException {
        key.serializeTo(out);
        byte[] bytes = data.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.writeBytes(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Row)) return false;
        Row<?> row = (Row<?>) o;
        return Objects.equals(key, row.key) && Objects.equals(data, row.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, data);
    }

    public K getKey() {
        return key;
    }

    public int sizeInBytes() {
        return key.sizeInBytes() + data.length();
    }
}
