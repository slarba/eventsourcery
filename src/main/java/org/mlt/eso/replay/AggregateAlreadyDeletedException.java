package org.mlt.eso.replay;

/**
 * Created by Marko on 28.4.2018.
 */
public class AggregateAlreadyDeletedException extends RuntimeException {
    public AggregateAlreadyDeletedException(String msg) {
        super(msg);
    }
}
