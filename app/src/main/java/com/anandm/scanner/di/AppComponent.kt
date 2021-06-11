package com.anandm.scanner.di

import android.app.Application
import com.anandm.scanner.ScannerApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuilder::class
    ]
)
interface AppComponent : AndroidInjector<ScannerApp> {

    override fun inject(app: ScannerApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

}