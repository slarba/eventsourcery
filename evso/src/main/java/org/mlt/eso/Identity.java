package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Identity {
    private final UUID id;

    public Identity() {
        id = UUID.randomUUID();
    }

    public Identity(String id) {
        this.id = UUID.fromString(id);
    }

    public Identity(UUID id) {
        this.id = id;
    }

    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Identity identity = (Identity) o;

        return id.equals(identity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @JsonIgnore
    public UUID getUUID() {
        return id;
    }
}
