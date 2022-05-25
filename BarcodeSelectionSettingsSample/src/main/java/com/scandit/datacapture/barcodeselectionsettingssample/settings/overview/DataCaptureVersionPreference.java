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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.overview;

import android.content.Context;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.core.capture.DataCaptureVersion;

import static com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants.DATACAPTURE_VERSION_KEY;

public final class DataCaptureVersionPreference extends Preference {

    public DataCaptureVersionPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setKey(DATACAPTURE_VERSION_KEY);
        setTitle(getContext().getString(R.string.datacapture_version, DataCaptureVersion.VERSION_STRING));
        setIconSpaceReserved(false);
        setSelectable(false);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        ((RelativeLayout) titleView.getParent()).setGravity(Gravity.CENTER);
        TextViewCompat.setTextAppearance(titleView, android.R.style.TextAppearance_Material_Caption);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
    }
}
