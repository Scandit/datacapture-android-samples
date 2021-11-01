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

package com.scandit.datacapture.idcapturesettingssample.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository;
import com.scandit.datacapture.idcapturesettingssample.di.Injector;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.FloatWithUnitPreferenceBuilder;

public class FloatWithUnitSettingsFragment extends PreferenceFragmentCompat {

    private static final String FIELD_TITLE_KEY = "title";
    private static final String FIELD_VALUE_KEY = "value";
    private static final String FIELD_DEFAULT_VALUE_KEY = "default_value";
    private static final String FIELD_MEASURE_UNIT_KEY = "measure_unit";
    private static final String FIELD_DEFAULT_MEASURE_UNIT_KEY = "default_measure_unit";

    public static FloatWithUnitSettingsFragment create(
            String title,
            String valueKey,
            String measureUnitKey,
            FloatWithUnit defaultValue
    ) {
        FloatWithUnitSettingsFragment fragment = new FloatWithUnitSettingsFragment();
        Bundle args = new Bundle();
        args.putString(FIELD_TITLE_KEY, title);
        args.putString(FIELD_VALUE_KEY, valueKey);
        args.putFloat(FIELD_DEFAULT_VALUE_KEY, defaultValue.getValue());
        args.putString(FIELD_MEASURE_UNIT_KEY, measureUnitKey);
        args.putString(FIELD_DEFAULT_MEASURE_UNIT_KEY, defaultValue.getUnit().name());
        fragment.setArguments(args);
        return fragment;
    }

    private String title;
    private String valueKey;
    private String measureUnitKey;
    private FloatWithUnit defaultValue;

    /*
     * The settings repository. Used by the preferences to store and retrieve properties.
     */
    private final SettingsRepository settingsRepository = Injector.getInstance().getSettingsRepository();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        title = getArguments().getString(FIELD_TITLE_KEY);
        valueKey = getArguments().getString(FIELD_VALUE_KEY);
        measureUnitKey = getArguments().getString(FIELD_MEASURE_UNIT_KEY);
        defaultValue = new FloatWithUnit(
                getArguments().getFloat(FIELD_DEFAULT_VALUE_KEY, 0f),
                MeasureUnit.valueOf(getArguments().getString(FIELD_DEFAULT_MEASURE_UNIT_KEY))
        );
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setPreferenceDataStore(settingsRepository);

        /*
         * Build the desired preferences and attach them to the preference fragment.
         */
        setPreferenceScreen(
                new FloatWithUnitPreferenceBuilder(
                        requireContext(),
                        valueKey,
                        measureUnitKey,
                        defaultValue
                ).build(getPreferenceManager())
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
        actionBar.setTitle(title);
    }
}
