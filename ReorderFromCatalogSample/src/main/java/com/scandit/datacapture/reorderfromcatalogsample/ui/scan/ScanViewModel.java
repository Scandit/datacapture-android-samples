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

package com.scandit.datacapture.reorderfromcatalogsample.ui.scan;

import androidx.lifecycle.ViewModel;

import com.scandit.datacapture.reorderfromcatalogsample.data.BarcodeSelectionRepository;
import com.scandit.datacapture.reorderfromcatalogsample.data.CameraRepository;
import com.scandit.datacapture.reorderfromcatalogsample.di.Injector;

/**
 * The view model for the scanning screen.
 */
public class ScanViewModel extends ViewModel {

    /**
     * The repository to interact with the device's camera. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final CameraRepository cameraRepository = Injector.getInstance().getCameraRepository();

    /**
     * The repository to interact with the BarcodeSelection mode. We use our own dependency injection
     * to obtain it, but you may use your favorite framework, like Dagger or Hilt instead.
     */
    private final BarcodeSelectionRepository barcodeSelectionRepository =
            Injector.getInstance().getBarcodeSelectionRepository();

    /**
     * Resume the capture process by turning on the camera.
     */
    void startCapture() {
        cameraRepository.turnOnCamera();
    }

    /**
     * Pause the capture process by putting the camera in standby.
     */
    void stopCapture() {
        cameraRepository.putCameraInStandby();
    }

    /**
     * Select all unselected codes in the current BarcodeSelectionSession.
     */
    void selectCurrentCodes() {
        barcodeSelectionRepository.selectAllUnselectedCodes();
    }
}
