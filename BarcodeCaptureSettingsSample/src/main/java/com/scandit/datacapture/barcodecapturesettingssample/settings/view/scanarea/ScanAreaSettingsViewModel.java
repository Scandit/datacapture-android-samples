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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view.scanarea;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;

@SuppressWarnings("WeakerAccess")
public class ScanAreaSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    void setShowGuides(boolean showGuides) {
        settingsManager.setShouldShowScanAreaGuides(showGuides);
    }

    boolean getShowGuides() {
        return settingsManager.getShouldShowScanAreaGuides();
    }

    FloatWithUnit getTopMargin() {
        return settingsManager.getScanAreaTopMargin();
    }

    FloatWithUnit getRightMargin() {
        return settingsManager.getScanAreaRightMargin();
    }

    FloatWithUnit getBottomMargin() {
        return settingsManager.getScanAreaBottomMargin();
    }

    FloatWithUnit getLeftMargin() {
        return settingsManager.getScanAreaLeftMargin();
    }

    void setRightMargin(FloatWithUnit rightMargin) {
        settingsManager.setScanAreaRightMargin(rightMargin);
    }

    void setBottomMargin(FloatWithUnit bottomMargin) {
        settingsManager.setScanAreaBottomMargin(bottomMargin);
    }

    void setLeftMargin(FloatWithUnit leftMargin) {
        settingsManager.setScanAreaLeftMargin(leftMargin);
    }
}
