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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.overlay;

import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.ui.style.Brush;

@SuppressWarnings("WeakerAccess")
public class OverlaySettingsViewModel extends ViewModel {

    @ColorInt private static final int RED = Color.parseColor("#FFFF0000");
    @ColorInt private static final int TRANSPARENT_RED = Color.parseColor("#33FF0000");
    @ColorInt private static final int GREEN = Color.parseColor("#FF00FF00");
    @ColorInt private static final int TRANSPARENT_GREEN = Color.parseColor("#3300FF00");

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    private final float strokeWidth = settingsManager.getDefaultBrush().getStrokeWidth();

    private OverlaySettingsBrushEntry[] availableBrushes = new OverlaySettingsBrushEntry[]{
            new OverlaySettingsBrushEntry(settingsManager.getDefaultBrush(), R.string._default),
            new OverlaySettingsBrushEntry(
                    TRANSPARENT_RED,
                    RED,
                    strokeWidth,
                    R.string.red
            ),
            new OverlaySettingsBrushEntry(
                    TRANSPARENT_GREEN,
                    GREEN,
                    strokeWidth,
                    R.string.green
            )
    };

    OverlaySettingsBrushEntry[] getAvailableBrushes() {
        return availableBrushes;
    }

    void setCurrentBrush(OverlaySettingsBrushEntry brushEntry) {
        settingsManager.setNewBrush(brushEntry.brush);
    }

    OverlaySettingsBrushEntry getCurrentBrushEntry() {
        Brush currentBrush = settingsManager.getCurrentBrush();
        for (OverlaySettingsBrushEntry entry : availableBrushes) {
            if (entry.brush.getStrokeColor() == currentBrush.getStrokeColor()
                    && entry.brush.getFillColor() == currentBrush.getFillColor()) {
                return entry;
            }
        }
        return availableBrushes[0];
    }
}
