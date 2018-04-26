package org.mlt.eso;

import java.util.UUID;

/**
 * Created by Marko on 26.4.2018.
 */
public class NameSetEvent extends Event {
    private String name;

    protected NameSetEvent() {}

    public NameSetEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "NameSet";
    }
}
