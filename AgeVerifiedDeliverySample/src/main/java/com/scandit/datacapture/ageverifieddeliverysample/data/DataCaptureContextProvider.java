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

package com.scandit.datacapture.ageverifieddeliverysample.data;

import androidx.annotation.VisibleForTesting;

import com.scandit.datacapture.core.capture.DataCaptureContext;

/**
 * The provider of DataCaptureContext. DataCaptureContext is used to initialize most of the
 * DataCapture components like DataCaptureView or IdCapture.
 */
public class DataCaptureContextProvider {
	// Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    @VisibleForTesting
    public static String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";

    /**
     * The initialized DataCaptureContext.
     */
    private DataCaptureContext dataCaptureContext;

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
