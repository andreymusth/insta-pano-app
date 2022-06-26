package com.tzkt.andrey.instapano;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class InstaPanoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
