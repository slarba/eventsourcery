package org.mlt.eso.stores.file;

import org.junit.Test;

import java.io.*;
import java.util.UUID;

import static org.junit.Assert.*;

public class MemTableTest {
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
        EventRow des = EventRow.deserialize(dis);
        dis.close();
        in.close();

        assertEquals(row, des);
    }
}
