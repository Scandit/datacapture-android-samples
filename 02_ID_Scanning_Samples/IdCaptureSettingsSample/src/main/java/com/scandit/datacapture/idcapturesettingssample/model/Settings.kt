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

package com.scandit.datacapture.idcapturesettingssample.model

import com.scandit.datacapture.core.common.geometry.Anchor
import com.scandit.datacapture.core.common.geometry.FloatWithUnit
import com.scandit.datacapture.core.common.geometry.MeasureUnit
import com.scandit.datacapture.core.source.CameraPosition
import com.scandit.datacapture.core.source.TorchState
import com.scandit.datacapture.core.source.VideoResolution
import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.data.IdAnonymizationMode
import com.scandit.datacapture.id.data.IdFieldType
import com.scandit.datacapture.id.data.IdImageType
import com.scandit.datacapture.id.data.MobileDocumentDataElement
import com.scandit.datacapture.id.ui.IdLayoutLineStyle
import com.scandit.datacapture.id.ui.IdLayoutStyle
import com.scandit.datacapture.id.ui.TextHintPosition

data class Settings(
    val idCapture: IdCaptureSettings = IdCaptureSettings(),
    val camera: CameraSettings = CameraSettings(),
    val view: ViewSettings = ViewSettings(),
    val result: ResultSettings = ResultSettings()
) {
    data class IdCaptureSettings(
        // Document selection
        val acceptedDocuments: List<IdCaptureDocument> = emptyList(),
        val rejectedDocuments: List<IdCaptureDocument> = emptyList(),

        // Image types
        val imageTypes: Set<IdImageType> = emptySet(),

        // Anonymization
        val anonymizationMode: IdAnonymizationMode = IdAnonymizationMode.FIELDS_ONLY,
        val anonymizedFields: Set<IdFieldType> = emptySet(),
        val anonymizeDefaultFields: Boolean = true,

        // Feedback
        val capturedFeedback: FeedbackType = FeedbackType.SOUND_AND_VIBRATION,
        val rejectedFeedback: FeedbackType = FeedbackType.NONE,

        // Physical document scanner
        val physicalDocumentScannerType: PhysicalDocumentScannerType = PhysicalDocumentScannerType.SINGLE_SIDE,
        val singleSideScannerBarcodeEnabled: Boolean = false,
        val singleSideScannerMrzEnabled: Boolean = false,
        val singleSideScannerVizEnabled: Boolean = false,
        val decodeBackOfEuropeanDrivingLicense: Boolean = false,

        // Mobile document scanner
        val mobileScannerIso1801315Enabled: Boolean = false,
        val mobileScannerOcrEnabled: Boolean = false,
        val mdocElementsToRetain: Set<MobileDocumentDataElement> = emptySet(),

        // Rejection criteria
        val rejectVoidedIds: Boolean = false,
        val rejectExpiredIds: Boolean = false,
        val rejectIdsExpiringInDays: Int? = null,
        val rejectNotRealIdCompliant: Boolean = false,
        val rejectForgedAamvaBarcodes: Boolean = false,
        val rejectInconsistentData: Boolean = false,
        val rejectHolderBelowAge: Int? = null,
        val rejectionTimeoutSeconds: Int = 6
    )

    data class CameraSettings(
        val position: CameraPosition = CameraPosition.WORLD_FACING,
        val torchState: TorchState = TorchState.OFF,
        val preferredResolution: VideoResolution = VideoResolution.AUTO
    )

    data class ViewSettings(
        // Logo
        val logoAnchor: Anchor = Anchor.BOTTOM_RIGHT,
        val logoAnchorOffsetX: FloatWithUnit = FloatWithUnit(0f, MeasureUnit.FRACTION),
        val logoAnchorOffsetY: FloatWithUnit = FloatWithUnit(0f, MeasureUnit.FRACTION),

        // Overlay
        val overlayStyle: IdLayoutStyle = IdLayoutStyle.ROUNDED,
        val overlayLineStyle: IdLayoutLineStyle = IdLayoutLineStyle.BOLD,
        val capturedBrushStyle: BrushStyle = BrushStyle.DEFAULT,

        // Text hints
        val viewfinderFrontText: String = "",
        val viewfinderBackText: String = "",
        val textHintPosition: TextHintPosition = TextHintPosition.ABOVE_VIEWFINDER,
        val showTextHints: Boolean = true,

        // Gestures
        val isTapToFocusEnabled: Boolean = true,

        // Controls
        val isTorchControlEnabled: Boolean = false
    )

    data class ResultSettings(
        val continuousScanEnabled: Boolean = false
    )
}
