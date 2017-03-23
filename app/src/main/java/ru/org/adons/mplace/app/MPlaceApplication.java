package ru.org.adons.mplace.app;

import android.app.Application;

import com.facebook.stetho.Stetho;

import ru.org.adons.mplace.BuildConfig;

/**
 * Provide application scope dependencies (Context, DaoSession)
 */

public class MPlaceApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    public AppComponent appComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(getApplicationContext()))
                    .build();
        }
        return appComponent;
    }

}
