package org.mlt.esotest.events;

import org.mlt.eso.Event;
import org.mlt.esotest.AggregateId;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExampleCreated extends Event {
    private int count;
    private String name;
    private AggregateId id;

    protected AggregateExampleCreated() {

    }

    public AggregateExampleCreated(AggregateId id, int count, String name) {
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

    public AggregateId getId() {
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
