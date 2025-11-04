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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.data.PhysicalDocumentScannerType;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionPreferenceUtils;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.DocumentSelectionType;
import com.scandit.datacapture.idcapturesettingssample.utils.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IdCapturePreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private Listener listener;

    Preference acceptedDocumentsPreference;
    Preference rejectedDocumentsPreference;
    Preference scannerPreference;

    public IdCapturePreferenceBuilder(Context context) {
        this.context = context;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

        // Preference to choose which documents are accepted.
        acceptedDocumentsPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_accepted_documents_title)
        );
        acceptedDocumentsPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onAcceptedDocumentsClick();
            return true;
        });
        parent.addPreference(acceptedDocumentsPreference);

        // Preference to choose which documents are rejected.
        rejectedDocumentsPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_rejected_documents_title)
        );
        rejectedDocumentsPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onRejectedDocumentsClick();
            return true;
        });
        parent.addPreference(rejectedDocumentsPreference);

        // Preference to choose which scanner is enabled.
        scannerPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_scanner_title)
        );
        scannerPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onScannerClick();
            return true;
        });
        parent.addPreference(scannerPreference);

        /*
         * MultiSelection preference to choose which image types are returned as part of the result.
         */
        MultiSelectListPreference supportedImageSidesPreference = PreferenceBuilder.multiSelectList(
                context,
                Keys.IMAGE_TYPES,
                context.getString(R.string.image_types_title),
                Defaults.getSupportedImagesEntries(),
                Defaults.getSupportedImagesValues()
        );
        parent.addPreference(supportedImageSidesPreference);

        // Feedback sub-preferences.
        Preference feedbackPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_feedback_title)
        );
        feedbackPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onFeedbackClick();
            return true;
        });
        parent.addPreference(feedbackPreference);

        // Rejection sub-preferences.
        Preference rejectionPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_rejection_title)
        );
        rejectionPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onRejectionClick();
            return true;
        });
        parent.addPreference(rejectionPreference);

        // Switch to enable or disable the extraction of extra information for the back side of
        // European driver's licenses.
        SwitchPreferenceCompat decodeBackOfEuropeanDrivingLicenseSwitchPreference =
                PreferenceBuilder._switch(
                        context,
                        Keys.DECODE_BACK_OF_EUROPEAN_DRIVING_LICENSE,
                        context.getString(R.string.decode_back_of_european_driving_license),
                        Defaults.shouldDecodeBackOfEuropeanDrivingLicense()
                );
        parent.addPreference(decodeBackOfEuropeanDrivingLicenseSwitchPreference);
    }

    /**
     * Refreshes the preference summaries to reflect the selected document types and regions
     */
    public void refreshPreferenceSummaries(PreferenceDataStore preferenceDataStore) {
        // Accepted documents
        acceptedDocumentsPreference.setSummary(
                getSummaryForDocuments(preferenceDataStore, DocumentSelectionType.ACCEPTED));

        // Rejected documents
        rejectedDocumentsPreference.setSummary(
                getSummaryForDocuments(preferenceDataStore, DocumentSelectionType.REJECTED));

        // Scanner
        scannerPreference.setSummary(getScannerSummary(preferenceDataStore));
    }

    /**
     * Returns a human readable string that represents the scanner configuration
     */
    private String getScannerSummary(PreferenceDataStore preferenceDataStore) {
        java.util.List<String> summaryParts = new java.util.ArrayList<>();

        // Physical document scanner
        String scannerTypeValue = preferenceDataStore.getString(
                Keys.PHYSICAL_DOCUMENT_SCANNER_TYPE,
                Defaults.getDefaultPhysicalDocumentScannerType().name()
        );
        PhysicalDocumentScannerType scannerType = PhysicalDocumentScannerType.fromKey(scannerTypeValue);

        switch (scannerType) {
            case SINGLE_SIDE:
                java.util.List<String> physicalOptions = new java.util.ArrayList<>();
                if (preferenceDataStore.getBoolean(
                        Keys.SINGLE_SIDE_SCANNER_BARCODE,
                        Defaults.isSingleSideScannerBarcodeEnabled())) {
                    physicalOptions.add(context.getString(R.string.single_side_scanner_barcode));
                }
                if (preferenceDataStore.getBoolean(
                        Keys.SINGLE_SIDE_SCANNER_MRZ,
                        Defaults.isSingleSideScannerMrzEnabled())) {
                    physicalOptions.add(context.getString(R.string.single_side_scanner_mrz));
                }
                if (preferenceDataStore.getBoolean(
                        Keys.SINGLE_SIDE_SCANNER_VIZ,
                        Defaults.isSingleSideScannerVizEnabled())) {
                    physicalOptions.add(context.getString(R.string.single_side_scanner_viz));
                }

                if (!physicalOptions.isEmpty()) {
                    summaryParts.add(context.getString(
                            R.string.scanner_summary_physical,
                            String.join(", ", physicalOptions)
                    ));
                } else {
                    summaryParts.add(context.getString(
                            R.string.scanner_summary_physical,
                            context.getString(R.string.physical_document_scanner_single_side_type)
                    ));
                }
                break;
            case FULL:
                summaryParts.add(context.getString(
                        R.string.scanner_summary_physical,
                        context.getString(R.string.physical_document_scanner_full_type)
                ));
                break;
        }

        // Mobile document scanner
        java.util.List<String> mobileOptions = new java.util.ArrayList<>();
        if (preferenceDataStore.getBoolean(
                Keys.MOBILE_SCANNER_ISO_18013_15,
                Defaults.isMobileScannerIso1801315Enabled())) {
            mobileOptions.add(context.getString(R.string.mobile_scanner_iso_18013_15));
        }
        if (preferenceDataStore.getBoolean(
                Keys.MOBILE_SCANNER_OCR,
                Defaults.isMobileScannerOcrEnabled())) {
            mobileOptions.add(context.getString(R.string.mobile_scanner_ocr));
        }

        if (!mobileOptions.isEmpty()) {
            summaryParts.add(context.getString(
                    R.string.scanner_summary_mobile,
                    String.join(", ", mobileOptions)
            ));
        }

        return String.join(" | ", summaryParts);
    }

    /**
     * Returns a human readable string that represents the selected documents
     */
    private String getSummaryForDocuments(
            PreferenceDataStore preferenceDataStore,
            DocumentSelectionType documentSelectionType
    ) {
        List<IdCaptureDocumentType> selectedDocumentTypes =
                DocumentSelectionPreferenceUtils.getSelectedDocumentTypes(preferenceDataStore, documentSelectionType);

        if (selectedDocumentTypes.isEmpty()) {
            return context.getString(R.string.settings_value_none);
        }

        StringBuilder stringBuilder = new StringBuilder();
        Collections.sort(selectedDocumentTypes, Comparator.comparing(Enum::name));

        for (int i = 0; i < selectedDocumentTypes.size(); i++) {
            IdCaptureDocumentType documentType = selectedDocumentTypes.get(i);
            String details = DocumentSelectionPreferenceUtils.getSummaryForDocumentType(context,
                            preferenceDataStore, documentType, documentSelectionType);

            stringBuilder.append(StringUtils.toNameCase(documentType.toString()));
            stringBuilder.append(" (");
            stringBuilder.append(details);
            stringBuilder.append(")");

            if (i < selectedDocumentTypes.size() - 1) {
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public interface Listener {
        void onAcceptedDocumentsClick();
        void onRejectedDocumentsClick();
        void onScannerClick();
        void onFeedbackClick();
        void onRejectionClick();
    }
}
