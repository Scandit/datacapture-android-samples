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

package com.scandit.datacapture.ageverifieddeliverysample.data;

import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlay;
import com.scandit.datacapture.barcode.ui.overlay.BarcodeCaptureOverlayStyle;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinder;
import com.scandit.datacapture.core.ui.viewfinder.RectangularViewfinderStyle;

import java.util.EnumSet;

/**
 * The provider of the BarcodeCapture-related singletons.
 */
public class BarcodeCaptureProvider {
    /**
     * The current BarcodeCapture.
     */
    private final BarcodeCapture barcodeCapture;

    /**
     * BarcodeCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    private final BarcodeCaptureOverlay overlay;

    public BarcodeCaptureProvider() {
        barcodeCapture = BarcodeCapture.forDataCaptureContext(null, createBarcodeCaptureSettings());
        overlay = createBarcodeCaptureOverlay();
    }

    /**
     * Get the current BarcodeCapture.
     */
    public BarcodeCapture getBarcodeCapture() {
        return barcodeCapture;
    }

    /**
     * BarcodeCaptureOverlay displays the additional UI to guide the user through the capture process.
     */
    public BarcodeCaptureOverlay getOverlay() {
        return overlay;
    }

    /**
     * Create the BarcodeCapture's settings.
     */
    private BarcodeCaptureSettings createBarcodeCaptureSettings() {
        BarcodeCaptureSettings settings = new BarcodeCaptureSettings();
        settings.enableSymbologies(ENABLED_SYMBOLOGIES);
        return settings;
    }

    /**
     * Create the BarcodeCapture's overlay.
     */
    private BarcodeCaptureOverlay createBarcodeCaptureOverlay() {
        BarcodeCaptureOverlay overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, null,
                BarcodeCaptureOverlayStyle.FRAME);
        overlay.setViewfinder(new RectangularViewfinder(RectangularViewfinderStyle.SQUARE));
        return overlay;
    }

    /**
     * Enabled barcode symbologies.
     */
    private final static EnumSet<Symbology> ENABLED_SYMBOLOGIES = EnumSet.of(
            Symbology.EAN13_UPCA,
            Symbology.EAN8,
            Symbology.UPCE,
            Symbology.QR,
            Symbology.DATA_MATRIX,
            Symbology.CODE39,
            Symbology.CODE128,
            Symbology.INTERLEAVED_TWO_OF_FIVE
    );
}
