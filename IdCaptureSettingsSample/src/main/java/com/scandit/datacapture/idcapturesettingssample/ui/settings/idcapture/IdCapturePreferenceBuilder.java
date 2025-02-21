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
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.id.data.IdCaptureDocumentType;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.FeedbackType;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
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
         * Switch to enable or disable the face image as part of the result.
         */
        SwitchPreferenceCompat faceImageSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.FACE_IMAGE,
                context.getString(R.string.result_with_face_image_title),
                Defaults.isFaceImageEnabled()
        );
        parent.addPreference(faceImageSwitchPreference);

        /*
         * DropDown preference to choose anonymization mode
         */
        DropDownPreference anonymizationModePreference = PreferenceBuilder.dropDown(
                context,
                Keys.ANONYMIZATION_MODE,
                context.getString(R.string.anonymization_mode_title),
                Defaults.getAnonymizationModeEntries(),
                Defaults.getAnonymizationModeValues(),
                Defaults.getDefaultAnonymizationMode().name()
        );
        parent.addPreference(anonymizationModePreference);

        /*
         * DropDown preference to choose which feedback should be enabled for id captured event.
         */
        DropDownPreference idCapturedFeedbackSettings = PreferenceBuilder.dropDown(
                context,
                Keys.ID_CAPTURED_FEEDBACK,
                context.getString(R.string.id_captured_feedback),
                Defaults.getSupportedFeedbackEntries(),
                Defaults.getSupportedFeedbackValues(),
                FeedbackType.SOUND_AND_VIBRATION.name()
        );
        parent.addPreference(idCapturedFeedbackSettings);

        /*
         * DropDown preference to choose which feedback should be enabled for id rejected event.
         */
        DropDownPreference idRejectedFeedbackSettings = PreferenceBuilder.dropDown(
                context,
                Keys.ID_REJECTED_FEEDBACK,
                context.getString(R.string.id_rejected_feedback),
                Defaults.getSupportedFeedbackEntries(),
                Defaults.getSupportedFeedbackValues(),
                FeedbackType.NONE.name()
        );
        parent.addPreference(idRejectedFeedbackSettings);

        /*
         * Switch to enable or disable the extraction of extra information for the back side of
         * European driver's licenses.
         */
        SwitchPreferenceCompat decodeBackOfEuropeanDrivingLicenseSwitchPreference =
                PreferenceBuilder._switch(
                        context,
                        Keys.DECODE_BACK_OF_EUROPEAN_DRIVING_LICENSE,
                        context.getString(R.string.decode_back_of_european_driving_license),
                        Defaults.shouldDecodeBackOfEuropeanDrivingLicense()
                );
        parent.addPreference(decodeBackOfEuropeanDrivingLicenseSwitchPreference);

        /*
         * Switch to enable or disable rejecting voided IDs.
         */
        SwitchPreferenceCompat rejectVoidedIdsSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_VOIDED_IDS,
                context.getString(R.string.reject_voided_ids),
                Defaults.shouldRejectVoidedIds()
        );
        parent.addPreference(rejectVoidedIdsSwitchPreference);

        /*
         * Switch to enable or disable rejecting expired IDs.
         */
        SwitchPreferenceCompat rejectExpiredIdsSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_EXPIRED_IDS,
                context.getString(R.string.reject_expired_ids),
                Defaults.shouldRejectExpiredIds()
        );
        parent.addPreference(rejectExpiredIdsSwitchPreference);

        /*
         * Switch to enable or disable rejecting IDs expiring in within the specified number of days.
         */
        Integer defaultExpirationDayCount = Defaults.shouldRejectIdsExpiringInDays();
        EditTextPreference rejectIdsExpiringInDaysEditTextPreference = PreferenceBuilder.integerEditText(
                context,
                Keys.REJECT_IDS_EXPIRING_IN_DAYS,
                context.getString(R.string.reject_ids_expiring_in_days),
                4,
                defaultExpirationDayCount != null ? String.valueOf(defaultExpirationDayCount) : ""
        );
        parent.addPreference(rejectIdsExpiringInDaysEditTextPreference);

        /*
         * Switch to enable or disable rejecting not Real ID compliant.
         */
        SwitchPreferenceCompat rejectNotRealIdCompliantSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_NOT_REAL_ID_COMPLIANT,
                context.getString(R.string.reject_not_real_id_compliant),
                Defaults.shouldRejectNotRealIdCompliant()
        );
        parent.addPreference(rejectNotRealIdCompliantSwitchPreference);

        /*
         * Switch to enable or disable rejecting forged AAMVA barcodes.
         */
        SwitchPreferenceCompat rejectForgedAamvaBarcodesSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_FORGED_AAMVA_BARCODES,
                context.getString(R.string.reject_forged_aamva_barcodes),
                Defaults.shouldRejectForgedAamvaBarcodes()
        );
        parent.addPreference(rejectForgedAamvaBarcodesSwitchPreference);

        /*
         * Switch to enable or disable rejecting IDs with inconsistent data.
         */
        SwitchPreferenceCompat rejectInconsistentDataSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_INCONSISTENT_DATA,
                context.getString(R.string.reject_inconsistent_data),
                Defaults.shouldRejectInconsistentData()
        );
        parent.addPreference(rejectInconsistentDataSwitchPreference);

        /*
         * Switch to enable or disable rejecting IDs whose holder is below the specified age.
         */
        Integer defaultMinAge = Defaults.shouldRejectHolderBelowAge();
        EditTextPreference rejectHolderBelowAgeEditTextPreference = PreferenceBuilder.integerEditText(
                context,
                Keys.REJECT_HOLDER_BELOW_AGE,
                context.getString(R.string.reject_holder_below_age),
                3,
                defaultMinAge != null ? String.valueOf(defaultMinAge) : ""
        );
        parent.addPreference(rejectHolderBelowAgeEditTextPreference);
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
        boolean isFullScannerEnabled =
                preferenceDataStore.getBoolean(Keys.FULL_SCANNER, Defaults.isFullScannerEnabled());
        scannerPreference.setSummary(
                isFullScannerEnabled ? R.string.full_scanner : R.string.single_side_scanner);
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
    }
}
