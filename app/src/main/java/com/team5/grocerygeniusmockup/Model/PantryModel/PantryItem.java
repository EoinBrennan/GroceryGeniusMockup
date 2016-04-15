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

public class PantryItem {
    private String name;
    private String shelf;
    private String shopFrom;
    private String shopFromKey;
    private String secFrom;
    private String secFromKey;
    private int quantity;
    private long expiryDate;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    /* Empty constructor required for Firebase serialisation. */

    public PantryItem() {
    }

    /**
     * Use this constructor to create Shop objects
     *
     * @param name     The name of item, e.g. Milk
     * @param shelf     The name of the shelf the item is on.
     * @param shopFrom The shop this item came from.
     * @param shopFromKey That shop's key.
     * @param secFrom The section this item came from.
     * @param secFromKey That section's key.
     * @param quantity The amount of the item the user wants.
     * @param expiryDate The long value date the item expires.
     */

    public PantryItem(String name, String shelf, String shopFrom, String shopFromKey, String secFrom, String secFromKey, int quantity, long expiryDate) {
        this.name = name;
        this.shelf = shelf;
        this.shopFrom = shopFrom;
        this.shopFromKey = shopFromKey;
        this.secFrom = secFrom;
        this.secFromKey = secFromKey;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.timestampCreated = timestampCreated;

        HashMap<String, Object> timestampLastChangedObj = new HashMap<String, Object>();
        timestampLastChangedObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampLastChangedObj;
    }

    public String getName() {
        return name;
    }

    public String getShelf() {
        return shelf;
    }

    public String getShopFrom() {
        return shopFrom;
    }

    public String getShopFromKey() {
        return shopFromKey;
    }

    public String getSecFrom() {
        return secFrom;
    }

    public String getSecFromKey() {
        return secFromKey;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getExpiryDate() {
        return expiryDate;
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
