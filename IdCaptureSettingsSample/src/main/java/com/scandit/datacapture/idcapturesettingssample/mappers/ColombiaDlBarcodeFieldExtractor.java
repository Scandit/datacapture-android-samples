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

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.ColombiaDlBarcodeResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class ColombiaDlBarcodeFieldExtractor extends FieldExtractor {

    private final ColombiaDlBarcodeResult colombiaDlResult;

    public ColombiaDlBarcodeFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        colombiaDlResult = capturedId.getColombiaDlBarcode();
    }

    /*
     * We extract all the ColombiaDlBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Categories", extractField(colombiaDlResult.getCategories())));
        result.add(new CaptureResult.Entry("Identification Type", extractField(colombiaDlResult.getIdentificationType())));

        return result;
    }

    private String extractField(List<String> values) {
        if (values.isEmpty()) {
            return EMPTY_TEXT_VALUE;
        }
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            result.append(extractField(value)).append(", ");
        }
        return result.toString();
    }
}
