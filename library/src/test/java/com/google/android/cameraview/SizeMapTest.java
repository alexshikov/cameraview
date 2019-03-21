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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.SortedSet;

public class SizeMapTest {

    @Test
    public void testAdd_simple() {
        SizeMap map = new SizeMap();
        map.add(new Size(3, 4));
        map.add(new Size(9, 16));
        assertThat(map.ratios().size(), is(2));
    }

    @Test
    public void testAdd_duplicate() {
        SizeMap map = new SizeMap();
        map.add(new Size(3, 4));
        map.add(new Size(6, 8));
        map.add(new Size(9, 12));
        assertThat(map.ratios().size(), is(1));
        AspectRatio ratio = (AspectRatio) map.ratios().toArray()[0];
        assertThat(ratio.toString(), is("3:4"));
        assertThat(map.sizes(ratio).size(), is(3));
    }

    @Test
    public void testClear() {
        SizeMap map = new SizeMap();
        map.add(new Size(12, 34));
        assertThat(map.ratios().size(), is(1));
        map.clear();
        assertThat(map.ratios().size(), is(0));
    }

    @Test
    public void testEmpty() {
        SizeMap map = new SizeMap();
        SortedSet<Size> sizes = map.sizes(AspectRatio.of(3, 4));
        assertNull(sizes);
    }

    @Test
    public void testSizesSyncronization() {
        SizeMap map = new SizeMap();
        map.add(new Size(1, 2));
        map.add(new Size(2,3));

        map.remove(AspectRatio.of(1,2));
        assertThat(map.ratios().size(), is(1));

        AspectRatio ratio = map.ratios().iterator().next();
        assertThat(ratio, is(AspectRatio.of(2, 3)));

        assertNull(map.sizes(AspectRatio.of(1,2)));
        assertThat(map.sizes(AspectRatio.of(2,3)).last(), is(new Size(2,3)));
    }

    @Test
    public void testSizesSync2() {
        SizeMap map = new SizeMap();
        map.add(new Size(1, 2));
        map.add(new Size(2, 3));
        map.add(new Size(3,4));

        SizeMap sync = new SizeMap();
        sync.add(new Size(1,2));

        //map.ratios().removeIf(r -> !sync.ratios().contains(r));

        for (AspectRatio ratio: new ArrayList<>(map.ratios())) {
            if (!sync.ratios().contains(ratio)) {
                map.remove(ratio);
            }
        }

        assertNull(map.sizes(AspectRatio.of(3,4)));
    }
}
