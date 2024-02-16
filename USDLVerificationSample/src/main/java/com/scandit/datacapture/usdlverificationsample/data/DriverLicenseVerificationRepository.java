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

package com.scandit.datacapture.usdlverificationsample.data;

import androidx.annotation.VisibleForTesting;

import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.internal.sdk.annotations.Mockable;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.verification.aamvabarcode.AamvaBarcodeVerificationTask;
import com.scandit.datacapture.id.verification.aamvabarcode.AamvaBarcodeVerifier;
import com.scandit.datacapture.id.verification.aamvavizbarcode.AamvaVizBarcodeComparisonResult;
import com.scandit.datacapture.id.verification.aamvavizbarcode.AamvaVizBarcodeComparisonVerifier;

/**
 * The repository to verify Driver's licenses.
 */
public class DriverLicenseVerificationRepository {

    /**
     * The verifier that compares the human-readable data from the front side of the document with
     * the data encoded in the barcode, and signals any suspicious differences.
     */
    private AamvaVizBarcodeComparisonVerifier comparisonVerifier;

    /**
     * The verifier that checks the validity of a Driver's License.
     */
    @VisibleForTesting
    public AamvaBarcodeVerifier barcodeVerifier;

    /**
     * Create a new instance of this class.
     */
    public DriverLicenseVerificationRepository(DataCaptureContext dataCaptureContext) {
        this.comparisonVerifier = AamvaVizBarcodeComparisonVerifier.create(dataCaptureContext);
        this.barcodeVerifier = AamvaBarcodeVerifier.create(dataCaptureContext);
    }

    /**
     * Compares the human-readable data from the front side of the document with
     * the data encoded in the barcode, and signals any suspicious differences.
     */
    public AamvaVizBarcodeComparisonResult compareFrontAndBack(CapturedId capturedId) {
        return comparisonVerifier.verify(capturedId);
    }

    /**
     * Checks the validity of a Driver's License. Always returns
     * an error for expired documents.
     */
    public AamvaBarcodeVerificationTask verifyIdOnBarcode(CapturedId capturedId) {
        return barcodeVerifier.verify(capturedId);
    }
}
