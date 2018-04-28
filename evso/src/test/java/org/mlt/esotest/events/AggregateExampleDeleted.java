package org.mlt.esotest.events;

import org.mlt.eso.Event;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExampleDeleted extends Event {
    public AggregateExampleDeleted() {

    }

    @Override
    public String getType() {
        return "AggregateDeleted";
    }
}
