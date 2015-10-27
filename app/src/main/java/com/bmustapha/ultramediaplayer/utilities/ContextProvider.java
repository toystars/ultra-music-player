package com.bmustapha.ultramediaplayer.utilities;

import android.content.Context;

/**
 * Created by kamiye on 10/11/15.
 */
public class ContextProvider {

    private static Context context;

    public ContextProvider (Context context) {
        ContextProvider.context = context;
    }

    public static Context getContext() {
        return context;
    }
}
