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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.view;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class LogoPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;
    private Listener listener = null;

    public LogoPreferenceBuilder(Context context) {
        this.context = context;
    }

    public LogoPreferenceBuilder setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * Category containing Logo preferences.
         */
        PreferenceCategory logoCategory =
                PreferenceBuilder.category(context, context.getString(R.string.logo_category_title));
        parent.addPreference(logoCategory);

        /*
         * DropDown preference to choose the logo anchor.
         */
        DropDownPreference logoAnchorPreference = PreferenceBuilder.dropDown(
                context,
                Keys.LOGO_ANCHOR,
                context.getString(R.string.logo_anchor_title),
                Defaults.getSupportedLogoAnchorsEntries(),
                Defaults.getSupportedLogoAnchorsValues(),
                Defaults.getDefaultLogoAnchor().name()
        );
        logoCategory.addPreference(logoAnchorPreference);

        /*
         * Preference to choose the logo anchor x point.
         */
        Preference xPreference = PreferenceBuilder.floatWithUnitPreviewPreference(
                context,
                Keys.LOGO_ANCHOR_OFFSET_X_VALUE,
                Keys.LOGO_ANCHOR_OFFSET_X_MEASURE_UNIT,
                Defaults.getDefaultLogoAnchorX()
        );
        xPreference.setOnPreferenceClickListener(pref -> {
            if (listener != null) {
                listener.onLogoXClicked();
            }
            return true;
        });
        logoCategory.addPreference(xPreference);

        /*
         * Preference to choose the logo anchor y point.
         */
        Preference yPreference = PreferenceBuilder.floatWithUnitPreviewPreference(
                context,
                Keys.LOGO_ANCHOR_OFFSET_Y_VALUE,
                Keys.LOGO_ANCHOR_OFFSET_Y_MEASURE_UNIT,
                Defaults.getDefaultLogoAnchorY()
        );
        yPreference.setOnPreferenceClickListener(pref -> {
            if (listener != null) {
                listener.onLogoYClicked();
            }
            return true;
        });
        logoCategory.addPreference(yPreference);
    }

    public interface Listener {
        void onLogoXClicked();
        void onLogoYClicked();
    }
}
