package org.mlt.eso.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.mlt.eso.Event;
import org.mlt.eso.Events;

import java.io.IOException;

/**
 * Created by Marko on 26.4.2018.
 */
public class EventTypeIdResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object value) {
        return Events.eventTypeForClass(value.getClass().getName());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        TypeFactory tf = context.getTypeFactory();
        try {
            String className = Events.classForEventType(id);
            if(className==null) {
                throw new EventNotRegisteredException("class for event type " + id + " not registered.");
            }
            return tf.constructType(tf.findClass(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found", e);
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
