[![Build Status](https://travis-ci.org/slarba/eventsourcery.svg?branch=master)](https://travis-ci.org/slarba/eventsourcery)

# Introduction

Eventsourcery is a small utility library for event sourcing in Java. It includes a simple JDBC event store,
event collection, replay and migration tools.

# Setting up

Make sure `evso-1.0-SNAPSHOT.jar` is in your classpath.

TODO: publish to Maven repo

# Aggregates and events

Aggregates are domain objects that have an identity and a lifecycle. Their methods (usually) produce events, that can be
used later to reconstruct the aggregate state.

Events are objects that describe what happened. They are serialized as JSON (using Jackson library) to the event store,
so event classes obey Jackson annotations.

Example aggregate with events:

```java
// identity
public class OrderId extends Identity {}

// events
public class OrderCreated extends Event {}
public class OrderAddressUpdated extends Event {}
...

public class Order extends Aggregate<OrderId> {
    private List<OrderLine> orderLines = new ArrayList<>();
    private Address address;

    // always provide default constructor that generates a new identity
    // default constructor must not produce events!
    public Order() {
        super(new OrderId());
    }

    public Order(Address address) {
        this();
        Events.dispatch(this, new OrderCreated(getId()));
        setAddress(address);
    }

    public void setAddress(Address address) {
        this.address = address;
        Events.dispatch(this, new OrderAddressUpdated(getId(), address));
    }

    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        Events.dispatch(this, new OrderLineAdded(getId(), orderLine);
    }

    public void removeOrderLine(int index) {
        if(index<0 || index>=orderLines.size()) {
            throw new IllegalArgumentException("order line index out of range");
        }
        orderLines.remove(index);
        Events.dispatch(this, new OrderLineDeleted(getId(), index);
    }

    public void cancel() {
        setDeleted(true);
        Events.dispatch(this, new OrderCancelled(getId()));
    }

    // event handlers are called when replaying the events to bring aggregate up to its current state
    // never cause side effects outside the aggregate!

    public void on(OrderCreated event) {
        setId(event.getOrderId());
    }

    public void on(OrderAddressUpdated event) {
        this.address = event.getAddress();
    }

    public void on(OrderLineAdded event) {
        this.orderLines.add(event.getOrderLine());
    }

    public void on(OrderLineDeleted event) {
        this.orderLines.remove(event.getOrderLineIndex());
    }

    public void on(OrderCancelled event) {
        setDeleted(true);
    }
}
```

The aggregate id must be derived from `Identity`, events from `Event` and aggregates from `Aggregate` classes provided
by the library. Use `Events.dispatch(aggregate, event)` to dispatch event describing what happened. The event name
should be in the past tense. Event classes must be registered so that event types are (de)serialized correctly:

```java
Events.registerEventType("OrderCreated", OrderCreated.class);
Events.registerEventType("OrderCancelled", OrderCancelled.class);
```

# Collecting and storing events

Events can be collected from the domain code using `Events.collect()` method:

```java
List<StorableEvent> events = Events.collect(() -> {
    // domain code
    Order order = new Order(new Address(...));
    order.addOrderLine(new OrderLine(...));
    order.addOrderLine(new OrderLine(...));
    order.removeOrderLine(1);
    order.cancel();
});
```

`collect()` is thread safe; each thread has its own collection list. The calls can be nested without effecting the outer
`collect()` call.

You can then store the events to provided JDBC event store:

```java
JDBCEventStore eventStore = new JDBCEventStore(dataSource);
eventStore.createSchema();  // creates the required tables and indices if not already exist
eventStore.append(events);
```

If you want to get notified after events have been successfully appended to the event store, you can add a listener:

```java
eventStore.addAppendListener(new AppendListener() {
    @Override
    public void eventsAppended(List<StorableEvent> events) {
        ... read events, update read models ...
    }
});
```

# Loading and replaying events

To 'rehydrate' an aggregate from the event store:

```java
public Order findById(OrderId orderId) {
    List<StorableEvent> events = eventStore.loadEventsForAggregate(id);
    if(events.size()==0) {
        return null;
    }

    // construct empty order
    Order order = new Order();

    // replay events
    EventReplayer replayer = new EventReplayer();
    replayer.rehydrate(order, events);

    // event handlers have now been called and aggregate state is up to date

    // also check if the order has been deleted
    if(order.isDeleted()) {
        return null;
    }

    return order;
}
```

# Constructing read models

Read models filter the event stream by event type and construct the result. Example read model that counts the
number of existing orders:

```java
public class OrderCount implements AppendListener {
    private int count = 0;
    private int eventStreamOffset = 0;
    private EventStore eventStore;

    private static final String[] EVENT_TYPES = new String[] {
        "OrderCreated", "OrderCancelled"
    };

    public OrderCount(EventStore eventStore) {
        this.eventStore = eventStore;
        // catch up from the beginning of the event stream
        catchup();
        // and listen to new append events
        this.eventStore.addAppendListener(this);
    }

    public int getOrderCount() {
        return count;
    }

    public on(OrderCreated event) {
        // the state could be maintained in a database
        ++count;
    }

    public on(OrderDeleted event) {
        --count;
    }

    private void catchup() {
        EventReplayer replayer = new EventReplayer();

        while(true) {
            // read max 100 events at a time
            List<StorableEvent> events = eventStore.loadEventsOfType(EVENT_TYPES, eventStreamOffset, 100);
            replayer.dispatch(this, events);
            eventStreamOffset += events.size();
            if(events.size()<100) {
                // reached the end for this round
                break;
            }
        }
    }

    @Override
    public void eventsAppended(List<StorableEvent> events) {
        // you can use the events passed to the listener, or query the eventstore like this:
        catchup();
    }
}
```

# Event migration

Events inevitably change. To mitigate this, the event replayer has a event migration feature. The idea is to register
a migration for given event type that replaces the deprecated event with 1 to n new events, possibly based on the
old one:

```java
eventReplayer.registerMigration("MyDeprecatedEvent", new EventMigration<MyDeprecatedEvent> {
    @Override
    public List<Event> migrate(Event old) {
       return Arrays.asList(new NewEvent1(old.getSomething()), new NewEvent2(), ...);
    }
});
```

# Concurrency

`Aggregate`-derived classes have a version that is incremented each time the event is dispatched, or replayed. An event
always applies to a certain version of the aggregate that originated the event. In case there are conflicting updates
to the aggregate (both updates result in an event that applies to the same aggregate version), event store refuses to
store the events, effectively rolling back the transaction. This can be used to detect concurrent modifications
(optimistic locking).
