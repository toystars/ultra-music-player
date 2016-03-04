package com.bmustapha.ultramediaplayer.config;

import android.app.Application;

import com.bmustapha.ultramediaplayer.utilities.ContextProvider;
import com.facebook.drawee.backends.pipeline.Fresco;


/**
 * Created by kamiye on 10/12/15.
 */
public class UltraMediaPlayerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize ContextProvider
        new ContextProvider(getApplicationContext());
        // initialize Fresco image loading
        Fresco.initialize(this);
    }
}
