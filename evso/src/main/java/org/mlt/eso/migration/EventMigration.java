package org.mlt.eso.migration;

import org.mlt.eso.Event;

import java.util.List;

public interface EventMigration<T extends Event> {
    List<Event> migrate(Event event);
}
