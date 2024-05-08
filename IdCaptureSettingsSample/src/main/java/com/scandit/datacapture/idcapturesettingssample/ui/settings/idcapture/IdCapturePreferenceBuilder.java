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
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.FeedbackType;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class IdCapturePreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCapturePreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * MultiSelection preference to choose which document types to enable in IdCapture.
         */
        MultiSelectListPreference supportedDocumentsPreference = PreferenceBuilder.multiSelectList(
                context,
                Keys.SUPPORTED_DOCUMENTS,
                context.getString(R.string.supported_documents_title),
                Defaults.getSupportedDocumentsEntries(),
                Defaults.getSupportedDocumentsValues()
        );
        parent.addPreference(supportedDocumentsPreference);

        /*
         * DropDown preference to choose which sides to scan.
         */
        DropDownPreference supportedSidesPreference = PreferenceBuilder.dropDown(
                context,
                Keys.SUPPORTED_SIDES,
                context.getString(R.string.supported_sides_title),
                Defaults.getSupportedSidesEntries(),
                Defaults.getSupportedSidesValues(),
                Defaults.getDefaultSupportedSides().name()
        );
        parent.addPreference(supportedSidesPreference);

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
         * DropDown preference to choose which feedback should be enabled for id capture timeout event.
         */
        DropDownPreference idCaptureTimeoutFeedbackSettings = PreferenceBuilder.dropDown(
                context,
                Keys.ID_CAPTURE_TIMEOUT_FEEDBACK,
                context.getString(R.string.id_capture_timeout_feedback),
                Defaults.getSupportedFeedbackEntries(),
                Defaults.getSupportedFeedbackValues(),
                FeedbackType.NONE.name()
        );
        parent.addPreference(idCaptureTimeoutFeedbackSettings);

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
    }
}
