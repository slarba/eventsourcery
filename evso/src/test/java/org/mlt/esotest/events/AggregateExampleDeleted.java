package org.mlt.esotest.events;

import org.mlt.eso.Event;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExampleDeleted extends Event {
    public AggregateExampleDeleted() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateExampleDeleted)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
