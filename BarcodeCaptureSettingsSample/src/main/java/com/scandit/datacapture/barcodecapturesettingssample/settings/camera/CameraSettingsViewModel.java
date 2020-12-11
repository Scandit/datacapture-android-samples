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

package com.scandit.datacapture.barcodecapturesettingssample.settings.camera;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.barcodecapturesettingssample.models.SettingsManager;
import com.scandit.datacapture.core.source.CameraPosition;
import com.scandit.datacapture.core.source.FocusGestureStrategy;
import com.scandit.datacapture.core.source.TorchState;
import com.scandit.datacapture.core.source.VideoResolution;

@SuppressWarnings("WeakerAccess")
public class CameraSettingsViewModel extends ViewModel {

    private final SettingsManager settingsManager = SettingsManager.getCurrentSettings();

    public CameraSettingsPositionEntry[] provideCameraPositionsAndEnableState() {
        CameraPosition currentCameraPosition = settingsManager.getCurrentCameraPosition();
        CameraPosition[] allowedCameraPositions = new CameraPosition[]{
                CameraPosition.WORLD_FACING, CameraPosition.USER_FACING
        };
        CameraSettingsPositionEntry[] entries =
                new CameraSettingsPositionEntry[allowedCameraPositions.length];
        for (int i = 0; i < allowedCameraPositions.length; i++) {
            CameraPosition cameraPosition = allowedCameraPositions[i];
            entries[i] = new CameraSettingsPositionEntry(
                    cameraPosition, currentCameraPosition == cameraPosition
            );
        }
        return entries;
    }

    void setCameraPosition(CameraPosition cameraPosition) {
        settingsManager.setCameraPosition(cameraPosition);
    }

    VideoResolution getVideoResolution() {
        return settingsManager.getVideoResolution();
    }

    void setVideoResolution(VideoResolution videoResolution) {
        settingsManager.setVideoResolution(videoResolution);
    }

    float getZoomFactor() {
        return settingsManager.getZoomFactor();
    }

    void setZoomFactor(float zoomFactor) {
        settingsManager.setZoomFactor(zoomFactor);
    }

    float getZoomGestureZoomFactor() {
        return settingsManager.getZoomGestureZoomFactor();
    }

    void setZoomGestureZoomFactor(float zoomFactor) {
        settingsManager.setZoomGestureZoomFactor(zoomFactor);
    }

    FocusGestureStrategy getFocusGestureStrategy() {
        return settingsManager.getFocusGestureStrategy();
    }

    void setFocusGestureStrategy(FocusGestureStrategy strategy) {
        settingsManager.setFocusGestureStrategy(strategy);
    }
}
