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

package com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes;

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.barcode.data.CompositeType;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntry;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntryA;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntryB;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.compositetypes.type.CompositeTypeEntryC;

import java.util.EnumSet;

@SuppressWarnings("WeakerAccess")
public class CompositeTypesSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    public CompositeTypeEntry[] getCompositeTypeEntries() {
        EnumSet<CompositeType> compositeTypes = getCompositeTypes();
        return new CompositeTypeEntry[]{
                CompositeTypeEntryA.fromCurrentTypes(compositeTypes),
                CompositeTypeEntryB.fromCurrentTypes(compositeTypes),
                CompositeTypeEntryC.fromCurrentTypes(compositeTypes),
        };
    }

    private EnumSet<CompositeType> getCompositeTypes() {
        return settingsManager.getEnabledCompositeTypes();
    }

    public void toggleCompositeType(CompositeTypeEntry entry) {
        EnumSet<CompositeType> compositeTypes = getCompositeTypes();
        if (compositeTypes.contains(entry.compositeType)) {
            compositeTypes.remove(entry.compositeType);
        } else {
            compositeTypes.add(entry.compositeType);
        }
        settingsManager.setEnabledCompositeTypes(compositeTypes);
    }
}
