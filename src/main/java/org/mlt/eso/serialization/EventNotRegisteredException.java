package org.mlt.eso.serialization;

/**
 * Created by Marko on 28.4.2018.
 */
public class EventNotRegisteredException extends RuntimeException {
    public EventNotRegisteredException(String msg) {
        super(msg);
    }
}
