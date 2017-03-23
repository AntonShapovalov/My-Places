package ru.org.adons.mplace.app;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Provide application scope dependencies (Context, DaoSession)
 */

@Singleton
@Component(modules = AppModule.class)
interface AppComponent {
}
