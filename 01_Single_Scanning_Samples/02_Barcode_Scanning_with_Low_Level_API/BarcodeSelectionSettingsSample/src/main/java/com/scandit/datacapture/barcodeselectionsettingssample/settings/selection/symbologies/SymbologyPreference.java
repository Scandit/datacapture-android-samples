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

package com.scandit.datacapture.barcodeselectionsettingssample.settings.selection.symbologies;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.barcodeselectionsettingssample.R;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.SharedPrefsConstants;

public final class SymbologyPreference extends Preference
        implements CompoundButton.OnCheckedChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchCompat switchEnabled;
    private Symbology symbology;

    public SymbologyPreference(Context context, Symbology symbology) {
        super(context, null, androidx.preference.R.attr.switchPreferenceCompatStyle, 0);
        init(symbology);
    }

    private void init(Symbology symbology) {
        setIconSpaceReserved(false);
        setKey(SharedPrefsConstants.getSymbologyEnabledKey(symbology));
        setTitle(SymbologyDescription.create(symbology).getReadableName());
        setWidgetLayoutResource(R.layout.divider_and_switch_pref);
        this.symbology = symbology;
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        preferenceManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setBackgroundResource(R.drawable.bg_white_ripple);
        switchEnabled = (SwitchCompat) holder.findViewById(R.id.preference_switch);
        updateValue(getPersistedBoolean(false));
        holder.setDividerAllowedAbove(true);
        holder.setDividerAllowedBelow(true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPrefsConstants.getSymbologyEnabledKey(symbology))) {
            updateValue(getPersistedBoolean(false));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        persistBoolean(isChecked);
    }

    private void updateValue(boolean checked) {
        if (switchEnabled != null) {
            switchEnabled.setOnCheckedChangeListener(null);
            switchEnabled.setChecked(checked);
            switchEnabled.setOnCheckedChangeListener(this);
        }
    }

    public Symbology getSymbology() { return symbology; }
}
