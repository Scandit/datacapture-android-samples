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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.anonymization;

import android.content.Context;

import androidx.preference.CheckBoxPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.id.data.IdFieldType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

public class IdCaptureAnonymizationPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCaptureAnonymizationPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        // DropDown preference to choose anonymization mode
        DropDownPreference anonymizationModePreference = PreferenceBuilder.dropDown(
                context,
                Keys.ANONYMIZATION_MODE,
                context.getString(R.string.anonymization_mode_title),
                Defaults.getAnonymizationModeEntries(),
                Defaults.getAnonymizationModeValues(),
                Defaults.getDefaultAnonymizationMode().name()
        );
        parent.addPreference(anonymizationModePreference);

        // Category for anonymized fields
        PreferenceCategory anonymizedFieldsCategory = new PreferenceCategory(context);
        anonymizedFieldsCategory.setTitle(R.string.anonymized_fields_category_title);
        parent.addPreference(anonymizedFieldsCategory);

        // Add a CheckBoxPreference for each IdFieldType
        for (IdFieldType fieldType : IdFieldType.values()) {
            CheckBoxPreference checkBoxPreference = PreferenceBuilder.checkbox(
                    context,
                    Keys.getAnonymizedFieldKey(fieldType),
                    StringUtils.toNameCase(fieldType.name()),
                    Defaults.isFieldAnonymized(fieldType)
            );
            anonymizedFieldsCategory.addPreference(checkBoxPreference);
        }
    }
}
