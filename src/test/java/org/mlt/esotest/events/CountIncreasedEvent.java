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
    public String getType() {
        return "CountIncreased";
    }

}
