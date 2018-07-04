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

public class FrameMetadata {

    private final int mWidth;
    private final int mHeight;
    private final int mRotation;
    private final int mCameraFacing;

    public FrameMetadata(int width, int height, int rotation, int facing) {
        mWidth = width;
        mHeight = height;
        mRotation = rotation;
        mCameraFacing = facing;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getRotation() {
        return mRotation;
    }

    public int getCameraFacing() {
        return mCameraFacing;
    }

    public boolean isLandscape() {
        return mRotation % 180 == 90;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FrameMetadata) {
            FrameMetadata other = (FrameMetadata) obj;
            return (other.getWidth() == getWidth() &&
                    other.getHeight() == getHeight() &&
                    other.getCameraFacing() == getCameraFacing() &&
                    other.getRotation() == getRotation());
        } else {
            return super.equals(obj);
        }
    }
}
