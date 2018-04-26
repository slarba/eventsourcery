package org.mlt.eso;

import java.util.*;

/**
 * Created by Marko on 26.4.2018.
 */
public class Events {
    private static final ThreadLocal<Stack<List<StorableEvent>>> events = ThreadLocal.withInitial(Stack::new);
    private static Map<String, String> mapping = new HashMap<>();

    public static List<StorableEvent> collecting(Runnable r) {
        events.get().push(new ArrayList<>());
        try {
            r.run();
        } catch(Throwable t) {
            events.get().pop();
            throw t;
        }
        return events.get().pop();
    }

    public static void dispatch(Aggregate source, Event... e) {
        Stack<List<StorableEvent>> stack = events.get();
        if(stack.isEmpty()) {
            return;
        }
        List<StorableEvent> l = stack.peek();
        for(Event ev : e) {
            l.add(new StorableEvent(source.getId(), source.getVersion(), ev));
            source.bumpVersion();
        }
    }

    public static String classForType(String id) {
        return mapping.get(id);
    }

    public static void registerEventType(String id, Class<?> cls) {
        mapping.put(id, cls.getName());
    }
}
