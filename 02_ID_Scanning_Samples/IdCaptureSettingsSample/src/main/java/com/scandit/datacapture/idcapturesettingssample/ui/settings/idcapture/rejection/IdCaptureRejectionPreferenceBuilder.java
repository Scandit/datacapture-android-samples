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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.rejection;

import android.content.Context;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class IdCaptureRejectionPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCaptureRejectionPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        // Switch to enable or disable rejecting expired IDs.
        SwitchPreferenceCompat rejectExpiredIdsSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_EXPIRED_IDS,
                context.getString(R.string.reject_expired_ids),
                Defaults.shouldRejectExpiredIds()
        );
        parent.addPreference(rejectExpiredIdsSwitchPreference);

        // Switch to enable or disable rejecting not Real ID compliant.
        SwitchPreferenceCompat rejectNotRealIdCompliantSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_NOT_REAL_ID_COMPLIANT,
                context.getString(R.string.reject_not_real_id_compliant),
                Defaults.shouldRejectNotRealIdCompliant()
        );
        parent.addPreference(rejectNotRealIdCompliantSwitchPreference);

        // Switch to enable or disable rejecting forged AAMVA barcodes.
        SwitchPreferenceCompat rejectForgedAamvaBarcodesSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_FORGED_AAMVA_BARCODES,
                context.getString(R.string.reject_forged_aamva_barcodes),
                Defaults.shouldRejectForgedAamvaBarcodes()
        );
        parent.addPreference(rejectForgedAamvaBarcodesSwitchPreference);

        // Switch to enable or disable rejecting IDs with inconsistent data.
        SwitchPreferenceCompat rejectInconsistentDataSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_INCONSISTENT_DATA,
                context.getString(R.string.reject_inconsistent_data),
                Defaults.shouldRejectInconsistentData()
        );
        parent.addPreference(rejectInconsistentDataSwitchPreference);

        // Switch to enable or disable rejecting IDs whose holder is below the specified age.
        Integer defaultMinAge = Defaults.shouldRejectHolderBelowAge();
        EditTextPreference rejectHolderBelowAgeEditTextPreference = PreferenceBuilder.integerEditText(
                context,
                Keys.REJECT_HOLDER_BELOW_AGE,
                context.getString(R.string.reject_holder_below_age),
                3,
                defaultMinAge != null ? String.valueOf(defaultMinAge) : ""
        );
        parent.addPreference(rejectHolderBelowAgeEditTextPreference);

        // Switch to enable or disable rejecting IDs expiring in within the specified number of days.
        Integer defaultExpirationDayCount = Defaults.shouldRejectIdsExpiringInDays();
        EditTextPreference rejectIdsExpiringInDaysEditTextPreference = PreferenceBuilder.integerEditText(
                context,
                Keys.REJECT_IDS_EXPIRING_IN_DAYS,
                context.getString(R.string.reject_ids_expiring_in_days),
                4,
                defaultExpirationDayCount != null ? String.valueOf(defaultExpirationDayCount) : ""
        );
        parent.addPreference(rejectIdsExpiringInDaysEditTextPreference);

        // Switch to enable or disable rejecting voided IDs.
        SwitchPreferenceCompat rejectVoidedIdsSwitchPreference = PreferenceBuilder._switch(
                context,
                Keys.REJECT_VOIDED_IDS,
                context.getString(R.string.reject_voided_ids),
                Defaults.shouldRejectVoidedIds()
        );
        parent.addPreference(rejectVoidedIdsSwitchPreference);
    }
}
