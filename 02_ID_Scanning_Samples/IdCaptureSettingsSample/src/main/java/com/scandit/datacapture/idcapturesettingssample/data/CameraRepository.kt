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

package com.scandit.datacapture.idcapturesettingssample.data

import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.source.TorchListener
import com.scandit.datacapture.core.source.TorchState
import com.scandit.datacapture.id.capture.IdCapture

/**
 * The repository to interact with the device's camera.
 */
class CameraRepository(
    private val dataCaptureContext: DataCaptureContext,
    private val settingsRepository: SettingsRepository,
) : TorchListener {

    /**
     * The wrapper over the device's camera.
     */
    private var camera: Camera? = null

    fun turnOnCamera() {
        /*
         * Switch the camera on. The camera frames will be sent to IdCapture for processing.
         * Additionally the preview will appear on the screen. The camera is started asynchronously,
         * and you may notice a small delay before the preview appears.
         */
        camera?.switchToDesiredState(FrameSourceState.ON)
    }

    fun turnOffCamera() {
        /*
         * Switch the camera off to stop streaming frames. The camera is stopped asynchronously.
         */
        camera?.switchToDesiredState(FrameSourceState.OFF)
    }

    /**
     * Request a new Camera to be built with the selected position.
     */
    private fun createCamera() {
        val settings = settingsRepository.settings.camera

        // Clear the torch listener linked to the existing camera.
        camera?.removeTorchListener(this)

        // Set the device's default camera as DataCaptureContext's FrameSource. DataCaptureContext
        // passes the frames from it's FrameSource to the added modes to perform capture or
        // tracking.
        camera = Camera.getCamera(settings.position)
        camera?.addTorchListener(this)

        // Set the camera as the DataCaptureContext's frame source.
        dataCaptureContext.setFrameSource(camera)
    }

    /**
     * Synchronize the torch state and the activation status of the torch in the settings.
     */
    override fun onTorchStateChanged(state: TorchState) {
        if (state != TorchState.AUTO) {
            settingsRepository.updateCamera {
                it.copy(torchState = state)
            }
        }
    }

    /**
     * Apply camera settings changes. If camera position changes, a recreation is necessary.
     */
    fun applyCameraSettings() {
        val settings = settingsRepository.settings.camera

        if (camera == null || camera?.position != settings.position) {
            val wasOn = camera?.currentState == FrameSourceState.ON
            createCamera()

            if (wasOn) {
                turnOnCamera()
            }
        }

        val cameraSettings = IdCapture.createRecommendedCameraSettings()
        cameraSettings.preferredResolution = settings.preferredResolution
        camera?.applySettings(cameraSettings)

        if (camera?.desiredTorchState != settings.torchState) {
            camera?.desiredTorchState = settings.torchState
        }
    }
}
