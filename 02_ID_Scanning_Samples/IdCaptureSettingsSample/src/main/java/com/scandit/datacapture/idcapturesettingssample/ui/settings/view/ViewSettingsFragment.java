
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

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.FloatWithUnitSettingsFragment;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SettingsPreferenceBuilder;

public class ViewSettingsFragment extends PreferenceFragmentCompat
        implements LogoPreferenceBuilder.Listener {

    private static final String LOGO_X_TAG = "logo_x";
    private static final String LOGO_Y_TAG = "logo_y";

    public static ViewSettingsFragment create() {
        return new ViewSettingsFragment();
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
                        .addSection(new OverlayPreferenceBuilder(requireContext()))
                        .addSection(new LogoPreferenceBuilder(requireContext()).setListener(this))
                        .addSection(new GesturesPreferenceBuilder(requireContext()))
                        .addSection(new ControlsPreferenceBuilder(requireContext()))
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
        actionBar.setTitle(R.string.view_category_title);
    }

    @Override
    public void onLogoXClicked() {
        if (requireActivity().getSupportFragmentManager().findFragmentByTag(LOGO_X_TAG) == null) {
            /*
             * Show the logo's x coordinate settings fragment.
             */
            Fragment xFragment = FloatWithUnitSettingsFragment.create(
                    getString(R.string.logo_anchor_offset_x_title),
                    Keys.LOGO_ANCHOR_OFFSET_X_VALUE,
                    Keys.LOGO_ANCHOR_OFFSET_X_MEASURE_UNIT,
                    Defaults.getDefaultLogoAnchorX()
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.scan_fragment_container, xFragment, LOGO_X_TAG)
                    .addToBackStack(LOGO_X_TAG)
                    .commit();
        }
    }

    @Override
    public void onLogoYClicked() {
        if (requireActivity().getSupportFragmentManager().findFragmentByTag(LOGO_Y_TAG) == null) {
            /*
             * Show the logo's x coordinate settings fragment.
             */
            Fragment yFragment = FloatWithUnitSettingsFragment.create(
                    getString(R.string.logo_anchor_offset_y_title),
                    Keys.LOGO_ANCHOR_OFFSET_Y_VALUE,
                    Keys.LOGO_ANCHOR_OFFSET_Y_MEASURE_UNIT,
                    Defaults.getDefaultLogoAnchorY()
            );
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.scan_fragment_container, yFragment, LOGO_Y_TAG)
                    .addToBackStack(LOGO_Y_TAG)
                    .commit();
        }
    }

    private void goToFragment(Fragment fragment, String tag) {

    }
}
