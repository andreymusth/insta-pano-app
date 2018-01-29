package com.tzkt.andrey.instapano.utils;

import android.content.Context;
import android.content.Intent;

import com.tzkt.andrey.instapano.PreviewActivity;
import com.tzkt.andrey.instapano.settings.SettingsActivity;

/**
 * Created by andrey on 29/01/2018.
 */

public final class NavigationUtils {

    public static void openSettings(Context c){
        Intent intent = new Intent(c, SettingsActivity.class);
        c.startActivity(intent);
    }

    public static void openPreview(Context c){
        Intent intent = new Intent(c, PreviewActivity.class);
        c.startActivity(intent);
    }
}
