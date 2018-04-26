package org.mlt.eso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Marko on 26.4.2018.
 */
public class EventReplayer {
    public void replay(Aggregate ex, List<StorableEvent> events) {
        if(ex.isDeleted()) {
            throw new RuntimeException("attempt to replay events on deleted aggregate");
        }
        try {
            for (StorableEvent event : events) {
                Event data = event.getData();
                try {
                    Method m = ex.getClass().getMethod("on", data.getClass());
                    m.invoke(ex, data);
                    ex.bumpVersion();
                } catch(NoSuchMethodException nsme) {
                    throw new MissingEventHandlerException("no event handler for event "
                            + data.getType() + " (" + data.getClass().getName() + ") in "
                            + ex.getClass().getName(), nsme);
                }
            }
        } catch(InvocationTargetException iv) {
            throw new RuntimeException("event handler error: ", iv);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("illegal access", e);
        }
    }
}
