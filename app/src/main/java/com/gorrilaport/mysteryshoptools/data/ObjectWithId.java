package com.gorrilaport.mysteryshoptools.data;

/**
 * Superclass for objects that can be persisted in SQLite.
 */
public abstract class ObjectWithId {
    private long id;

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final int getIntId() {
        return (int) id;
    }
}