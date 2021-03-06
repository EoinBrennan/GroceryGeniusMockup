package com.team5.grocerygeniusmockup.Model.PantryModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.team5.grocerygeniusmockup.Utilities.Constants;

import java.util.HashMap;

/**
 * Created by Eoin on 07/03/2016.
 *
 * Used to items the user wants to but.
 *
 * Timestamps are stored as HashMap<String, Object>. The Object stored is a special dummy value
 * that becomes a long timestamp of the moment it reaches the Firebase database when it gets there.
 */

public class Shelf {
    private String name;
    private int order;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    /* Empty constructor required for Firebase serialisation. */

    public Shelf() {
    }

    /**
     * Use this constructor to create Shop objects
     *
     * @param name     The name of item, e.g. Milk
     * @param order    Where does this section appear in the shop.
     */

    public Shelf(String name, int order) {
        this.name = name;
        this.order = order;
        this.timestampCreated = timestampCreated;

        HashMap<String, Object> timestampLastChangedObj = new HashMap<String, Object>();
        timestampLastChangedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampLastChangedObj;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
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
