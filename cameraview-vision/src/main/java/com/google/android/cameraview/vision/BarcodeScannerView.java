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

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.google.android.cameraview.CameraView;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeScannerView extends CameraView {

    private BarcodeProcessor mBarcodeProcessor;

    public BarcodeScannerView(Context context) {
        this(context, null);
    }

    public BarcodeScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarcodeScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBarcodeProcessor = new BarcodeProcessor(context);
        addCallback(new CameraView.Callback() {
            @Override
            public void onFramePreview(CameraView cameraView, byte[] data, int width, int height, int orientation) {
                int facing = cameraView.getFacing();
                int rotation = getCorrectCameraRotation(orientation, facing);

                mBarcodeProcessor.process(data, new FrameMetadata(width, height, rotation, facing));
            }
        });
    }

    public void setFrameDelegate(FrameProcessorDelegate<SparseArray<Barcode>> delegate) {
        mBarcodeProcessor.setDelegate(delegate);
    }

    public void setBarcodeType(int barcodeType) {
        mBarcodeProcessor.setBarcodeType(barcodeType);
    }

    private static int getCorrectCameraRotation(int rotation, int facing) {
        if (facing == CameraView.FACING_FRONT) {
            return (rotation - 90 + 360) % 360;
        } else {
            return (-rotation + 90 + 360) % 360;
        }
    }
}
