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
package com.scandit.datacapture.pricecapturesample.scan

import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.label.capture.LabelCapture
import com.scandit.datacapture.label.capture.LabelCaptureFeedback
import com.scandit.datacapture.label.capture.LabelCaptureSettings
import com.scandit.datacapture.label.capture.LabelDefinition

/**
 * Owns the Scandit Data Capture pipeline for the sample: the camera, the data capture context, and
 * the [LabelCapture] mode configured with the pre-built price-label definition.
 */
class DataCaptureManager(licenseKey: String) {

    val camera: Camera? = Camera.getDefaultCamera(LabelCapture.createRecommendedCameraSettings())

    val dataCaptureContext: DataCaptureContext = DataCaptureContext.forLicenseKey(licenseKey).also {
        it.setFrameSource(camera)
    }

    // `createPriceCaptureDefinition` is the SDK's pre-built definition for shelf price labels. It
    // captures the product barcode (a BARCODE-type field) and the printed price (a TEXT-type field)
    // in a single shot, with the symbologies and text patterns of European price tags already tuned.
    val labelCapture: LabelCapture = LabelCapture.forDataCaptureContext(
        dataCaptureContext,
        LabelCaptureSettings.builder()
            .addLabel(LabelDefinition.createPriceCaptureDefinition(PRICE_LABEL_NAME))
            .build()
    ).also {
        // Silence the mode's automatic capture feedback, which fires on every frame a label stays
        // in view. The overlay handler emits feedback once per newly captured label instead. The
        // no-arg `LabelCaptureFeedback` is silent (no sound, no vibration).
        it.feedback = LabelCaptureFeedback()
    }

    companion object {
        private const val PRICE_LABEL_NAME = "price-label"
    }
}
