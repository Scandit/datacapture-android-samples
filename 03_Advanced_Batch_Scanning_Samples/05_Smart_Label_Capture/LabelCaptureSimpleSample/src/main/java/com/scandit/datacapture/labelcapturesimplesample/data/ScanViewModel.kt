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
package com.scandit.datacapture.labelcapturesimplesample.data

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.label.capture.LabelCapture
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowOverlay
import com.scandit.datacapture.labelcapturesimplesample.Settings
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class ScanViewModel : ViewModel() {
    // Create the camera using label capture recommended camera settings.
    private val camera = Camera.getDefaultCamera(LabelCapture.createRecommendedCameraSettings())

    // Create the data capture context and set its camera.
    val dataCaptureContext = DataCaptureContext.forLicenseKey(Settings.LICENSE_KEY)
        .also { dataCaptureContext ->
            dataCaptureContext.setFrameSource(camera)
        }

    private val _messages = Channel<String>(Channel.CONFLATED)
    val messages: ReceiveChannel<String> = _messages

    private val labelCapture = LabelCaptureProvider.buildLabelCapture(dataCaptureContext)
    private var validationFlowOverlay: LabelCaptureValidationFlowOverlay? = null

    fun buildLabelCaptureOverlay(context: Context) =
        LabelCaptureProvider.buildOverlay(context, labelCapture)

    fun getValidationFlowOverlay(
        context: Context,
    ): LabelCaptureValidationFlowOverlay {
        validationFlowOverlay?.let { oldOverlay ->
            (oldOverlay.parent as? ViewGroup)?.removeView(oldOverlay)
        }
        return validationFlowOverlay ?: LabelCaptureProvider.buildValidationFlowOverlay(
            context,
            labelCapture
        ) { label ->
            _messages.trySend(label.toString())
        }.also {
            validationFlowOverlay = it
        }
    }

    fun onDialogDismissed() {
        camera?.switchToDesiredState(FrameSourceState.ON)
        labelCapture.isEnabled = true
    }

    fun onPause() {
        validationFlowOverlay?.onPause()
    }

    fun onResume() {
        validationFlowOverlay?.onResume()
    }
}
