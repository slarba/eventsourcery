package org.mlt.eso.stores.file;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

public class MemTableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testAggregateKeyOrderingAndEquality() {
        UUID id = UUID.randomUUID();
        AggregateKey k1 = new AggregateKey(id, 1);
        AggregateKey k2 = new AggregateKey(id, 2);
        assertTrue(k1.compareTo(k2) < 0);
        assertTrue(k2.compareTo(k1) > 0);
        assertTrue(k1.compareTo(k1)==0);

        AggregateKey a = new AggregateKey(UUID.fromString("f1685911-4938-4993-add5-0682f774590e"), 1);
        AggregateKey b = new AggregateKey(UUID.fromString("11685911-4938-4993-add5-0682f774590e"), 2);

        assertTrue(a.compareTo(b) > 0);
        assertTrue(b.compareTo(a) < 0);

        AggregateKey c = new AggregateKey(UUID.fromString("11685911-4938-4993-add5-0682f774590e"), 2);
        assertNotEquals(a, b);
        assertEquals(b, c);
    }

    @Test
    public void rowSerializationTest() throws IOException {
        EventRow row = new EventRow(new AggregateKey(new UUID(1234,5678), 2), "foobar");
        ByteArrayOutputStream out = new ByteArrayOutputStream(10*1024);
        DataOutputStream dos = new DataOutputStream(out);
        row.serialize(dos);
        dos.close();
        out.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DataInputStream dis = new DataInputStream(in);
        EventRow des = new EventRow();
        des.deserialize(dis);
        dis.close();
        in.close();

        assertEquals(row, des);
    }

    @Test
    public void testSSFileSerialization() throws IOException, InstantiationException, IllegalAccessException {
        EventMemTable memTable = new EventMemTable(200);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        memTable.put(new EventRow(new AggregateKey(id1, 1), "foo"));
        memTable.put(new EventRow(new AggregateKey(id1, 2), "bat"));
        memTable.put(new EventRow(new AggregateKey(id2, 1), "another row"));
        memTable.put(new EventRow(new AggregateKey(id1, 3), "kek!"));

        File tmpFile = folder.newFile();
        EventSSTable ssTable = new EventSSTable(tmpFile, 4);
        ssTable.create();

        memTable.serialize(ssTable);
        ssTable.close();

        ssTable.open();
        Map<AggregateKey, Long> index = ssTable.deserialize(AggregateKey.class);
        assertEquals(4, index.size());
        Set<AggregateKey> keys = index.keySet();
        assertTrue(keys.contains(new AggregateKey(id1, 1)));
        assertTrue(keys.contains(new AggregateKey(id1, 2)));
        assertTrue(keys.contains(new AggregateKey(id1, 3)));
        assertTrue(keys.contains(new AggregateKey(id2, 1)));
        assertFalse(keys.contains(new AggregateKey(id2, 2)));
    }
}
