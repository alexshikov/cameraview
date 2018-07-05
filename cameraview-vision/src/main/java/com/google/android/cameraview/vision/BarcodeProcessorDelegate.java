/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.cameraview.vision;

import android.util.SparseArray;

import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeProcessorDelegate implements FrameProcessorDelegate<SparseArray<Barcode>> {

    private long lastDetectionTime;
    private int delayBetweenDetections = 500;

    public int getDelayBetweenDetections() {
        return delayBetweenDetections;
    }

    public void setDelayBetweenDetections(int delay) {
        delayBetweenDetections = delay;
    }

    public void onBarcodeDetected(Barcode barcode) {
    }

    @Override
    public final void onSuccess(SparseArray<Barcode> result) {
        int size = result.size();
        if (size == 0) {
            return;
        }

        long t = System.currentTimeMillis();
        if ((t - lastDetectionTime) < delayBetweenDetections) {
            return;
        }

        lastDetectionTime = t;

        for (int i = 0; i < size; i++) {
            onBarcodeDetected(result.get(result.keyAt(i)));
        }
    }

    @Override
    public void onError(Exception e) {
    }
}
