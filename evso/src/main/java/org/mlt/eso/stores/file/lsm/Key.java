package org.mlt.eso.stores.file.lsm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Key<K extends Key> implements Comparable<K> {
    public abstract void serializeTo(DataOutputStream out) throws IOException;
    public abstract int sizeInBytes();
    public abstract void deserialize(DataInputStream in) throws IOException;
}
