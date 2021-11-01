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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.view;

import android.content.Context;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class ControlsPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public ControlsPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * Category containing the controls preferences.
         */
        PreferenceCategory gesturesCategory =
                PreferenceBuilder.category(context, context.getString(R.string.controls_category_title));
        parent.addPreference(gesturesCategory);

        /*
         * Switch to enable or disable the torch control.
         */
        SwitchPreferenceCompat torchControlSwitch = PreferenceBuilder._switch(
                context,
                Keys.TORCH_CONTROL,
                context.getString(R.string.torch_control_title),
                Defaults.getDefaultTorchControlEnabled()
        );
        gesturesCategory.addPreference(torchControlSwitch);
    }
}
