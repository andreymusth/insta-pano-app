package com.tzkt.andrey.instapano

import android.app.Application
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class InstaPanoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()


        )
    }
}