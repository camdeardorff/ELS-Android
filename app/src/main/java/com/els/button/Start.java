package com.els.button;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Cam on 1/17/17.
 */

public class Start extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // start DBFlow instance now
        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());
    }
}
