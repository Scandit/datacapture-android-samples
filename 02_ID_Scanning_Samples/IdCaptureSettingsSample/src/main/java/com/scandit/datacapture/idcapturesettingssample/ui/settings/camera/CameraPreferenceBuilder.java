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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.camera;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class CameraPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public CameraPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * DropDown preference to choose which camera position to scan with.
         */
        DropDownPreference cameraPositionPreference = PreferenceBuilder.dropDown(
                context,
                Keys.CAMERA_POSITION,
                context.getString(R.string.camera_position_title),
                Defaults.getSupportedCameraPositionEntries(),
                Defaults.getSupportedCameraPositionValues(),
                Defaults.getDefaultCameraPosition().name()
        );
        parent.addPreference(cameraPositionPreference);

        /*
         * Switch to enable or disable the torch.
         */
        SwitchPreferenceCompat torchPreference = PreferenceBuilder._switch(
                context,
                Keys.TORCH_STATE,
                context.getString(R.string.camera_torch_title),
                Defaults.getTorchEnabledDefault()
        );
        parent.addPreference(torchPreference);

        /*
         * DropDown preference to choose the video resolution.
         */
        DropDownPreference resolutionPreference = PreferenceBuilder.dropDown(
                context,
                Keys.PREFERRED_RESOLUTION,
                context.getString(R.string.camera_preferred_resolution_title),
                Defaults.getSupportedResolutionsEntries(),
                Defaults.getSupportedResolutionsValues(),
                Defaults.getDefaultResolution().name()
        );
        parent.addPreference(resolutionPreference);
    }
}
