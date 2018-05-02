package org.mlt.eso;

/**
 * Base class for events
 *
 * Events are serialized to JSON using Jackson library when appending to event store, so all Jackson annotations
 * work as expected. Derived events should implement default constructor too.
 *
 * Event classes must be registered using {@link org.mlt.eso.Events#registerEventType(String, Class)}
 */
public abstract class Event {
}
