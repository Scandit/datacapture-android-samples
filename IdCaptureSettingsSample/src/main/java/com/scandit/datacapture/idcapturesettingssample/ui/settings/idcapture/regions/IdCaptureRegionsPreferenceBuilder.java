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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.regions;

import android.content.Context;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.id.capture.IdCaptureRegions;
import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class IdCaptureRegionsPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private final IdCaptureDocumentType documentType;
    private final DocumentSelectionType documentSelectionType;
    private CheckBoxPreference anyRegionPreference;
    private final List<CheckBoxPreference> regionPreferences = new ArrayList<>();

    public IdCaptureRegionsPreferenceBuilder(Context context, IdCaptureDocumentType documentType,
                                             DocumentSelectionType documentSelectionType) {
        this.context = context;
        this.documentType = documentType;
        this.documentSelectionType = documentSelectionType;
    }

    @Override
    public void build(PreferenceGroup parent) {
        IdCaptureRegion[] regions = IdCaptureRegion.values();
        Arrays.sort(regions,
                Comparator.comparing((IdCaptureRegion region) -> region != IdCaptureRegion.ANY && region != IdCaptureRegion.EU_AND_SCHENGEN)
                        .thenComparing(IdCaptureRegions::getDescription));

        for (IdCaptureRegion region : regions) {
            CheckBoxPreference preference = createRegionPreference(region);
            if (region == IdCaptureRegion.ANY) {
                anyRegionPreference = preference;
            }
            regionPreferences.add(preference);
            parent.addPreference(preference);
        }
    }

    private CheckBoxPreference createRegionPreference(IdCaptureRegion region) {
        CheckBoxPreference preference = PreferenceBuilder.checkbox(
                context,
                Keys.getDocumentRegionKey(documentType, region, documentSelectionType),
                IdCaptureRegions.getDescription(region),
                Defaults.isRegionEnabledByDefault(region)
        );

        preference.setOnPreferenceChangeListener((pref, newValue) -> {
            boolean isEnabled = (boolean) newValue;

            // Ensure that at least one region is selected to prevent misconfigurations. If no
            // region is selected, IdCaptureRegion.ANY is selected automatically.
            if (!isEnabled && getEnabledRegionCount() <= 1) {
                if (region == IdCaptureRegion.ANY) {
                    return false;
                } else {
                    anyRegionPreference.setChecked(true);
                }
            }
            return true;
        });

        return preference;
    }

    /**
     * Returns the number of enabled regions
     */
    private int getEnabledRegionCount() {
        int enabledRegionCount = 0;

        for (CheckBoxPreference preference : regionPreferences) {
            if (preference.isChecked()) {
                enabledRegionCount++;
            }
        }

        return enabledRegionCount;
    }

    /**
     * Filters region preferences that contain the search query
     */
    public void filterPreferences(String searchQuery) {
        String lowerCaseSearchQuery = searchQuery.toLowerCase();

        for (CheckBoxPreference preference : regionPreferences) {
            String title = preference.getTitle().toString().toLowerCase();
            preference.setVisible(title.contains(lowerCaseSearchQuery));
        }
    }
}
