package com.snda.storage.service.model;


/**
 * Represents an owner object with a canonical ID and, optionally, a display name.
 *
 */
@Deprecated
public class StorageOwner {

    private String displayName;
    private String id;

    public StorageOwner() {
    }

    public StorageOwner(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " ["
            + "id=" + getId()
            + (getDisplayName() != null ? ", name=" + getDisplayName(): "")
            + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }
    
}
