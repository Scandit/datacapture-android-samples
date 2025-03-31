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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner;

import android.content.Context;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreferenceCompat;

import com.scandit.datacapture.idcapturesettingssample.R;
import com.scandit.datacapture.idcapturesettingssample.data.Defaults;
import com.scandit.datacapture.idcapturesettingssample.data.Keys;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.PreferenceBuilder;
import com.scandit.datacapture.idcapturesettingssample.ui.settings.common.SectionPreferenceBuilder;

public class IdCaptureScannerPreferenceBuilder implements SectionPreferenceBuilder {

    private final Context context;

    public IdCaptureScannerPreferenceBuilder(Context context) {
        this.context = context;
    }

    @Override
    public void build(PreferenceGroup parent) {
        // Switch to enable or disable the full scanner
        SwitchPreferenceCompat fullScannerPreference = PreferenceBuilder._switch(
                context,
                Keys.FULL_SCANNER,
                context.getString(R.string.full_scanner),
                Defaults.isFullScannerEnabled()
        );
        parent.addPreference(fullScannerPreference);

        PreferenceDataStore prefs = parent.getPreferenceDataStore();
        boolean isFullScannerEnabled = prefs.getBoolean(Keys.FULL_SCANNER, Defaults.isFullScannerEnabled());

        // Category containing the Single Side Scanner preferences.
        PreferenceCategory singleSideScannerCategory =
                PreferenceBuilder.category(context,
                        context.getString(R.string.single_side_scanner));
        parent.addPreference(singleSideScannerCategory);

        // Switch to enable or disable barcode scanning
        CheckBoxPreference barcodePreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_BARCODE,
                context.getString(R.string.single_side_scanner_barcode),
                Defaults.isSingleSideScannerBarcodeEnabled()
        );
        barcodePreference.setEnabled(!isFullScannerEnabled);
        parent.addPreference(barcodePreference);

        // Switch to enable or disable Machine Readable Zone scanning
        CheckBoxPreference mrzPreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_MRZ,
                context.getString(R.string.single_side_scanner_mrz),
                Defaults.isSingleSideScannerMrzEnabled()
        );
        mrzPreference.setEnabled(!isFullScannerEnabled);
        parent.addPreference(mrzPreference);

        // Switch to enable or disable Visual Inspection Zone scanning
        CheckBoxPreference vizPreference = PreferenceBuilder.checkbox(
                context,
                Keys.SINGLE_SIDE_SCANNER_VIZ,
                context.getString(R.string.single_side_scanner_viz),
                Defaults.isSingleSideScannerVizEnabled()
        );
        vizPreference.setEnabled(!isFullScannerEnabled);
        parent.addPreference(vizPreference);

        fullScannerPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean isEnabled = (boolean) newValue;
            barcodePreference.setEnabled(!isEnabled);
            mrzPreference.setEnabled(!isEnabled);
            vizPreference.setEnabled(!isEnabled);
            return true;
        });
    }
}
