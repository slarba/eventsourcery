package org.mlt.eso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class InMemoryEventStore implements EventStore {
    private ArrayList<String> events = new ArrayList<>();

    @Override
    public List<StorableEvent> loadEventsForAggregate(UUID uuid) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        List<StorableEvent> e = new ArrayList<>();
        for(String s : events) {
            StorableEvent ev = serializer.jsonToEvent(s);
            if(ev.getAggregateId().equals(uuid)) {
                e.add(ev);
            }
        }
        return e;
    }

    @Override
    public List<StorableEvent> loadEvents(int startindex, int count) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        List<StorableEvent> l = new ArrayList<>();
        for(int i=startindex; i<startindex+count && i<l.size(); i++) {
            l.add(serializer.jsonToEvent(events.get(i)));
        }
        return l;
    }

    @Override
    public void append(List<StorableEvent> events) {
        StorableEventSerializer serializer = new StorableEventSerializer();
        for(StorableEvent e : events) {
            String json = serializer.eventToJson(e);
            System.out.println(json);
            this.events.add(json);
        }
    }
}
