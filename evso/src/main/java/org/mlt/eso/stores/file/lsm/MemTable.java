package org.mlt.eso.stores.file.lsm;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class MemTable<K extends Key<K>> {
    private final SortedMap<K, Row<K>> memTable = new TreeMap<>();
    private int totalSize;
    private int sizeThreshold;

    public MemTable(int sizeThreshold) {
        totalSize = 0;
        this.sizeThreshold = sizeThreshold;
    }

    public void put(Row<K> row) {
        memTable.put(row.getKey(), row);
        totalSize += row.dataLength();
    }

    public boolean isSizeThresholdExceeded() {
        return totalSize >= sizeThreshold;
    }

    /**
     * Serializes memtable to a sstable file.
     *
     * Structure of the file:
     *
     * int32  - number of entries
     * // offset table
     * struct {
     *     sizeof(key)  - uuid
     *     int64        - offset of data entry in file
     * }
     * // data table
     * sizeof(row)      - data row, variable size
     *
     * @param table SSTable to serialize to
     * @throws IOException
     */
    public void serialize(SSTable<K> table) throws IOException {
        DataOutputStream out = table.getOutputStream();

        if(memTable.isEmpty()) {
            return;
        }

        // comes in key order
        List<Row<K>> rows = new ArrayList<>(memTable.values());

        // calculate offset table size
        long offsetTableSize = memTable.size()*(rows.get(0).getKey().sizeInBytes() + Long.BYTES);

        // number of entries
        out.writeInt(memTable.size());
        long offset = Integer.BYTES + offsetTableSize;   // account for number of entries

        // write offset table
        for(Row<K> row : rows) {
            table.addKey(row.getKey());
            row.getKey().serializeTo(out);
            out.writeLong(offset);
            offset += row.sizeInBytes();
        }

        for(Row<K> row : rows) {
            row.serialize(out);
        }
    }
}
