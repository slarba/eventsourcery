package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class StorableEvent {
    @JsonProperty("aId")
    private UUID aggregateId;

    @JsonProperty("ver")
    private long version;

    @JsonProperty("at")
    private long occurred;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonTypeIdResolver(EventTypeIdResolver.class)
    private Event data;

    protected StorableEvent() { }

    public StorableEvent(UUID aggregateId, long version, Event data) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.occurred = System.currentTimeMillis();
        this.data = data;
    }

    public Event getData() {
        return data;
    }

    public long getVersion() { return version; }

    public String toString() {
        return "event[" +
                "aggregateId=" + aggregateId.toString() + "," +
                "version=" + version + "," +
                "occurred=" + occurred + "," +
                "data=" + data.toString() + "]";
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public long getOccurred() {
        return occurred;
    }
}
