package com.anandm.scanner.di;

import android.content.Context;

import com.anandm.scanner.BarcodeCaptureActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ScannerModule {

    @Provides
    Context provideScannerActivity(BarcodeCaptureActivity activity) {
        return activity;
    }
}
