package com.team5.grocerygeniusmockup.Model.ListModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.HashMap;

/**
 * Created by Eoin on 05/03/2016.
 *
 * Used to model shops the user visits.
 *
 * Timestamps are stored as HashMap<String, Object>. The Object stored is a special dummy value
 * that becomes a long timestamp of the moment it reaches the Firebase database when it gets there.
 */

public class List {
    private String name;
    private String owner;
    private String ownerEmail;
    private String ownerUID;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    /* Empty constructor required for Firebase serialisation. */

    public List() {
    }

    /**
     * Use this constructor to create Shop objects
     *
     * @param name The name of shop, e.g. Aldi.
     * @param owner The name of the list's owner.
     * @param ownerEmail The owner's email
     * @param ownerUID The owner's UID in firebase.
     */

    public List(String name, String owner, String ownerEmail, String ownerUID) {
        this.name = name;
        this.owner = owner;
        this.ownerEmail = ownerEmail;
        this.ownerUID = ownerUID;
        this.timestampCreated = timestampCreated;

        HashMap<String, Object> timestampLastChangedObj = new HashMap<String, Object>();
        timestampLastChangedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampLastChangedObj;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public HashMap<String, Object> getTimestampCreated() {
        if (timestampCreated != null) {
            return timestampLastChanged;
        }

        HashMap<String, Object> timestampCreatedObj = new HashMap<String, Object>();
        timestampCreatedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        return timestampCreatedObj;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    /* These methods are not stored in the JSON object. */

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampLastChangedLong() {
        return (long) timestampLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
