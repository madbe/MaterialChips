package edu.ben.sample;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ben on 07-Dec-17.
 */

public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
}
