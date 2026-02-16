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

import android.content.Context
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.common.geometry.PointWithUnit
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.control.TorchSwitchControl
import com.scandit.datacapture.core.ui.gesture.TapToFocus

class DataCaptureViewRepository(
    private val dataCaptureContext: DataCaptureContext,
    private val settingsRepository: SettingsRepository,
) {

    fun buildDataCaptureView(context: Context): DataCaptureView =
        DataCaptureView.newInstance(context, dataCaptureContext).apply {
            val settings = settingsRepository.settings.view

            logoAnchor = settings.logoAnchor
            logoOffset = PointWithUnit(
                settings.logoAnchorOffsetX,
                settings.logoAnchorOffsetY
            )
            focusGesture = if (settings.isTapToFocusEnabled) TapToFocus() else null

            // Disable the swipe to zoom.
            zoomGesture = null

            if (settings.isTorchControlEnabled) {
                addControl(TorchSwitchControl(context))
            }
        }
}
