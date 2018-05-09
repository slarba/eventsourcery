package org.mlt.eso.stores.file.lsm;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class SSTable<K extends Key<K>> {
    private File file;
    private BloomFilter<K> bloomFilter;
    private DataOutputStream out;
    private DataInputStream in;

    public SSTable(File file, int expectedElements) {
        this.file = file;
        this.bloomFilter = BloomFilter.create(createFunnel(), expectedElements);
    }

    protected abstract Funnel<K> createFunnel();

    public void create() {
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("cannot create sstable file", e);
        }
    }

    public void open() {
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        } catch(FileNotFoundException fnf) {
            throw new RuntimeException("sstable file not found", fnf);
        }
    }

    public boolean isKeyProbablyInTable(K key) {
        return bloomFilter.mightContain(key);
    }

    public DataInputStream getInputStream() {
        return in;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

    public void close() {
        try {
            if(out!=null) {
                out.close();
                out = null;
            }
            if(in!=null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            throw new RuntimeException("error closing sstable file", e);
        }
    }

    public void addKey(K key) {
        bloomFilter.put(key);
    }

    public Map<K, Long> deserialize(Class<K> keyClass) throws IOException, IllegalAccessException, InstantiationException {
        int count = in.readInt();
        Map<K, Long> index = new HashMap<>();
        for(int i=0; i<count; i++) {
            K key = keyClass.newInstance();
            key.deserialize(in);
            long offset = in.readLong();
            index.put(key, offset);
        }
        return index;
    }
}
