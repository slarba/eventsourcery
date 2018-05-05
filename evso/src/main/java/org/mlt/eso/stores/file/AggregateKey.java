package org.mlt.eso.stores.file;

import org.mlt.eso.stores.file.lsm.Key;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class AggregateKey extends Key<AggregateKey> {
    private UUID id;
    private long version;

    public AggregateKey(UUID aggregateId, long version) {
        this.id = aggregateId;
        this.version = version;
    }

    @Override
    public int compareTo(AggregateKey o) {
        if(id.equals(o.id)) {
            return (int)(version - o.version);
        } else {
            return -id.compareTo(o.id); // UUID compareTo is reversed!
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateKey that = (AggregateKey) o;
        return version == that.version &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, version);
    }

    @Override
    public void serializeTo(DataOutputStream out) throws IOException {
        out.writeLong(version);
        out.writeLong(id.getLeastSignificantBits());
        out.writeLong(id.getMostSignificantBits());
    }

    @Override
    public int sizeInBytes() {
        return 3*Long.BYTES;
    }

    public static AggregateKey serializeFrom(DataInputStream in) throws IOException {
        long ver = in.readLong();
        long lsb = in.readLong();
        long msb = in.readLong();
        return new AggregateKey(new UUID(msb, lsb), ver);
    }

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
}
