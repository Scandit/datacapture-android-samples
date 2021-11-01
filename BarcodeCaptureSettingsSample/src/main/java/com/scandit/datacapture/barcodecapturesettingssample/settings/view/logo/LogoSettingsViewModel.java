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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.logo;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.ui.LogoStyle;

@SuppressWarnings("WeakerAccess")
public class LogoSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    Anchor[] provideAnchors() {
        return Anchor.values();
    }

    Anchor getCurrentAnchor() {
        return settingsManager.getLogoAnchor();
    }

    FloatWithUnit getCurrentAnchorXOffset() {
        return settingsManager.getAnchorXOffset();
    }

    FloatWithUnit getCurrentAnchorYOffset() {
        return settingsManager.getAnchorYOffset();
    }

    void setAnchor(Anchor anchor) {
        settingsManager.setLogoAnchor(anchor);
    }

    LogoStyleEntry[] getLogoStyles() {
        LogoStyle[] availableStyles = LogoStyle.values();
        LogoStyleEntry[] styleEntries = new LogoStyleEntry[availableStyles.length];
        for (int i = 0; i< availableStyles.length; i++) {
            LogoStyle style = availableStyles[i];
            styleEntries[i] = new LogoStyleEntry(style, settingsManager.getLogoStyle() == style);
        }
        return styleEntries;
    }

    void setCurrentStyle(LogoStyleEntry entry) {
        settingsManager.setLogoStyle(entry.getStyle());
    }
}
