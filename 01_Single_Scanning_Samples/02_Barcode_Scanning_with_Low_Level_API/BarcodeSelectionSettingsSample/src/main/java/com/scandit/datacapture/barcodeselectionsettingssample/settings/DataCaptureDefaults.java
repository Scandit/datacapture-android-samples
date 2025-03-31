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

package com.scandit.datacapture.barcodeselectionsettingssample.settings;


import android.content.Context;

import com.scandit.datacapture.barcode.selection.capture.BarcodeSelection;
import com.scandit.datacapture.barcode.selection.capture.BarcodeSelectionSettings;
import com.scandit.datacapture.barcode.selection.ui.overlay.BarcodeSelectionBasicOverlayStyle;
import com.scandit.datacapture.barcodeselectionsettingssample.settings.view.overlay.OverlaySettingsFragment;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MarginsWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.ui.DataCaptureView;

public final class DataCaptureDefaults {

    private static DataCaptureDefaults INSTANCE;

    public static DataCaptureDefaults getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataCaptureDefaults(context);
        }
        return INSTANCE;
    }

    private final MarginsWithUnit scanAreaMargins;
    private final PointWithUnit viewPointOfInterest;
    private final PointWithUnit barcodeSelectionPointOfInterest = new PointWithUnit(
            new FloatWithUnit(.5f, MeasureUnit.FRACTION),
            new FloatWithUnit(.5f, MeasureUnit.FRACTION)
    );
    private final BarcodeSelectionBasicOverlayStyle overlayStyle = BarcodeSelectionBasicOverlayStyle.FRAME;
    private final OverlaySettingsFragment.BrushStyle defaultBrushStyle = OverlaySettingsFragment.BrushStyle.DEFAULT;

    private DataCaptureDefaults(Context context) {
        DataCaptureView view = DataCaptureView.newInstance(context, null);
        scanAreaMargins = view.getScanAreaMargins();
        viewPointOfInterest = view.getPointOfInterest();
    }

    public PointWithUnit getBarcodeSelectionPointOfInterest() {
        return barcodeSelectionPointOfInterest;
    }

    public PointWithUnit getViewPointOfInterest() {
        return viewPointOfInterest;
    }

    public MarginsWithUnit getScanAreaMargins() {
        return scanAreaMargins;
    }

    public BarcodeSelectionBasicOverlayStyle getOverlayStyle() {
        return overlayStyle;
    }

    public OverlaySettingsFragment.BrushStyle getDefaultBrushStyle() {
        return defaultBrushStyle;
    }
}
