package org.mlt.esotest;

import org.mlt.eso.Aggregate;
import org.mlt.eso.Events;
import org.mlt.esotest.events.AggregateExampleCreated;
import org.mlt.esotest.events.AggregateExampleDeleted;
import org.mlt.esotest.events.CountIncreasedEvent;
import org.mlt.esotest.events.NameSetEvent;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExample extends Aggregate<AggregateId> {
    private int count;
    private String name;

    public AggregateExample() {
    }

    public AggregateExample(int count, String name) {
        super(new AggregateId());
        Events.dispatch(this, new AggregateExampleCreated(getId(), count, name));
    }

    public void setName(String name) {
        this.name = name;
        Events.dispatch(this, new NameSetEvent(name));
    }

    public void increaseCount(int amount) {
        this.count += amount;
        Events.dispatch(this, new CountIncreasedEvent(amount));
    }

    public void delete() {
        setDeleted(true);
        Events.dispatch(this, new AggregateExampleDeleted());
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    // Event handlers

    public void on(AggregateExampleCreated event) {
        setId(event.getId());
        this.count = event.getCount();
        this.name = event.getName();
    }

    public void on(AggregateExampleDeleted event) {
        setDeleted(true);
   }

    public void on(CountIncreasedEvent event) {
        count += event.getCount();
    }

    public void on(NameSetEvent event) {
        this.name = event.getName();
    }
}
