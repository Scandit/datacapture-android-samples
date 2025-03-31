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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.subtypes;

import android.content.Context;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.id.data.RegionSpecificSubtype;
import com.scandit.datacapture.idcapturesettingssample.R;
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

public class IdCaptureSubtypesPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private final DocumentSelectionType documentSelectionType;
    private final List<CheckBoxPreference> subtypePreferences = new ArrayList<>();
    public IdCaptureSubtypesPreferenceBuilder(Context context,
                                              DocumentSelectionType documentSelectionType) {
        this.context = context;
        this.documentSelectionType = documentSelectionType;
    }

    @Override
    public void build(PreferenceGroup parent) {
        RegionSpecificSubtype[] subtypes = RegionSpecificSubtype.values();
        Arrays.sort(subtypes, Comparator.comparing(Enum::name));

        parent.addPreference(createEnableAllSubtypesPreference(parent));
        parent.addPreference(createDisableAllSubtypesPreference(parent));

        for (RegionSpecificSubtype subtype : subtypes) {
            CheckBoxPreference preference = createSubtypePreference(subtype);
            parent.addPreference(preference);
            subtypePreferences.add(preference);
        }
    }

    private Preference createEnableAllSubtypesPreference(PreferenceGroup parent) {
        Preference preference = PreferenceBuilder.preference(context,
                context.getString(R.string.id_capture_subtype_enable_all_title));
        preference.setKey(Keys.ID_SUBTYPE_ENABLE_ALL);
        preference.setOnPreferenceClickListener((pref) -> {
            updateAllSubtypes(parent, true);
            return true;
        });

        return preference;
    }

    private Preference createDisableAllSubtypesPreference(PreferenceGroup parent) {
        Preference preference = PreferenceBuilder.preference(context,
                context.getString(R.string.id_capture_subtype_disable_all_title));
        preference.setKey(Keys.ID_SUBTYPE_DISABLE_ALL);
        preference.setOnPreferenceClickListener((pref) -> {
            updateAllSubtypes(parent, false);
            return true;
        });

        return preference;
    }

    private void updateAllSubtypes(PreferenceGroup parent, boolean checked) {
        for (RegionSpecificSubtype subtype :  RegionSpecificSubtype.values()) {
            String key = Keys.getSubtypeKey(subtype, documentSelectionType);
            CheckBoxPreference preference = parent.findPreference(key);
            if (preference != null) {
                preference.setChecked(checked);
            }
        }
    }

    private CheckBoxPreference createSubtypePreference(RegionSpecificSubtype subtype) {
        return PreferenceBuilder.checkbox(
                context,
                Keys.getSubtypeKey(subtype, documentSelectionType),
                StringUtils.toNameCase(subtype.toString()),
                Defaults.isSubtypeEnabledByDefault(subtype)
        );
    }

    /**
     * Filters subtype preferences that contain the search query
     */
    public void filterPreferences(String searchQuery) {
        String lowerCaseSearchQuery = searchQuery.toLowerCase();

        for (CheckBoxPreference preference : subtypePreferences) {
            String title = preference.getTitle().toString().toLowerCase();
            preference.setVisible(title.contains(lowerCaseSearchQuery));
        }
    }
}
