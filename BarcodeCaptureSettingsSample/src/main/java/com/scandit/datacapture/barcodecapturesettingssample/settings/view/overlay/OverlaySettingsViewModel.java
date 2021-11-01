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

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.ui.style.Brush;

@SuppressWarnings("WeakerAccess")
public class OverlaySettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    BrushStyleEntry[] getAvailableBrushes() {
        BarcodeCaptureOverlayStyle currentStyle = settingsManager.getOverlayStyle();
        return settingsManager.getBrushes().get(currentStyle);
    }

    void setCurrentBrush(BrushStyleEntry brushEntry) {
        settingsManager.setNewBrush(brushEntry.brush);
    }

    BrushStyleEntry getCurrentBrushEntry() {
        BrushStyleEntry[] availableBrushes = getAvailableBrushes();
        Brush currentBrush = settingsManager.getCurrentBrush();
        for (BrushStyleEntry entry : availableBrushes) {
            if (entry.brush.getStrokeColor() == currentBrush.getStrokeColor()
                    && entry.brush.getFillColor() == currentBrush.getFillColor()) {
                return entry;
            }
        }
        return availableBrushes[0];
    }

    OverlayStyleEntry[] getOverlayStyles() {
        BarcodeCaptureOverlayStyle[] availableStyles = BarcodeCaptureOverlayStyle.values();
        OverlayStyleEntry[] styleEntries = new OverlayStyleEntry[availableStyles.length];
        for (int i = 0; i< availableStyles.length; i++) {
            BarcodeCaptureOverlayStyle style = availableStyles[i];
            styleEntries[i] = new OverlayStyleEntry(style, settingsManager.getOverlayStyle() == style);
        }
        return styleEntries;
    }

    void setCurrentStyle(OverlayStyleEntry entry) {
        settingsManager.setOverlayStyle(entry.getStyle());
    }
}
