package com.team5.grocerygeniusmockup.Utilities;

/**
 * Created by user on 05/03/2016.
 */

public final class Constants {

    /**
     * Constants for general use.
     */

    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_CREATED = "timestampCreated";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_CHANGED = "timestampLastChanged";

    public static final String FIREBASE_NODENAME_LISTS = "lists";
    public static final String FIREBASE_NODENAME_SHOPS = "shops";
    public static final String FIREBASE_NODENAME_SECTIONS = "sections";
    public static final String FIREBASE_NODENAME_ITEMS = "items";
    public static final String FIREBASE_NODENAME_PANTRY_ITEMS = "pantry_items";
    public static final String FIREBASE_NODENAME_SHELVES = "shelves";
    public static final String FIREBASE_NODENAME_SHARED = "sharedWith";

    /**
     * Constants for Firebase URL
     */

    public static final String FIREBASE_URL = "https://grocerygeniusmu.firebaseio.com/";
    public static final String FIREBASE_URL_LISTS = FIREBASE_URL + "/" + FIREBASE_NODENAME_LISTS;
    public static final String FIREBASE_URL_SHARED = FIREBASE_URL + "/sharedWith";


    public static final String FIREBASE_URL_NODE = FIREBASE_URL;
    /*intents */
    public static final String KEY_LIST_ID = "listKey";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_ID = "userID";
}
