package com.mobilewrapper.base;

import android.app.Application;

import com.mobilewrapper.base.communication.retrofit.beans.Initialize;

/**
 * Created by ppark on 2015-08-20.
 */
public class WrapperApplication extends Application {

    public static final String webViewHome = "http://m.wizwid.com/MCSW/handler/wizwid/kr/MainView-MainView";
    public static final String EXTRA_SERIAL_PUSHMSG = "pushMsg";

    public static boolean DEBUG = true;

    public static Initialize initialize = new Initialize();

    public static Initialize getInitialize() {
        return initialize;
    }

    public static void setInitialize(Initialize initialize) {
        WrapperApplication.initialize = initialize;
    }
}
