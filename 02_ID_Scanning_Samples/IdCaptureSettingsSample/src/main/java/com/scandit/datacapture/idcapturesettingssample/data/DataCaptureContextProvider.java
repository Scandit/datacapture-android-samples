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

import androidx.annotation.VisibleForTesting;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.idcapturesettingssample.BuildConfig;

/**
 * The provider for DataCaptureContext. DataCaptureContext is used to initialize most of the
 * DataCapture components like DataCaptureView or IdCapture.
 */
public class DataCaptureContextProvider {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    @VisibleForTesting
    public static String SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY;

    /**
     * The initialized DataCaptureContext.
     */
    private final DataCaptureContext dataCaptureContext;

    /**
     * Create a new instance of this class.
     */
    public DataCaptureContextProvider() {
        /*
         * Create DataCaptureContext using your license key.
         */
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);
    }

    /**
     * Get the initialized DataCaptureContext.
     */
    public DataCaptureContext getDataCaptureContext() {
        return dataCaptureContext;
    }
}
