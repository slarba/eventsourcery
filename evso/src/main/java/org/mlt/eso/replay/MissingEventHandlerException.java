package org.mlt.eso.replay;

/**
 * Created by Marko on 26.4.2018.
 */
public class MissingEventHandlerException extends RuntimeException {
    public MissingEventHandlerException(String msg, Exception innerEx) {
        super(msg, innerEx);
    }
}
