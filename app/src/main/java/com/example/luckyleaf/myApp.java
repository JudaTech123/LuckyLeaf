package com.example.luckyleaf;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

public class myApp extends Application {
    static myApp self;
    HandlerThread dbThread;
    Handler dbHandler;

    public Handler getDbHandler() {
        return dbHandler;
    }

    public static myApp getSelf() {
        return self;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        PrefsHelper.getInstance().initPrefs(this);
        dbThread = new HandlerThread("dbThread");
        dbThread.start();
        dbHandler = new Handler(dbThread.getLooper());
    }
}
