package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class StorableEvent {
    private UUID aggregateId;
    private long aggregateVersion;
    private Date occurred;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonTypeIdResolver(EventTypeIdResolver.class)
    private Event data;

    protected StorableEvent() {

    }

    public StorableEvent(UUID aggregateId, long version, Event data) {
        this.aggregateId = aggregateId;
        this.aggregateVersion = version;
        this.occurred = new Date();
        this.data = data;
    }

    public Event getData() {
        return data;
    }

    public String toString() {
        return "event[" +
                "aggregateId=" + aggregateId.toString() + "," +
                "version=" + aggregateVersion + "," +
                "occurred=" + occurred + "," +
                "data=" + data.toString() + "]";
    }

    public UUID getAggregateId() {
        return aggregateId;
    }
}
