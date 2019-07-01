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

package com.scandit.datacapture.searchandfindsample;

import androidx.lifecycle.ViewModel;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.searchandfindsample.models.DataCaptureManager;

public final class MainViewModel extends ViewModel {

    private Camera camera = DataCaptureManager.CURRENT.camera;

    void startFrameSource() {
        if (camera != null) {
            camera.switchToDesiredState(FrameSourceState.ON);
        }
    }

    void stopFrameSource() {
        if (camera != null) {
            camera.switchToDesiredState(FrameSourceState.OFF);
        }
    }
}
