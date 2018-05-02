package org.mlt.eso;

/**
 * Base class for event-aware domain classes
 *
 */
public class Aggregate<T extends Identity> {
    private T id;
    private long version;
    private boolean deleted;

    /**
     * Default constructor.
     *
     * Derived classes should implement a default constructor that does NOT emit events
     * using {@link org.mlt.eso.Events#dispatch(Aggregate, Event...)}
     */
    protected Aggregate() {
    }

    /**
     * Construct aggregate with identity.
     *
     * Constructors in derived classes should call this to set the identity and dispatch a creation event using
     * {@link org.mlt.eso.Events#dispatch(Aggregate, Event...)}
     *
     * @see org.mlt.eso.Events#dispatch(Aggregate, Event...)
     * @param id unique identity for this aggregate
     */
    protected Aggregate(T id) {
        this.id = id;
        version = 0;
    }

    /**
     * Get aggregate identity
     *
     * @return aggregate identity
     */
    public T getId() { return id; }

    /**
     * Return aggregate version
     *
     * @return aggregate version
     */
    public long getVersion() { return version; }

    /**
     * Set aggregate identity
     *
     * Useful in handling construction events
     *
     * @param id unique identity for this aggregate
     */
    protected void setId(T id) {
        this.id = id;
    }

    /**
     * Increment version after event dispatched or replayed. Used to assign and check applicable aggregate version
     * for events.
     */
    public void bumpVersion() {
        ++version;
    }

    /**
     * Mark object deletion status
     *
     * Useful for handling aggregate deletion. Events cannot be replayed on top of aggregate that is deleted.
     *
     * @param deleted true marks the object as deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Get deletion status
     *
     * @return true if aggregate is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
}
