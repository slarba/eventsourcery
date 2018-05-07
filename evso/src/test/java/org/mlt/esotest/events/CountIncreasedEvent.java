package org.mlt.esotest.events;

import org.mlt.eso.Event;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class CountIncreasedEvent extends Event {
    private int count;

    protected CountIncreasedEvent() {

    }

    public CountIncreasedEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountIncreasedEvent)) return false;

        CountIncreasedEvent that = (CountIncreasedEvent) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }
}
