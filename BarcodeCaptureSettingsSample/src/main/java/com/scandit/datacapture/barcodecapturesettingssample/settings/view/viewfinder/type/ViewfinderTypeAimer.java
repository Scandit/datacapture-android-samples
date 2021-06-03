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
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.ui.viewfinder.AimerViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

public class ViewfinderTypeAimer extends ViewfinderType {

    public static ViewfinderTypeAimer fromCurrentViewfinderAndSettings(
            Viewfinder currentViewfinder, SettingsManager settingsManager
    ) {
        return new ViewfinderTypeAimer(
                currentViewfinder instanceof AimerViewfinder,
                settingsManager.getAimerViewfinderFrameColor(),
                settingsManager.getAimerViewfinderDotColor()
        );
    }

    private FrameColor frameColor;
    private DotColor dotColor;

    private ViewfinderTypeAimer(
            boolean enabled,
            FrameColor frameColor,
            DotColor dotColor
    ) {
        super(R.string.aimer, enabled);
        this.frameColor = frameColor;
        this.dotColor = dotColor;
    }

    @Nullable
    @Override
    public Viewfinder buildViewfinder() {
        AimerViewfinder viewfinder = new AimerViewfinder();
        viewfinder.setFrameColor(frameColor.color);
        viewfinder.setDotColor(dotColor.color);
        return viewfinder;
    }

    @Override
    public void resetDefaults() {}

    public FrameColor getFrameColor() {
        return frameColor;
    }

    public void setFrameColor(FrameColor frameColor) {
        this.frameColor = frameColor;
    }

    public DotColor getDotColor() {
        return dotColor;
    }

    public void setDotColor(DotColor dotColor) {
        this.dotColor = dotColor;
    }

    public enum FrameColor {
        DEFAULT(new AimerViewfinder().getFrameColor(), R.string._default),
        BLUE(Color.BLUE, R.string.blue),
        RED(Color.RED, R.string.red);

        @ColorInt public final int color;
        @StringRes public final int displayName;

        FrameColor(@ColorInt int color, @StringRes int displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }

    public enum DotColor {
        DEFAULT(new AimerViewfinder().getDotColor(), R.string._default),
        BLUE(Color.BLUE, R.string.blue),
        RED(Color.RED, R.string.red);

        @ColorInt public final int color;
        @StringRes public final int displayName;

        DotColor(@ColorInt int color, @StringRes int displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }
}
