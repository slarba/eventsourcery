package org.mlt.eso;

import java.util.List;

public interface AppendListener {
    void eventsAppended(List<StorableEvent> events);
}
