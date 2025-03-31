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

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.camera.CameraSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.IdCaptureSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.result.ResultSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.view.ViewSettingsFragment;

public class OverviewSettingsFragment extends PreferenceFragmentCompat
        implements OverviewPreferenceBuilder.Listener {

    private static final String TAG_ID_CAPTURE = "id_capture";
    private static final String TAG_CAMERA = "camera";
    private static final String TAG_VIEW = "view";
    private static final String TAG_RESULT = "result";

    public static OverviewSettingsFragment create() {
        return new OverviewSettingsFragment();
    }

    /*
     * The settings repository. Used by the preferences to store and retrieve properties.
     */
    private final SettingsRepository settingsRepository = Injector.getInstance().getSettingsRepository();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        /*
         * Build the desired preferences and attach them to the preference fragment.
         */
        setPreferenceScreen(
                new SettingsPreferenceBuilder(requireContext())
                        .addSection(
                                new OverviewPreferenceBuilder(requireContext())
                                        .setListener(this)
                        )
                        .build(getPreferenceManager())
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * Update the actionBar title and navigation every time we're resuming the fragment.
         */
        setupActionBar();
    }

    private void setupActionBar() {
        /*
         * Make sure the back arrow is shown and the correct title for the fragment is displayed.
         */
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.main_settings);
    }

    @Override
    public void onIdCapturePressed() {
        goToFragment(
                IdCaptureSettingsFragment.create(),
                TAG_ID_CAPTURE
        );
    }

    @Override
    public void onCameraPressed() {
        goToFragment(
                CameraSettingsFragment.create(),
                TAG_CAMERA
        );
    }

    @Override
    public void onViewPressed() {
        goToFragment(
                ViewSettingsFragment.create(),
                TAG_VIEW
        );
    }

    @Override
    public void onResultPressed() {
        goToFragment(
                ResultSettingsFragment.create(),
                TAG_RESULT
        );
    }

    private void goToFragment(Fragment fragment, String tag) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.scan_fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }
}
