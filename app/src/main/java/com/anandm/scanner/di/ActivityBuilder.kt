package com.anandm.scanner.di

import com.anandm.scanner.BarcodeCaptureActivity
import com.anandm.scanner.di.ScannerModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [ScannerModule::class])
    abstract fun bindBarcodeCaptureActivity(): BarcodeCaptureActivity?
}