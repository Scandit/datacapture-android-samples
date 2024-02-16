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
import androidx.preference.SwitchPreferenceCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class OverlayPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public OverlayPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * Category containing the overlay preferences.
         */
        PreferenceCategory overlayCategory =
                PreferenceBuilder.category(context, context.getString(R.string.overlay_category_title));
        parent.addPreference(overlayCategory);

        /*
         * DropDown preference to choose the overlay style.
         */
        DropDownPreference stylePreference = PreferenceBuilder.dropDown(
                context,
                Keys.OVERLAY_STYLE,
                context.getString(R.string.overlay_style_title),
                Defaults.getSupportedOverlayStylesEntries(),
                Defaults.getSupportedOverlayStylesValues(),
                Defaults.getDefaultOverlayStyle().name()
        );
        overlayCategory.addPreference(stylePreference);

        /*
         * DropDown preference to choose the overlay line style.
         */
        DropDownPreference lineStylePreference = PreferenceBuilder.dropDown(
                context,
                Keys.OVERLAY_LINE_STYLE,
                context.getString(R.string.overlay_line_style_title),
                Defaults.getSupportedOverlayLineStylesEntries(),
                Defaults.getSupportedOverlayLineStylesValues(),
                Defaults.getDefaultOverlayLineStyle().name()
        );
        overlayCategory.addPreference(lineStylePreference);

        /*
         * DropDown preference to choose the capturedBrush.
         */
        DropDownPreference capturedBrushPreference = PreferenceBuilder.dropDown(
                context,
                Keys.CAPTURED_BRUSH,
                context.getString(R.string.captured_brush_title),
                Defaults.getSupportedCapturedBrushEntries(),
                Defaults.getSupportedCapturedBrushValues(),
                Defaults.getDefaultCapturedBrushStyle().name()
        );
        overlayCategory.addPreference(capturedBrushPreference);

        /*
         * DropDown preference to text hint position
         */
        DropDownPreference textHintPositionPreference = PreferenceBuilder.dropDown(
                context,
                Keys.TEXT_HINT_POSITION,
                context.getString(R.string.text_hint_position_title),
                Defaults.getTextHintPositionEntries(),
                Defaults.getTextHintPositionValues(),
                Defaults.getDefaultTextHintPosition().name()
        );
        overlayCategory.addPreference(textHintPositionPreference);

        SwitchPreferenceCompat showTextHintsSwitch = PreferenceBuilder._switch(
                context,
                Keys.SHOW_TEXT_HINTS,
                context.getString(R.string.show_text_hints_title),
                Defaults.getDefaultShowTextHints()
        );
        overlayCategory.addPreference(showTextHintsSwitch);

        EditTextPreference viewfinderFrontTextPreference = PreferenceBuilder.editText(
                context,
                Keys.VIEWFINDER_FRONT_TEXT,
                context.getString(R.string.viewfinder_front_text_title)
        );
        overlayCategory.addPreference(viewfinderFrontTextPreference);

        EditTextPreference viewfinderBackTextPreference = PreferenceBuilder.editText(
                context,
                Keys.VIEWFINDER_BACK_TEXT,
                context.getString(R.string.viewfinder_back_text_title)
        );
        overlayCategory.addPreference(viewfinderBackTextPreference);
    }
}
