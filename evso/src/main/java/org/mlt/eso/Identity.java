package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Base class for aggregate identities
 */
public class Identity {
    private final UUID id;

    /**
     * Generate default identity
     */
    public Identity() {
        id = UUID.randomUUID();
    }

    /**
     * Construct from valid UUID string
     *
     * @param id UUID string
     */
    public Identity(String id) {
        this.id = UUID.fromString(id);
    }

    /**
     * Construct from {@link java.util.UUID}
     *
     * @param uuid UUID instance
     */
    public Identity(UUID uuid) {
        this.id = uuid;
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

    /**
     * Get the underlying UUID for this identity
     *
     * @return UUID
     */
    @JsonIgnore
    public UUID getUUID() {
        return id;
    }
}
