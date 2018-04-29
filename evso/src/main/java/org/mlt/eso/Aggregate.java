package org.mlt.eso;

/**
 * Created by Marko on 26.4.2018.
 */
public class Aggregate<T extends Identity> {
    private T id;
    private long version;
    private boolean deleted;

    protected Aggregate() {
    }

    protected Aggregate(T id) {
        this.id = id;
        version = 0;
    }

    public T getId() { return id; }

    public long getVersion() { return version; }

    protected void setId(T id) {
        this.id = id;
    }

    public void bumpVersion() {
        ++version;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
