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

package com.google.android.cameraview;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class AspectRatioChooser {

    public enum Flag {
        CLOSEST_LOW, CLOSEST_HIGH, BEST_RATIO;
    }

    public static Size largest(SizeMap map, AspectRatio ratio) {
        return map.sizes(ratio).last();
    }

    public static Size closest(SizeMap map, int desiredWidth, int desiredHeight, EnumSet<Flag> flags) {

        Size desiredSize = new Size(desiredWidth, desiredHeight);
        Size result = null;

        Set<AspectRatio> ratios;
        if (flags.contains(Flag.BEST_RATIO)) {
            ratios = new HashSet<>();
            ratios.add(closestRatio(map.ratios(), desiredWidth, desiredHeight));
        } else {
            ratios = map.ratios();
        }

        for (AspectRatio r: ratios) {
            for (Size s: map.sizes(r)) {

                int compareResult = s.compareTo(desiredSize);

                // perfect match
                if (compareResult == 0) {
                    return s;
                }

                if (compareResult < 0 && flags.contains(Flag.CLOSEST_LOW)) {
                    if (result == null || result.compareTo(s) < 0) {
                        result = s;
                    }
                }

                if (compareResult > 0 && flags.contains(Flag.CLOSEST_HIGH)) {
                    if (result == null || result.compareTo(s) > 0) {
                        result = s;
                    }
                }
            }
        }

        return result != null ? result : new Size (0, 0);
    }

    public static AspectRatio closestRatio(Set<AspectRatio> ratios, int desiredWidth, int desiredHeight) {

        AspectRatio ratio = null;
        float desiredRatio = AspectRatio.of(desiredWidth, desiredHeight).toFloat();

        for (AspectRatio r: ratios) {
            if (ratio == null) {
                ratio = r;
                continue;
            }

            if (Math.abs(desiredRatio - r.toFloat()) < Math.abs(desiredRatio - ratio.toFloat())) {
                ratio = r;
            }
        }

        return ratio;
    }
}
