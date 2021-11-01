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

package com.scandit.datacapture.idcapturesettingssample.mappers;

import com.scandit.datacapture.id.data.ArgentinaIdBarcodeResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class ArgentinaIdBarcodeResultMapper extends ResultMapper {

    private final ArgentinaIdBarcodeResult argentinaIdResult;

    public ArgentinaIdBarcodeResultMapper(CapturedId capturedId) {
        super(capturedId);
        argentinaIdResult = capturedId.getArgentinaIdBarcode();
    }

    /*
     * We extract all the ArgentinaIdBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> getSubtypeEntries() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Document Copy", extractField(argentinaIdResult.getDocumentCopy())));
        result.add(new CaptureResult.Entry("Personal Id Number", extractField(argentinaIdResult.getPersonalIdNumber())));

        return result;
    }
}
