package com.team5.grocerygeniusmockup.Model;

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

public class User {
    private String name;
    private String email;
    private String UID;
    private HashMap<String, Object> timestampCreated;

    /* Empty constructor required for Firebase serialisation. */

    public User() {
    }

    /**
     * Use this constructor to create Shop objects
     *
     * @param name The name of shop, e.g. Aldi.
     * @param email The user's email
     * @param UID The user's UID in firebase.
     */

    public User(String name, String owner, String email, String UID) {
        this.name = name;
        this.email = email;
        this.UID = UID;
        this.timestampCreated = timestampCreated;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUID() {
        return UID;
    }

    public HashMap<String, Object> getTimestampCreated() {
        HashMap<String, Object> timestampCreatedObj = new HashMap<String, Object>();
        timestampCreatedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        return timestampCreatedObj;
    }


    /* These methods are not stored in the JSON object. */

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
    
}
