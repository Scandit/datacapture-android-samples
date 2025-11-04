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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner;

import android.content.Context;

import androidx.preference.CheckBoxPreference;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.data.PhysicalDocumentScannerType;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.utils.EnumUtils;

public class IdCaptureScannerPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private Listener listener;

    Preference mdocElementsPreference;

    public IdCaptureScannerPreferenceBuilder(Context context) {
        this.context = context;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void build(PreferenceGroup parent) {
        PreferenceCategory physicalDocumentScannerCategory =
                PreferenceBuilder.category(context,
                        context.getString(R.string.physical_document_scanner));
        parent.addPreference(physicalDocumentScannerCategory);

        PhysicalDocumentScannerType[] scannerTypes = PhysicalDocumentScannerType.values();
        DropDownPreference scannerTypePreference = PreferenceBuilder.dropDown(
                context,
                Keys.PHYSICAL_DOCUMENT_SCANNER_TYPE,
                context.getString(R.string.physical_document_scanner_type),
                EnumUtils.getEntryNamesNameCase(scannerTypes),
                EnumUtils.getEntryNames(scannerTypes),
                Defaults.getDefaultPhysicalDocumentScannerType().name()
        );
        parent.addPreference(scannerTypePreference);

        PreferenceDataStore prefs = parent.getPreferenceDataStore();
        String scannerTypeValue = prefs.getString(
                Keys.PHYSICAL_DOCUMENT_SCANNER_TYPE,
                Defaults.getDefaultPhysicalDocumentScannerType().name()
        );
        PhysicalDocumentScannerType currentScannerType = PhysicalDocumentScannerType.fromKey(scannerTypeValue);

        PreferenceCategory singleSideScannerFeaturesCategory =
                PreferenceBuilder.category(context,
                        context.getString(R.string.single_side_scanner_features));
        parent.addPreference(singleSideScannerFeaturesCategory);

        boolean isSingleSideMode = currentScannerType == PhysicalDocumentScannerType.SINGLE_SIDE;
        singleSideScannerFeaturesCategory.setVisible(isSingleSideMode);

        CheckBoxPreference barcodePreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_BARCODE,
                context.getString(R.string.single_side_scanner_barcode),
                Defaults.isSingleSideScannerBarcodeEnabled()
        );
        barcodePreference.setVisible(isSingleSideMode);
        parent.addPreference(barcodePreference);

        CheckBoxPreference mrzPreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_MRZ,
                context.getString(R.string.single_side_scanner_mrz),
                Defaults.isSingleSideScannerMrzEnabled()
        );
        mrzPreference.setVisible(isSingleSideMode);
        parent.addPreference(mrzPreference);

        CheckBoxPreference vizPreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_VIZ,
                context.getString(R.string.single_side_scanner_viz),
                Defaults.isSingleSideScannerVizEnabled()
        );
        vizPreference.setVisible(isSingleSideMode);
        parent.addPreference(vizPreference);

        PreferenceCategory mobileDocumentScannerCategory =
                PreferenceBuilder.category(context,
                        context.getString(R.string.mobile_document_scanner));
        parent.addPreference(mobileDocumentScannerCategory);

        CheckBoxPreference iso1801315Preference = PreferenceBuilder.checkbox(
                context,
                Keys.MOBILE_SCANNER_ISO_18013_15,
                context.getString(R.string.mobile_scanner_iso_18013_15),
                Defaults.isMobileScannerIso1801315Enabled()
        );
        parent.addPreference(iso1801315Preference);

        CheckBoxPreference ocrPreference = PreferenceBuilder.checkbox(
                context,
                Keys.MOBILE_SCANNER_OCR,
                context.getString(R.string.mobile_scanner_ocr),
                Defaults.isMobileScannerOcrEnabled()
        );
        parent.addPreference(ocrPreference);

        mdocElementsPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.mdoc_elements_to_retain_title)
        );
        mdocElementsPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onMdocElementsClick();
            return true;
        });
        parent.addPreference(mdocElementsPreference);

        scannerTypePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            PhysicalDocumentScannerType newScannerType = PhysicalDocumentScannerType.fromKey((String) newValue);
            boolean shouldShowFeatures = newScannerType == PhysicalDocumentScannerType.SINGLE_SIDE;
            singleSideScannerFeaturesCategory.setVisible(shouldShowFeatures);
            barcodePreference.setVisible(shouldShowFeatures);
            mrzPreference.setVisible(shouldShowFeatures);
            vizPreference.setVisible(shouldShowFeatures);
            return true;
        });
    }

    public void refreshPreferenceSummaries(PreferenceDataStore preferenceDataStore) {
        int selectedCount = getMdocElementsCount(preferenceDataStore);
        mdocElementsPreference.setSummary(selectedCount + " elements selected");
    }

    private int getMdocElementsCount(PreferenceDataStore preferenceDataStore) {
        int count = 0;
        for (com.scandit.datacapture.id.data.MobileDocumentDataElement element :
                com.scandit.datacapture.id.data.MobileDocumentDataElement.values()) {
            if (preferenceDataStore.getBoolean(Keys.getMdocElementKey(element), false)) {
                count++;
            }
        }
        return count;
    }


    public interface Listener {
        void onMdocElementsClick();
    }
}
