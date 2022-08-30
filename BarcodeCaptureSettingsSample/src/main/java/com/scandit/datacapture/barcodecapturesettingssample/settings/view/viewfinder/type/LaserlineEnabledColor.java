/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.viewfinder.type;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinderStyle;

import java.util.HashMap;
import java.util.Map;

public class LaserlineEnabledColor {

    @ColorInt public final int color;
    @StringRes public final int displayName;

    private static final Map<LaserlineViewfinderStyle, LaserlineEnabledColor[]> map =
            new HashMap<>();

    public static final LaserlineEnabledColor DEFAULT = new LaserlineEnabledColor(
            new LaserlineViewfinder(LaserlineViewfinderStyle.LEGACY).getEnabledColor(),
            R.string._default
    );

    static {
        LaserlineViewfinderStyle[] styles = LaserlineViewfinderStyle.values();

        LaserlineEnabledColor red = new LaserlineEnabledColor(Color.RED, R.string.red);
        LaserlineEnabledColor blue = new LaserlineEnabledColor(Color.BLUE, R.string.blue);
        LaserlineEnabledColor white = new LaserlineEnabledColor(Color.WHITE, R.string.white);

        for (LaserlineViewfinderStyle style : styles) {
            LaserlineViewfinder viewfinder = new LaserlineViewfinder(style);
            LaserlineEnabledColor[] colors = {
                    new LaserlineEnabledColor(viewfinder.getEnabledColor(), R.string._default),
                    red,
                    blue,
                    white
            };
            map.put(style, colors);
        }
    }

    public static LaserlineEnabledColor[] getAllForStyle(LaserlineViewfinderStyle style) {
        return map.get(style);
    }

    public static LaserlineEnabledColor getDefaultForStyle(LaserlineViewfinderStyle style) {
        return getAllForStyle(style)[0];
    }

    private LaserlineEnabledColor(@ColorInt int color, @StringRes int displayName) {
        this.color = color;
        this.displayName = displayName;
    }
}
