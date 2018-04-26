package org.mlt.eso;

/**
 * Created by Marko on 26.4.2018.
 */
public class AggregateExample extends Aggregate {
    private int count;
    private String name;

    public AggregateExample() {
    }

    public AggregateExample(int count, String name) {
        super(true);
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

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void delete() {
        setDeleted(true);
        Events.dispatch(this, new AggregateExampleDeleted());
    }
}
