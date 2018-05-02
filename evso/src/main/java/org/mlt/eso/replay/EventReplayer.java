package org.mlt.eso.replay;

import org.mlt.eso.Aggregate;
import org.mlt.eso.Event;
import org.mlt.eso.Events;
import org.mlt.eso.serialization.StorableEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Marko on 26.4.2018.
 */
public class EventReplayer {
    public void rehydrate(Aggregate ex, List<StorableEvent> events) {
        try {
            for (StorableEvent event : events) {
                if(ex.isDeleted()) {
                    throw new AggregateAlreadyDeletedException("attempt to rehydrate events on deleted aggregate");
                }
                Event data = event.getData();
                try {
                    invokeEventHandler(ex, data);
                    ex.bumpVersion();
                } catch(NoSuchMethodException nsme) {
                    throw new MissingEventHandlerException("no event handler for event "
                            + Events.eventTypeForClass(data.getClass().getName()) + " (" + data.getClass().getName() + ") in "
                            + ex.getClass().getName(), nsme);
                }
            }
        } catch(InvocationTargetException iv) {
            throw new RuntimeException("event handler error: ", iv);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("illegal access", e);
        }
    }

    public void dispatch(Object ex, List<StorableEvent> events) {
        try {
            for (StorableEvent event : events) {
                try {
                    invokeEventHandler(ex, event.getData());
                } catch(NoSuchMethodException nsme) {
                    // ignore
                }
            }
        } catch(InvocationTargetException iv) {
            throw new RuntimeException("event handler error: ", iv);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("illegal access", e);
        }
    }

    private void invokeEventHandler(Object ex, Event data)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ex.getClass().getMethod("on", data.getClass());
        m.invoke(ex, data);
    }
}
