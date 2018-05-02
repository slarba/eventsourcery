package org.mlt.eso.migration;

import org.mlt.eso.Event;
import org.mlt.eso.Events;
import org.mlt.eso.serialization.StorableEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventMigrator {
    private Map<String, EventMigration<? extends Event>> migrations = new HashMap<>();

    public void registerMigration(String deprecatedEvent, EventMigration<? extends Event> migration) {
        migrations.put(deprecatedEvent, migration);
    }

    public List<StorableEvent> migrate(List<StorableEvent> events) {
        List<StorableEvent> result = new ArrayList<>();
        for(StorableEvent e : events) {
            EventMigration<? extends Event> migration = getMigrationFor(e.getData());
            if(migration!=null) {
                List<Event> migrated = migration.migrate(e.getData());
                for(Event me : migrated) {
                    result.add(new StorableEvent(e.getAggregateId(), e.getVersion(), e.getOccurred(), me));
                }
            } else {
                result.add(e);
            }
        }
        return result;
    }

    private EventMigration<? extends Event> getMigrationFor(Event data) {
        return migrations.get(Events.eventTypeForClass(data.getClass().getName()));
    }
}
