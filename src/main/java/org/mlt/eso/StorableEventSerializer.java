package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * Created by Marko on 26.4.2018.
 */
public class StorableEventSerializer {
    private ObjectMapper mapper;

    public StorableEventSerializer() {
        mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    public String eventToJson(StorableEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("event serialization failed: ", e);
        }
    }

    public StorableEvent jsonToEvent(String json) {
        try {
            return mapper.readValue(json, StorableEvent.class);
        } catch (IOException e) {
            throw new RuntimeException("event deserialization failed: ", e);
        }
    }
}
