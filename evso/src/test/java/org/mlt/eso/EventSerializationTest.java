package org.mlt.eso;

import org.junit.Before;
import org.junit.Test;
import org.mlt.eso.serialization.EventNotRegisteredException;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.serialization.StorableEventSerializer;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by Marko on 28.4.2018.
 */
public class EventSerializationTest {

    @Test(expected = EventNotRegisteredException.class)
    public void testFailedDeserialization() {
        StorableEventSerializer serializer = new StorableEventSerializer();
        UUID uuid = UUID.fromString("417ef4ce-4ac0-11e8-842f-0ed5f89f718b");
        String json = serializer.eventToJson(new StorableEvent(uuid, 3, 123, new SampleEvent("foo", 3)));
        serializer.jsonToEvent(json);
    }

    @Test
    public void testSerialization() {
        Events.registerEventType("SampleEvent", SampleEvent.class);
        StorableEventSerializer serializer = new StorableEventSerializer();
        long occurred = 123;
        UUID uuid = UUID.fromString("417ef4ce-4ac0-11e8-842f-0ed5f89f718b");
        String json = serializer.eventToJson(new StorableEvent(uuid, 3, occurred, new SampleEvent("foo", 3)));
        assertEquals("{\"data\":{\"foo\":\"foo\",\"bar\":3},\"type\":\"SampleEvent\",\"aId\":\"417ef4ce-4ac0-11e8-842f-0ed5f89f718b\",\"ver\":3,\"at\":123}", json);
        Events.deregisterEventType("SampleEvent");
    }

    @Test
    public void testDeserialization() {
        Events.registerEventType("SampleEvent", SampleEvent.class);
        StorableEventSerializer serializer = new StorableEventSerializer();
        long occurred = 123;
        UUID uuid = UUID.fromString("417ef4ce-4ac0-11e8-842f-0ed5f89f718b");
        String json = serializer.eventToJson(new StorableEvent(uuid, 3, occurred, new SampleEvent("foo", 3)));
        StorableEvent event = serializer.jsonToEvent(json);
        assertEquals(uuid, event.getAggregateId());
        assertEquals(3, event.getVersion());
        assertEquals(123, event.getOccurred());
        assertEquals(SampleEvent.class, event.getData().getClass());
        Events.deregisterEventType("SampleEvent");
    }

}
