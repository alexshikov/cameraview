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

// Implementation based on React-Native camera implementation
// https://github.com/react-native-community/react-native-camera/blob/master/android/src/main/java/org/reactnative/barcodedetector/RNBarcodeDetector.java

package com.google.android.cameraview.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.util.SparseArray;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BarcodeProcessor extends FrameProcessorBase<SparseArray<Barcode>> {

    private BarcodeDetector mBarcodeDetector = null;
    private FrameMetadata mPreviousFrameMetadata;
    private BarcodeDetector.Builder mBuilder;

    private int mBarcodeType = Barcode.ALL_FORMATS;

    private Executor mExecutor;

    public BarcodeProcessor(Context context) {
        mBuilder = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(mBarcodeType);

        mExecutor = Executors.newFixedThreadPool(1);
    }

    public SparseArray<Barcode> detect(Bitmap bitmap) {

        if (!isOperational()) {
            return null;
        }

        Frame frame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        return mBarcodeDetector.detect(frame);
    }

    @Override
    protected Task<SparseArray<Barcode>> detect(byte[] data, FrameMetadata metadata) {
        return Tasks.call(mExecutor, new DetectBarcode(data, metadata));
    }

    // Public API

    public boolean isOperational() {
        if (mBarcodeDetector == null) {
            createBarcodeDetector();
        }

        return mBarcodeDetector.isOperational();
    }

    @Override
    public void stop() {

    }

    public void setBarcodeType(int barcodeType) {
        if (barcodeType != mBarcodeType) {
            release();
            mBuilder.setBarcodeFormats(barcodeType);
            mBarcodeType = barcodeType;
        }
    }


    public void release() {
        releaseBarcodeDetector();
        mPreviousFrameMetadata = null;
    }

    // Lifecycle methods

    private void releaseBarcodeDetector() {
        if (mBarcodeDetector != null) {
            mBarcodeDetector.release();
            mBarcodeDetector = null;
        }
    }

    private void createBarcodeDetector() {
        mBarcodeDetector = mBuilder.build();
    }

    private class DetectBarcode implements Callable<SparseArray<Barcode>> {

        private final byte[] mData;
        private final FrameMetadata mMetadata;

        public DetectBarcode (byte[] data, FrameMetadata metadata) {
            mData = data;
            mMetadata = metadata;
        }

        @Override
        public SparseArray<Barcode> call() throws Exception {

            // If the frame has different dimensions, create another barcode detector.
            // Otherwise we will most likely get nasty "inconsistent image dimensions" error from detector
            // and no barcode will be detected.
            if (!mMetadata.equals(mPreviousFrameMetadata)) {
                releaseBarcodeDetector();
            }

            if (mBarcodeDetector == null) {
                createBarcodeDetector();
                mPreviousFrameMetadata = mMetadata;
            }

            // build vision frame from data
            Frame.Builder builder = new Frame.Builder();
            ByteBuffer byteBuffer = ByteBuffer.wrap(mData);
            builder.setImageData(byteBuffer, mMetadata.getWidth(), mMetadata.getHeight(), ImageFormat.NV21);
            Frame frame = builder.build();

            return mBarcodeDetector.detect(frame);
        }
    }
}
