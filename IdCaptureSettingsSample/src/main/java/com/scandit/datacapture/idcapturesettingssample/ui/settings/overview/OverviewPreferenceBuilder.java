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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.overview;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class OverviewPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private Listener listener;

    public OverviewPreferenceBuilder(Context context) {
        this.context = context;
    }

    public OverviewPreferenceBuilder setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * Add idCapture subpreference.
         */
        Preference idCapturePreference = PreferenceBuilder.preference(
                context, context.getString(R.string.id_capture_category_title)
        );
        idCapturePreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onIdCapturePressed();
            return true;
        });
        parent.addPreference(idCapturePreference);

        /*
         * Add camera subpreference.
         */
        Preference cameraPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.camera_category_title)
        );
        cameraPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onCameraPressed();
            return true;
        });
        parent.addPreference(cameraPreference);

        /*
         * Add view subpreference.
         */
        Preference viewPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.view_category_title)
        );
        viewPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onViewPressed();
            return true;
        });
        parent.addPreference(viewPreference);

        /*
         * Add result subpreference.
         */
        Preference resultPreference = PreferenceBuilder.preference(
                context, context.getString(R.string.result_category_title)
        );
        resultPreference.setOnPreferenceClickListener((pref) -> {
            if (listener != null) listener.onResultPressed();
            return true;
        });
        parent.addPreference(resultPreference);
    }

    public interface Listener {
        void onIdCapturePressed();
        void onCameraPressed();
        void onViewPressed();
        void onResultPressed();
    }
}
