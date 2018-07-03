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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.EnumSet;

public class AspectRatioChooserTest {

    @Test
    public void largest() {
        SizeMap map = new SizeMap();
        map.add(new Size(2,4));
        map.add(new Size(1, 2));

        AspectRatio r = AspectRatio.of(1, 2);

        Size size = AspectRatioChooser.largest(map, r);

        assertThat(size, is(new Size(2, 4)));
    }

    @Test
    public void matchDesiredSize() {
        SizeMap map = new SizeMap();
        map.add(new Size(3,6));
        map.add(new Size(2,4));
        map.add(new Size(1, 2));

        AspectRatio r = AspectRatio.of(1, 2);

        Size size = AspectRatioChooser.closest(map, 2, 4, EnumSet.noneOf(AspectRatioChooser.Flag.class));

        assertThat(size, is(new Size(2, 4)));
    }

    @Test
    public void closestLowDesiredSize() {
        SizeMap map = new SizeMap();
        map.add(new Size(5,10));
        map.add(new Size(4,8));
        map.add(new Size(2, 4));
        map.add(new Size(1, 2));

        Size size = AspectRatioChooser.closest(map, 3, 6, EnumSet.of(AspectRatioChooser.Flag.CLOSEST_LOW));

        assertThat(size, is(new Size(2, 4)));
    }

    @Test
    public void closestHighDesiredSize() {
        SizeMap map = new SizeMap();
        map.add(new Size(3,6));
        map.add(new Size(1, 2));

        Size size = AspectRatioChooser.closest(map, 2, 4, EnumSet.of(AspectRatioChooser.Flag.CLOSEST_HIGH));

        assertThat(size, is(new Size(3, 6)));
    }

    @Test
    public void closestLow2() {
        SizeMap map = new SizeMap();
        map.add(new Size(6,8)); // 3:4
        map.add(new Size(4,6)); // 2:3
        map.add(new Size(3, 4)); // 3:4
        map.add(new Size(2, 3)); // 2:3

        Size size = AspectRatioChooser.closest(map, 5, 8,
                EnumSet.of(AspectRatioChooser.Flag.CLOSEST_LOW));

        assertThat(size, is(new Size(4, 6)));
    }

    @Test
    public void closestLowBestRatio() {
        SizeMap map = new SizeMap();
        map.add(new Size(6,8)); // 3:4
        map.add(new Size(4,6)); // 2:3
        map.add(new Size(3, 4)); // 3:4
        map.add(new Size(2, 3)); // 2:3

        Size size = AspectRatioChooser.closest(map, 5, 8,
                EnumSet.of(AspectRatioChooser.Flag.CLOSEST_LOW,
                           AspectRatioChooser.Flag.BEST_RATIO));

        assertThat(size, is(new Size(3, 4)));
    }

    @Test
    public void closestHighBestRatio() {
        SizeMap map = new SizeMap();
        map.add(new Size(6,8)); // 3:4
        map.add(new Size(4,6)); // 2:3
        map.add(new Size(3, 4)); // 3:4
        map.add(new Size(2, 3)); // 2:3

        Size size = AspectRatioChooser.closest(map, 4, 4,
                EnumSet.of(AspectRatioChooser.Flag.CLOSEST_HIGH,
                        AspectRatioChooser.Flag.BEST_RATIO));

        assertThat(size, is(new Size(6, 8)));
    }
}
