package org.mlt.eso;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExampleCreated extends Event {
    private int count;
    private String name;
    private UUID id;

    protected AggregateExampleCreated() {

    }

    public AggregateExampleCreated(UUID id, int count, String name) {
        this.id = id;
        this.count = count;
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String toString() {
        return "created(" + id + "," + name + "," + count + ")";
    }

    @Override
    public String getType() {
        return "AggregateExampleCreated";
    }
}
