package org.mlt.eso;

/**
 * Created by Marko on 28.4.2018.
 */
public class SampleEvent extends Event {
    private String foo;
    private int bar;

    public SampleEvent() {
    }

    public SampleEvent(String foo, int i) {
        super();
        this.foo = foo;
        this.bar = i;
    }

    @Override
    public String getType() {
        return "SampleEvent";
    }
}
