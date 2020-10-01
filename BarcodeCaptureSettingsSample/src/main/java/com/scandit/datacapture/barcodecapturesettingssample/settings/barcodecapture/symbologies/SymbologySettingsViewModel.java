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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.symbologies;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;

import java.util.List;

public class SymbologySettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();
    private final List<SymbologyDescription> symbologyDescriptions;

    public SymbologySettingsViewModel() {
        symbologyDescriptions = SymbologyDescription.all();
    }

    void setAllSymbologiesEnabled(boolean enabled) {
        settingsManager.enableAllSymbologies(enabled);
    }

    SymbologySettingsEntry[] getSymbologyDescriptionsAndEnabledState() {
        SymbologySettingsEntry[] descriptionsAndEnabledState =
                new SymbologySettingsEntry[symbologyDescriptions.size()];
        for (int i = 0; i < symbologyDescriptions.size(); i++) {
            SymbologyDescription description = symbologyDescriptions.get(i);
            String currentIdentifier = description.getIdentifier();
            descriptionsAndEnabledState[i] = new SymbologySettingsEntry(
                    description, settingsManager.isSymbologyEnabled(currentIdentifier)
            );
        }
        return descriptionsAndEnabledState;
    }
}
