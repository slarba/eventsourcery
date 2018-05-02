package org.mlt.eso.serialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import org.mlt.eso.Event;

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

    public StorableEvent(UUID aggregateId, long version, long occurred, Event data) {
        this.aggregateId = aggregateId;
        this.version = version;
        this.occurred = occurred;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorableEvent)) return false;

        StorableEvent that = (StorableEvent) o;

        if (version != that.version) return false;
        //if (occurred != that.occurred) return false;
        if (!aggregateId.equals(that.aggregateId)) return false;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result = aggregateId.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        //result = 31 * result + (int) (occurred ^ (occurred >>> 32));
        result = 31 * result + data.hashCode();
        return result;
    }
}
