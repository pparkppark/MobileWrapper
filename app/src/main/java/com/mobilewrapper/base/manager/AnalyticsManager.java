package com.mobilewrapper.base.manager;

import android.content.Context;

import com.mobilewrapper.base.communication.Communication;

/**
 * Created by ppark on 2015-08-24.
 */
public class AnalyticsManager {

    public static void analytics(Context context, String category, String action, String label) {
        new Communication(context).analytics(category, action, label);
    }

    public static void analytics(Context context, String category, String action, Integer label) {
        analytics(context, category, action, label.toString());
    }
}
