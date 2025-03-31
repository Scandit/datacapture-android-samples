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

import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.List;

public class SettingsPreferenceBuilder {

    private final Context context;

    private final List<SectionPreferenceBuilder> sections = new ArrayList<>();

    public SettingsPreferenceBuilder(Context context) {
        this.context = context;
    }

    public SettingsPreferenceBuilder addSection(SectionPreferenceBuilder section) {
        sections.add(section);
        return this;
    }

    public PreferenceScreen build(PreferenceManager preferenceManager) {
        /*
         * Root container for the preferences.
         */
        PreferenceScreen screen = preferenceManager.createPreferenceScreen(context);

        for (SectionPreferenceBuilder builder : sections) {
            builder.build(screen);
        }
        return screen;
    }
}
