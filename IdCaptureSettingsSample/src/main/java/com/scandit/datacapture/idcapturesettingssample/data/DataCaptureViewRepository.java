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

package com.scandit.datacapture.idcapturesettingssample.data;

import android.content.Context;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.control.TorchSwitchControl;

public class DataCaptureViewRepository {

    /**
     * The DataCaptureContext that created DataCaptureViews will be attached to.
     */
    private final DataCaptureContext dataCaptureContext;

    /**
     * Repository containing the desired settings to be applied to the DataCaptureView.
     */
    private final SettingsRepository settingsRepository;

    public DataCaptureViewRepository(
            DataCaptureContext dataCaptureContext,
            SettingsRepository settingsRepository
    ) {
        this.dataCaptureContext = dataCaptureContext;
        this.settingsRepository = settingsRepository;
    }

    public DataCaptureView buildDataCaptureView(Context context) {
        DataCaptureView view = DataCaptureView.newInstance(context, dataCaptureContext);

        /*
         * Apply the logo anchor from settings.
         */
        view.setLogoAnchor(settingsRepository.getLogoAnchor());

        /*
         * Apply the offset to the logo anchor from settings.
         */
        view.setLogoOffset(
                new PointWithUnit(
                        settingsRepository.getLogoAnchorOffsetX(),
                        settingsRepository.getLogoAnchorOffsetY()
                )
        );

        /*
         * Enable or disable the tap to focus from the settings.
         */
        view.setFocusGesture(settingsRepository.getFocusGesture());

        /*
         * Disable the swipe to zoom.
         */
        view.setZoomGesture(null);

        /*
         * Enable or disable the torch control from the settings.
         */
        if (settingsRepository.getShouldShowTorchControl()) {
            view.addControl(new TorchSwitchControl(context));
        }

        return view;
    }
}
