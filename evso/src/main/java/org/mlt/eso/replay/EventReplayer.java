package org.mlt.eso.replay;

import org.mlt.eso.Aggregate;
import org.mlt.eso.Event;
import org.mlt.eso.Events;
import org.mlt.eso.migration.EventMigration;
import org.mlt.eso.migration.EventMigrator;
import org.mlt.eso.serialization.StorableEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Replays a list of events on aggregate.
 *
 * Aggregate should implement on-methods with event class as its only argument, for example:
 * {@code public void on(OrderCreated event) {
 *
 * }}
 */
public class EventReplayer {
    private EventMigrator migrator = new EventMigrator();

    /**
     * Register an event migration.
     *
     * Events evolve. Event migrations allow mangling of the event stream on the fly to upgrade old events.
     *
     * @param deprecatedEventName name of the deprecated event
     * @param migration instance of {@link EventMigration} that handles event migration
     */
    public void registerMigration(String deprecatedEventName, EventMigration<? extends Event> migration) {
        migrator.registerMigration(deprecatedEventName, migration);
    }

    /**
     * Rehydrate an aggregate from the given event list. Event handlers are called using reflection.
     *
     * @throws MissingEventHandlerException if no event handler is found
     * @throws AggregateAlreadyDeletedException if aggregate is in deleted state {@link Aggregate#isDeleted()}
     * @param aggregate (empty) aggregate instance to be reconstituted
     * @param events list of events loaded from event store
     */
    public void rehydrate(Aggregate aggregate, List<StorableEvent> events) {
        migrator.migrateStream(events).forEach((event) -> {
            try {
                if(aggregate.isDeleted()) {
                    throw new AggregateAlreadyDeletedException("attempt to rehydrate events on deleted aggregate");
                }
                Event data = event.getData();
                try {
                    invokeEventHandler(aggregate, data);
                    aggregate.bumpVersion();
                } catch(NoSuchMethodException nsme) {
                    throw new MissingEventHandlerException("no event handler for event "
                            + Events.eventTypeForClass(data.getClass().getName()) + " (" + data.getClass().getName() + ") in "
                            + aggregate.getClass().getName(), nsme);
                }
            } catch(InvocationTargetException iv) {
                throw new RuntimeException("event handler error: ", iv);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("illegal access", e);
            }
        });
    }

    /**
     * Dispatch events to any object. Missing handlers are ignored; useful for filtering through all events
     *
     * @param object any object implementing some event handlers
     * @param events list of events loaded from event store
     */
    public void dispatch(Object object, List<StorableEvent> events) {
        migrator.migrateStream(events).forEach((event) -> {
            try {
                try {
                    invokeEventHandler(object, event.getData());
                } catch(NoSuchMethodException nsme) {
                    // ignore
                }
            } catch(InvocationTargetException iv) {
                throw new RuntimeException("event handler error: ", iv);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("illegal access", e);
            }
        });
    }

    private void invokeEventHandler(Object ex, Event data)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ex.getClass().getMethod("on", data.getClass());
        m.invoke(ex, data);
    }
}
