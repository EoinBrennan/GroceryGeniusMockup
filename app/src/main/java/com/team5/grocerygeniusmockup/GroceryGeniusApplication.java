package com.team5.grocerygeniusmockup;

/**
 * Created by user on 05/03/2016.
 */

import com.firebase.client.Firebase;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Includes one-time initialization of Firebase related code
 */
public class GroceryGeniusApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*Initialise Fabric. */
        Fabric.with(this, new Crashlytics());
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setLogLevel(com.firebase.client.Logger.Level.DEBUG);
    }
}
