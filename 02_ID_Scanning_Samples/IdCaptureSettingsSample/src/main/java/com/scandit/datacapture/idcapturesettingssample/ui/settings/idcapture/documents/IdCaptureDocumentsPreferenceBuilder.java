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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

public class IdCaptureDocumentsPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private final DocumentSelectionType documentSelectionType;
    private Listener listener;

    public IdCaptureDocumentsPreferenceBuilder(Context context,
                                               DocumentSelectionType documentSelectionType) {
        this.context = context;
        this.documentSelectionType = documentSelectionType;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void build(PreferenceGroup parent) {
        IdCaptureDocumentType[] types = IdCaptureDocumentType.values();
        Arrays.sort(types, Comparator.comparing(Enum::name));

        for (int i = 0; i < types.length; i++) {
            IdCaptureDocumentType type = types[i];
            // Preference that allows to enable or disable the document type
            parent.addPreference(createDocumentTypePreference(type));

            // Preference that allows to choose the selected regions for the document type
            parent.addPreference(createDocumentTypeDetailsPreference(type));

            // Divider
            if (i < types.length - 1) {
                parent.addPreference(PreferenceBuilder.category(context));
            }
        }
    }

    /**
     * Creates the preference that allows to enable or disable the document type
     */
    private SwitchPreferenceCompat createDocumentTypePreference(
            IdCaptureDocumentType documentType) {
        return PreferenceBuilder._switch(
                context,
                Keys.getDocumentTypeKey(documentType, documentSelectionType),
                StringUtils.toNameCase(documentType.toString()),
                Defaults.isDocumentTypeSelectedByDefault(documentType, documentSelectionType)
        );
    }

    /**
     * Creates the preference that allows to choose the selected regions or subtypes for the
     * document type.
     */
    private Preference createDocumentTypeDetailsPreference(IdCaptureDocumentType documentType) {
        int titleRes = documentType == IdCaptureDocumentType.REGION_SPECIFIC ? R.string.id_capture_subtypes_title : R.string.id_capture_regions_title;
        Preference preference = PreferenceBuilder.preference(context, context.getString(titleRes));
        preference.setKey(Keys.getDocumentDetailsKey(documentType, documentSelectionType));
        preference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onDocumentClick(documentType);
            return true;
        });

        return preference;
    }

    /**
     * Defines dependencies so that a document regions dependency depends on the related document
     * type dependency.
     */
    public void setPreferenceDependencies(PreferenceScreen preferenceScreen) {
        for (IdCaptureDocumentType type : IdCaptureDocumentType.values()) {
            Preference preference = preferenceScreen.findPreference(
                    Keys.getDocumentDetailsKey(type, documentSelectionType));
            if (preference != null) {
                preference.setDependency(Keys.getDocumentTypeKey(type, documentSelectionType));
            }
        }
    }

    /**
     * Refreshes the preference summaries to reflect the selected regions
     */
    public void refreshDocumentRegions(PreferenceDataStore preferenceDataStore,
                                       PreferenceScreen preferenceScreen) {
        for (IdCaptureDocumentType type : IdCaptureDocumentType.values()) {
            Preference preference = preferenceScreen.findPreference(
                    Keys.getDocumentDetailsKey(type, documentSelectionType));
            if (preference != null) {
                preference.setSummary(
                        DocumentSelectionPreferenceUtils.getSummaryForDocumentType(context,
                                preferenceDataStore, type, documentSelectionType));
            }
        }
    }

    public interface Listener {
        void onDocumentClick(IdCaptureDocumentType documentType);
    }
}
