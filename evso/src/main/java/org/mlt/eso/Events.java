package org.mlt.eso;

import org.mlt.eso.serialization.StorableEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Dispatch and collect events
 *
 * Events are collected per-thread basis in a thread-local list. Collections can be nested without effecting
 * the outer collection (the inner collection starts with an empty list).
 */
public class Events {
    private static final ThreadLocal<Stack<List<StorableEvent>>> events = ThreadLocal.withInitial(Stack::new);
    private static Map<String, String> mapping = new ConcurrentHashMap<>();
    private static Map<String, String> reverseMapping = new ConcurrentHashMap<>();

    public static String eventTypeForClass(String className) {
        return reverseMapping.get(className);
    }

    public interface DomainCodeBlock {
        void run() throws Throwable;
    }

    /**
     * Start collecting events
     */
    public static void beginCollect() {
        events.get().push(new ArrayList<>());
    }

    /**
     * Stop collecting events
     *
     * @return list of storable events
     */
    public static List<StorableEvent> endCollect() {
        return events.get().pop();
    }

    /**
     * Collect events dispatched within a block
     *
     * If block throws an exception, events collected so far are discarded and exception is rethrown
     *
     * @param codeBlock block of domain code
     * @return list of storable events
     */
    public static List<StorableEvent> collect(DomainCodeBlock codeBlock) {
        beginCollect();
        try {
            codeBlock.run();
        } catch(Throwable t) {
            endCollect();
            throw new RuntimeException(t);
        }
        return endCollect();
    }

    /**
     * Dispatch an event from domain code
     *
     * @param source aggregate that generated this event
     * @param events array of events to be dispatched, in order
     */
    public static void dispatch(Aggregate source, Event... events) {
        Stack<List<StorableEvent>> stack = Events.events.get();
        if(stack.isEmpty()) {
            return;
        }
        List<StorableEvent> l = stack.peek();
        for(Event ev : events) {
            l.add(new StorableEvent(source.getId().getUUID(), source.getVersion(), System.currentTimeMillis(), ev));
            source.bumpVersion();
        }
    }

    static int getCollectionNestingLevel() {
        return events.get().size();
    }

    public static String classForEventType(String id) {
        return mapping.get(id);
    }

    /**
     * Register event class with event name
     *
     * Event name is used in serialization/deserialization to JSON. This mechanism allows switching of the event
     * implementation.
     *
     * @param eventName Event name
     * @param cls Event class
     */
    public static void registerEventType(String eventName, Class<? extends Event> cls) {
        mapping.put(eventName, cls.getName());
        reverseMapping.put(cls.getName(), eventName);
    }

    /**
     * Remove event registration
     *
     * @param eventName Event name
     */
    public static void deregisterEventType(String eventName) {
        reverseMapping.remove(mapping.get(eventName));
        mapping.remove(eventName);
    }
}
