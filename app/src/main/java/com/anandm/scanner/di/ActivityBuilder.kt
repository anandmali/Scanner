package com.anandm.scanner.di

import com.anandm.scanner.BarcodeCaptureActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [ScannerModule::class])
    abstract fun bindBarcodeCaptureActivity(): BarcodeCaptureActivity?

}