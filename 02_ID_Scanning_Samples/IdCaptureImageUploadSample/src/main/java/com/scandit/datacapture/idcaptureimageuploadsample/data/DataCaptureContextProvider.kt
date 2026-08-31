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

package com.scandit.datacapture.idcaptureimageuploadsample.data

import androidx.annotation.VisibleForTesting
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.idcaptureimageuploadsample.BuildConfig

/**
 * The provider for DataCaptureContext. DataCaptureContext is used to initialize most of the
 * DataCapture components like IdCapture.
 */
object DataCaptureContextProvider {

    // Add your license key to `secrets.properties` and it will be automatically added to the
    // BuildConfig field `BuildConfig.SCANDIT_LICENSE_KEY`.
    @VisibleForTesting
    @JvmField
    var SCANDIT_LICENSE_KEY = BuildConfig.SCANDIT_LICENSE_KEY

    /**
     * The DataCaptureContext initialized using your license key.
     */
    val dataCaptureContext: DataCaptureContext by lazy {
        DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)
    }
}
