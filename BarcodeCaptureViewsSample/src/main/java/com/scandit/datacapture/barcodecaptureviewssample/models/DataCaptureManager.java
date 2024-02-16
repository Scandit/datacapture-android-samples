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

package com.scandit.datacapture.barcodecaptureviewssample.models;

import androidx.annotation.NonNull;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.capture.DataCaptureContextListener;
import com.scandit.datacapture.core.capture.DataCaptureMode;
import com.scandit.datacapture.core.common.ContextStatus;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.FrameSource;

public final class DataCaptureManager implements DataCaptureContextListener {
	// There is a Scandit sample license key set below here.
	// This license key is enabled for sample evaluation only.
	// If you want to build your own application, get your license key by signing up for a trial at https://ssl.scandit.com/dashboard/sign-up?p=test
    private static final String SCANDIT_LICENSE_KEY = "AfUkdmKlRiP5FdlOFQnOhu4V3j5LFKttPGTWXFd7CkuRaTAstDqq78RrBm2ZG9LRu1T8CNgP6oLScGrUoEwfmP1TUXonIGCl2g9Fo5NYtmK/aEV8FX/YcdRKfWS5bJrTcWGDHdcsJxT6Me5C3RMdWZkdqeR5GEjDzT6dO4ZPWOBbNLjpkgZ0/MjtYQPKqSV+bSZC7+ekFaXovSKWfXV89BXtta/6sZHFJOMKxyvzh6zw5yA+NDR67OXoWKCrrNq4AOuBlt1ZelIHCqjQgTy/SZG110eJr5e4pth38Bx0fXE8FGX92BoxwJr1EG+P5CEJF8EFMy2zf87aJQYuzHmg0nM7czcNqLUd9F23uxntZYjKlwgWmmSzev/ozaumEvbW9RVW1bUQmV8pQ1SWILBuzQPeAw8iWOWgnTH18tH7cT+fUJumvM2rn7LWx9JYLAKBKRuwe2sDh3l5eqobZKdarIRsKVgXa4pw+gkYKuplzTo+Bzh70rbmtgq3IJ8hSpdoZITzfUQSwXkrgdQa5Cmrpxz9gXManBRt01h3eFXG7znZU9w0+uzzV/b5e6MQcPncODrCQOq0kfEBYgRoLAwVCOKnxyWQkqRbUpsTN2wy2MTg10flYhR/zf1eXdiUjgPUhWj8LtmgxJELYky7uMu46abfCkAw73e+12iJmlf9/tmTFk34La9ZQiF/BYps5h327ZW8qobay+Esx1i9dsaFKYt/nCN8jZdUYD/df+/vApyK4PMbph9EPRe5u0alg8BqpEExnkQsy1W7r85yngO/rxSXsY6rTMoTXb/87ul8uQnsrD41ZLtFdzo0OlbNTeNOI1mJz/E6/SOLbRRK";

    public static final DataCaptureManager CURRENT = new DataCaptureManager();

    public final BarcodeCapture barcodeCapture;
    public final DataCaptureContext dataCaptureContext;
    public final Camera camera;
    public boolean isLicenseValid = false;

    private DataCaptureManager() {
        // Create data capture context using your license key and set the camera as the frame source.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Register self as a listener to get informed of Scandit license status changes.
        dataCaptureContext.addListener(this);

        // Use the default camera with the recommended camera settings for the BarcodeCapture mode
        // and set it as the frame source of the context. The camera is off by default and must be
        // turned on to start streaming frames to the data capture context for recognition.
        camera = Camera.getDefaultCamera(BarcodeCapture.createRecommendedCameraSettings());
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException(
                    "Sample depends on a camera, which failed to initialize.");
        }

        // Create new barcode capture mode with default settings. Each mode of the sample will
        // apply its own settings specific to its use case.
        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, new BarcodeCaptureSettings());
    }

    @Override
    public void onFrameSourceChanged(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull FrameSource frameSource
    ) {}

    @Override
    public void onModeAdded(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull DataCaptureMode dataCaptureMode
    ) {}

    @Override
    public void onStatusChanged(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull ContextStatus contextStatus
    ) {
        isLicenseValid = contextStatus.isValid();
    }

    @Override
    public void onModeRemoved(
            @NonNull DataCaptureContext dataCaptureContext, @NonNull DataCaptureMode dataCaptureMode
    ) {}

    @Override
    public void onObservationStarted(@NonNull DataCaptureContext dataCaptureContext) {}

    @Override
    public void onObservationStopped(@NonNull DataCaptureContext dataCaptureContext) {}
}
