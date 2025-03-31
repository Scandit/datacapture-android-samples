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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.feedback;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.FeedbackType;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class IdCaptureFeedbackPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCaptureFeedbackPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        // DropDown preference to choose which feedback should be enabled for id captured event.
        DropDownPreference idCapturedFeedbackSettings = PreferenceBuilder.dropDown(
                context,
                Keys.ID_CAPTURED_FEEDBACK,
                context.getString(R.string.id_captured_feedback),
                Defaults.getSupportedFeedbackEntries(),
                Defaults.getSupportedFeedbackValues(),
                Defaults.getDefaultCapturedFeedback().name()
        );
        parent.addPreference(idCapturedFeedbackSettings);

        // DropDown preference to choose which feedback should be enabled for id rejected event.
        DropDownPreference idRejectedFeedbackSettings = PreferenceBuilder.dropDown(
                context,
                Keys.ID_REJECTED_FEEDBACK,
                context.getString(R.string.id_rejected_feedback),
                Defaults.getSupportedFeedbackEntries(),
                Defaults.getSupportedFeedbackValues(),
                Defaults.getDefaultRejectedFeedback().name()
        );
        parent.addPreference(idRejectedFeedbackSettings);
    }
}
