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
    private static final String SCANDIT_LICENSE_KEY = "AWHjFzlFHa+fLq/kfS8GCBU/hT60NkQeVGQOWhhtRVcDZxJfsD0OY9NK0YErLuxTtTKLC1BLdrvDdsJ1dnxmcx9fDIeeaQlxawtkiq1pmEFxHOvYa3emcbAfOeiwbFPtQEWCWvdc95KoIFxAuDiYcfccdywzH2KONgwmnV9cEcX11FhIPLtX74RLua7VkOukFfNTOGExxhiCq96qZnzGgrgViuagpL0ekK6xv8K4bYt7lVkxloUMM6dFRSZ4aummJ2Q1uZNR78kSGCpCn/uJjaf/5lyNbYWpnxYvsYRPI7jOFYZykI0nIjhjt/ncukCEsz4BQLAh5hp1qocvQ2+dw3ADD8LJLXcnX7JaCOKV5cfHEHGSLR4moTxNtxPXdUNlM5w75iHZub5BsIfkJCknKrLn5oJ15k5Rx4/JnFj11tGLqtfRs+jdtXSGxAb86BxwPM1mEBO/Va1yV//CGku5UWR5MwspCf7pl8OUH7frkCtV4kDB6y5jusSMSIEGnKCLd2sWKE04mAURrpWt8pgsIB89xXPPTgPh1C+nAeMuuEN3dPYAJYrJKvy44w130JrUvxWLcTM1oFVWikC6CluLC7WGgRhZCew0eROnv9neITolB6Gmy04dlF0euA595dJcw2lLTwwxEydGp5gGIIDtofviho7JdHtPrMer/Ptz1/LOVeF55OY9eg8z1Lq2CkZf6cgWZBPa1uakuZzxWXZUprJMdTquhInmqP4ELLxGXhv+CXoT2n0p022+wyiWAXatmhvcK+n2uCWX30SL0Sri1qPmf6Ldtgqj2aFEMLM+LouJg6Ukv0PKUTXlgPW7L0vYrNGtPjvRlaR7Nwph";

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
