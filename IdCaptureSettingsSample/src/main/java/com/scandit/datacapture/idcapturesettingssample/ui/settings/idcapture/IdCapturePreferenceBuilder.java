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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture;

import android.content.Context;

import androidx.preference.DropDownPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceGroup;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class IdCapturePreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCapturePreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        /*
         * MultiSelection preference to choose which document types to enable in IdCapture.
         */
        MultiSelectListPreference supportedDocumentsPreference = PreferenceBuilder.multiSelectList(
                context,
                Keys.SUPPORTED_DOCUMENTS,
                context.getString(R.string.supported_documents_title),
                Defaults.getSupportedDocumentsEntries(),
                Defaults.getSupportedDocumentsValues()
        );
        parent.addPreference(supportedDocumentsPreference);

        /*
         * DropDown preference to choose which sides to scan.
         */
        DropDownPreference supportedSidesPreference = PreferenceBuilder.dropDown(
                context,
                Keys.SUPPORTED_SIDES,
                context.getString(R.string.supported_sides_title),
                Defaults.getSupportedSidesEntries(),
                Defaults.getSupportedSidesValues(),
                Defaults.getDefaultSupportedSides().name()
        );
        parent.addPreference(supportedSidesPreference);

        /*
         * MultiSelection preference to choose which image sides are returned as part of the result.
         */
        MultiSelectListPreference supportedImageSidesPreference = PreferenceBuilder.multiSelectList(
                context,
                Keys.SUPPORTED_IMAGES,
                context.getString(R.string.supported_images_title),
                Defaults.getSupportedImagesEntries(),
                Defaults.getSupportedImagesValues()
        );
        parent.addPreference(supportedImageSidesPreference);
    }
}
