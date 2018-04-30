package org.mlt.evsosample.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mlt.eso.Events;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.EventStore;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BodyParsingHandler<T> implements Route {
    private final EventStore eventStore;
    Class<T> bodyClass;

    public BodyParsingHandler(Class<T> bodyClass, EventStore store) {
        this.bodyClass = bodyClass;
        this.eventStore = store;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AtomicReference<Object> result = new AtomicReference<>();
        List<StorableEvent> events = Events.collect(() -> {
            result.set(execute(mapper.readValue(request.body(), bodyClass)));
        });
        eventStore.append(events);
        return result.get();
    }

    protected abstract Object execute(T body);
}
