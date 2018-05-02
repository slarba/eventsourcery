package org.mlt.eso;

import org.mlt.eso.serialization.StorableEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marko on 26.4.2018.
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

    public static void beginCollect() {
        events.get().push(new ArrayList<>());
    }

    public static List<StorableEvent> endCollect() {
        return events.get().pop();
    }

    public static List<StorableEvent> collect(DomainCodeBlock r) {
        beginCollect();
        try {
            r.run();
        } catch(Throwable t) {
            endCollect();
            throw new RuntimeException(t);
        }
        return endCollect();
    }

    public static void dispatch(Aggregate source, Event... e) {
        Stack<List<StorableEvent>> stack = events.get();
        if(stack.isEmpty()) {
            return;
        }
        List<StorableEvent> l = stack.peek();
        for(Event ev : e) {
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

    public static void registerEventType(String id, Class<? extends Event> cls) {
        mapping.put(id, cls.getName());
        reverseMapping.put(cls.getName(), id);
    }

    public static void deregisterEventType(String id) {
        reverseMapping.remove(mapping.get(id));
        mapping.remove(id);
    }
}
