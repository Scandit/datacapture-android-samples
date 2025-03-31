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

import androidx.preference.PreferenceDataStore;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.id.data.IdCaptureRegion;
import com.scandit.datacapture.id.data.RegionSpecificSubtype;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DocumentSelectionPreferenceUtils {

    /**
     * Returns the selected regions for the specified document type
     */
    public static List<IdCaptureRegion> getSelectedRegions(PreferenceDataStore preferenceDataStore,
                                                           IdCaptureDocumentType type,
                                                           DocumentSelectionType documentSelectionType) {
        List<IdCaptureRegion> selectedRegions = new ArrayList<>();

        for (IdCaptureRegion region : IdCaptureRegion.values()) {
            boolean isSelected = preferenceDataStore.getBoolean(
                    Keys.getDocumentRegionKey(type, region, documentSelectionType),
                    Defaults.isRegionEnabledByDefault(region));

            if (isSelected) {
                selectedRegions.add(region);
            }
        }

        return selectedRegions;
    }

    /**
     * Returns the selected region-specific subtypes
     */
    public static List<RegionSpecificSubtype> getSelectedSubtypes(PreferenceDataStore preferenceDataStore,
                                                                  DocumentSelectionType documentSelectionType) {
        List<RegionSpecificSubtype> selectedSubtypes = new ArrayList<>();

        for (RegionSpecificSubtype subtype : RegionSpecificSubtype.values()) {
            boolean isSelected = preferenceDataStore.getBoolean(
                    Keys.getSubtypeKey(subtype, documentSelectionType),
                    Defaults.isSubtypeEnabledByDefault(subtype));

            if (isSelected) {
                selectedSubtypes.add(subtype);
            }
        }

        return selectedSubtypes;
    }

    /**
     * Returns a human readable string that represents the specified document type
     */
    public static String getSummaryForDocumentType(Context context,
                                                   PreferenceDataStore preferenceDataStore,
                                                   IdCaptureDocumentType type,
                                                   DocumentSelectionType documentSelectionType) {
        if (type == IdCaptureDocumentType.REGION_SPECIFIC) {
            List<RegionSpecificSubtype> subtypes =
                    DocumentSelectionPreferenceUtils.getSelectedSubtypes(preferenceDataStore, documentSelectionType);
            return DocumentSelectionPreferenceUtils.getSummaryForSelectedSubtypes(context, subtypes);
        } else {
            List<IdCaptureRegion> selectedRegions =
                    DocumentSelectionPreferenceUtils.getSelectedRegions(preferenceDataStore, type, documentSelectionType);
            return DocumentSelectionPreferenceUtils.getSummaryForSelectedRegions(context, selectedRegions);
        }
    }

    /**
     * Returns a human readable string that represents the specified selected regions
     */
    private static String getSummaryForSelectedRegions(Context context,
                                                      List<IdCaptureRegion> selectedRegions) {
        if (selectedRegions.contains(IdCaptureRegion.ANY)) {
            return context.getString(R.string.id_capture_any_region);
        }

        Collections.sort(selectedRegions, Comparator.comparing(Enum::name));
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < selectedRegions.size(); i++) {
            IdCaptureRegion region = selectedRegions.get(i);
            stringBuilder.append(StringUtils.toNameCase(region.toString()));

            if (i < selectedRegions.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a human readable string that represents the specified selected region-specific subtypes
     */
    private static String getSummaryForSelectedSubtypes(Context context,
                                                      List<RegionSpecificSubtype> selectedSubtypes) {
        if (selectedSubtypes.isEmpty()) {
            return context.getString(R.string.settings_value_none);
        } else if (selectedSubtypes.size() == RegionSpecificSubtype.values().length) {
            return context.getString(R.string.id_capture_any_subtype);
        }

        Collections.sort(selectedSubtypes, Comparator.comparing(Enum::name));
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < selectedSubtypes.size(); i++) {
            RegionSpecificSubtype subtype = selectedSubtypes.get(i);
            stringBuilder.append(StringUtils.toNameCase(subtype.toString()));

            if (i < selectedSubtypes.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the selected document types
     */
    public static List<IdCaptureDocumentType> getSelectedDocumentTypes(
            PreferenceDataStore preferenceDataStore,
            DocumentSelectionType documentSelectionType) {
        List<IdCaptureDocumentType> selectedDocumentTypes = new ArrayList<>();

        for (IdCaptureDocumentType type : IdCaptureDocumentType.values()) {
            boolean isSelected = preferenceDataStore.getBoolean(
                    Keys.getDocumentTypeKey(type, documentSelectionType),
                    Defaults.isDocumentTypeSelectedByDefault(type, documentSelectionType)
            );

            if (isSelected) {
                selectedDocumentTypes.add(type);
            }
        }

        return selectedDocumentTypes;
    }
}
