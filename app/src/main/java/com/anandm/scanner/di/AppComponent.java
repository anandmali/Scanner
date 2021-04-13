package com.anandm.scanner.di;

import android.app.Application;


import com.anandm.scanner.ScannerApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ActivityBuilder.class
        }
)
public interface AppComponent extends AndroidInjector<ScannerApp> {

    void inject(ScannerApp app);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}
