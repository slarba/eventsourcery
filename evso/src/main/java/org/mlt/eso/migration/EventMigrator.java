package org.mlt.eso.migration;

import org.mlt.eso.Event;
import org.mlt.eso.Events;
import org.mlt.eso.serialization.StorableEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventMigrator {
    private Map<String, EventMigration<? extends Event>> migrations = new HashMap<>();

    public void registerMigration(String deprecatedEvent, EventMigration<? extends Event> migration) {
        migrations.put(deprecatedEvent, migration);
    }

    public List<StorableEvent> migrate(List<StorableEvent> events) {
        return events.stream().flatMap(this::migrateSingle).collect(Collectors.toList());
    }

    private Stream<StorableEvent> migrateSingle(StorableEvent e) {
        EventMigration<? extends Event> migration = getMigrationFor(e.getData());
        if(migration!=null) {
            return migration.migrate(e.getData())
                    .stream()
                    .map((migrated) -> new StorableEvent(e.getAggregateId(), e.getVersion(), e.getOccurred(), migrated));
        } else {
            return Stream.of(e);
        }
    }

    private EventMigration<? extends Event> getMigrationFor(Event data) {
        return migrations.get(Events.eventTypeForClass(data.getClass().getName()));
    }
}
