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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.common;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;

public class FloatWithUnitPreferenceBuilder {

    private final Context context;
    private final String valueKey;
    private final String measureUnitKey;
    private final FloatWithUnit defaultValue;

    public FloatWithUnitPreferenceBuilder(
            Context context,
            String valueKey,
            String measureUnitKey,
            FloatWithUnit defaultValue
    ) {
        this.context = context;
        this.valueKey = valueKey;
        this.defaultValue = defaultValue;
        this.measureUnitKey = measureUnitKey;
    }

    public PreferenceScreen build(PreferenceManager preferenceManager) {
        /*
         * Root container for the preferences.
         */
        PreferenceScreen screen = preferenceManager.createPreferenceScreen(context);

        /*
         * Category containing the value and the measure unit.
         */
        PreferenceCategory containerCategory = PreferenceBuilder.category(context);
        screen.addPreference(containerCategory);

        /*
         * Preference for the point's value.
         */
        EditTextPreference valuePreference = PreferenceBuilder.numericEditText(
                context,
                valueKey,
                context.getString(R.string.value),
                5,
                String.valueOf(defaultValue.getValue())
        );
        containerCategory.addPreference(valuePreference);

        /*
         * Preference for the point's measure unit.
         */
        DropDownPreference measureUnitPreference = PreferenceBuilder.dropDown(
                context,
                measureUnitKey,
                context.getString(R.string.measure_unit),
                Defaults.getSupportedMeasureUnitEntries(),
                Defaults.getSupportedMeasureUnitValues(),
                defaultValue.getUnit().name()
        );
        containerCategory.addPreference(measureUnitPreference);

        return screen;
    }
}
