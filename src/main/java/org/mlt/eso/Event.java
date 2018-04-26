package org.mlt.eso;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Marko on 26.4.2018.
 */
public abstract class Event {
    @JsonIgnore()
    public abstract String getType();
}
