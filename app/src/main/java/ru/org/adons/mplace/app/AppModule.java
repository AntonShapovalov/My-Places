package ru.org.adons.mplace.app;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application scope dependencies (Context, DaoSession)
 */

@Module
class AppModule {

    private final Context context;

    AppModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return context;
    }

}
