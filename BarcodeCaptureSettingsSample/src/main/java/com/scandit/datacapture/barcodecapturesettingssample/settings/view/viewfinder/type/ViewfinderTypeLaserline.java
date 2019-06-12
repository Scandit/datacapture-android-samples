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
import com.scandit.datacapture.core.ui.viewfinder.LaserlineViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.Viewfinder;

public class ViewfinderTypeLaserline extends ViewfinderType {

    public static ViewfinderTypeLaserline fromCurrentViewfinderAndSettings(
            Viewfinder currentViewfinder, SettingsManager settingsManager
    ) {
        return new ViewfinderTypeLaserline(
                currentViewfinder instanceof LaserlineViewfinder,
                settingsManager.getLaserlineViewfinderWidth(),
                settingsManager.getLaserlineViewfinderEnabledColor(),
                settingsManager.getLaserlineViewfinderDisabledColor()
        );
    }

    private FloatWithUnit width;

    private EnabledColor enabledColor;
    private DisabledColor disabledColor;

    private ViewfinderTypeLaserline(
            boolean enabled,
            FloatWithUnit width,
            EnabledColor enabledColor,
            DisabledColor disabledColor
    ) {
        super(R.string.laserline, enabled);
        this.width = width;
        this.enabledColor = enabledColor;
        this.disabledColor = disabledColor;
    }

    @Nullable
    @Override
    public Viewfinder buildViewfinder() {
        LaserlineViewfinder viewfinder = new LaserlineViewfinder();
        viewfinder.setWidth(width);
        viewfinder.setEnabledColor(enabledColor.color);
        viewfinder.setDisabledColor(disabledColor.color);
        return viewfinder;
    }

    public EnabledColor getEnabledColor() {
        return enabledColor;
    }

    public void setEnabledColor(
            EnabledColor enabledColor
    ) {
        this.enabledColor = enabledColor;
    }

    public DisabledColor getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(
            DisabledColor disabledColor
    ) {
        this.disabledColor = disabledColor;
    }

    public FloatWithUnit getWidth() {
        return width;
    }

    public void setWidth(FloatWithUnit width) {
        this.width = width;
    }

    public enum EnabledColor {
        DEFAULT(new LaserlineViewfinder().getEnabledColor(), R.string._default),
        RED(Color.RED, R.string.red),
        WHITE(Color.WHITE, R.string.white);

        @ColorInt public final int color;
        @StringRes public final int displayName;

        EnabledColor(@ColorInt int color, @StringRes int displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }

    public enum DisabledColor {
        DEFAULT(new LaserlineViewfinder().getDisabledColor(), R.string._default),
        BLUE(Color.BLUE, R.string.blue),
        RED(Color.RED, R.string.red);

        @ColorInt public final int color;
        @StringRes public final int displayName;

        DisabledColor(@ColorInt int color, @StringRes int displayName) {
            this.color = color;
            this.displayName = displayName;
        }
    }
}
