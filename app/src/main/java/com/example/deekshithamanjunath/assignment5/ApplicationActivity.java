package com.example.deekshithamanjunath.assignment5;

import android.app.Application;
import android.content.Context;

/**
 * Created by deekshithamanjunath on 4/2/17.
 */

public class ApplicationActivity extends Application{
    private static ApplicationActivity instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

    }

    public static ApplicationActivity getInstance()
    {
        return instance;
    }

    public static Context getContext()
    {
        return instance.getApplicationContext();
    }
}
