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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.camera;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.BasePreferenceFragment;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledListPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledPreferenceCategory;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSeekBarPreference;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.common.StyledSwitchPreference;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.VideoResolution;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.CAMERA_FOCUS_RANGE_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.CAMERA_POSITION_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.CAMERA_PREFERRED_RESOLUTION_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.CAMERA_SETTINGS_CATEGORY_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.CAMERA_ZOOM_FACTOR_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.TOP_CAMERA_CATEGORY_KEY;
import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.TORCH_KEY;

public final class CameraSettingsFragment extends BasePreferenceFragment {

    public static CameraSettingsFragment newInstance() {
        return new CameraSettingsFragment();
    }

    @Override
    protected String getTitle() {
        return getString(R.string.camera);
    }

    @Override
    protected void addPreferencesToScreen(PreferenceScreen screen) {
        // Top Category.
        PreferenceCategory topCategory = new StyledPreferenceCategory(requireContext(), TOP_CAMERA_CATEGORY_KEY, null);
        screen.addPreference(topCategory);
        topCategory.addPreference(createCameraPositionPreference());
        topCategory.addPreference(createTorchPreference());

        // Camera Settings Category.
        PreferenceCategory cameraSettingsCategory = new StyledPreferenceCategory(requireContext(), CAMERA_SETTINGS_CATEGORY_KEY, R.string.camera_settings);
        screen.addPreference(cameraSettingsCategory);
        cameraSettingsCategory.addPreference(createPreferredResolutionPreference());
        cameraSettingsCategory.addPreference(createZoomFactorPreference());
    }

    private Preference createCameraPositionPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), CAMERA_POSITION_KEY, R.string.camera_position, true
        );
        String[] positions = new String[]{CameraPosition.WORLD_FACING.name(), CameraPosition.USER_FACING.name()};
        preference.setEntryValues(positions);
        preference.setEntries(R.array.camera_positions);
        if (preference.getValue() == null) {
            preference.setValueIndex(0);
        }
        return preference;
    }

    private Preference createTorchPreference() {
        return new StyledSwitchPreference(requireContext(), TORCH_KEY, R.string.torch_state, true);
    }

    private Preference createPreferredResolutionPreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), CAMERA_PREFERRED_RESOLUTION_KEY, R.string.preferred_resolution, true
        );
        String[] resolutions = new String[] {
                VideoResolution.AUTO.name(), VideoResolution.HD.name(), VideoResolution.FULL_HD.name()
        };
        preference.setEntryValues(resolutions);
        preference.setEntries(getResources().getStringArray(R.array.video_resolutions));
        if (preference.getValue() == null) {
            preference.setValueIndex(0);
        }
        return preference;
    }

    private Preference createZoomFactorPreference() {
        StyledSeekBarPreference preference = new StyledSeekBarPreference(
                requireContext(), CAMERA_ZOOM_FACTOR_KEY, R.string.zoom_factor, true, 1, 20
        );
        if (preference.getValue() == 0) {
            preference.setValue(1);
        }
        return preference;
    }

    private Preference createFocusRangePreference() {
        ListPreference preference = new StyledListPreference(
                requireContext(), CAMERA_FOCUS_RANGE_KEY, R.string.focus_range, true
        );
        return preference;
    }
}
