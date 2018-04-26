package org.mlt.eso;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class Aggregate {
    private UUID id;
    private long version;
    private boolean deleted;

    protected Aggregate() {

    }

    protected Aggregate(boolean init) {
        this.id = UUID.randomUUID();
        version = 0;
    }

    public UUID getId() { return id; }

    public long getVersion() { return version; }

    protected void setId(UUID id) {
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
