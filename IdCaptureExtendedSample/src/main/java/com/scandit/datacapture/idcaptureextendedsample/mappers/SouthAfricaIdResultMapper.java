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

package com.scandit.datacapture.idcaptureextendedsample.mappers;

import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.SouthAfricaIdBarcodeResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;

public final class SouthAfricaIdResultMapper extends ResultMapper {

    private final SouthAfricaIdBarcodeResult result;

    public SouthAfricaIdResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getSouthAfricaIdBarcode();
    }

    /*
     * We extract all the SouthAfricaIdBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extractFields() {
        ArrayList<CaptureResult.Entry> result = super.extractFields();

        result.add(new CaptureResult.Entry("Personal Id Number", extractField(this.result.getPersonalIdNumber())));
        result.add(new CaptureResult.Entry("Citizenship Status", extractField(this.result.getCitizenshipStatus())));
        result.add(new CaptureResult.Entry("Country of Birth", extractField(this.result.getCountryOfBirth())));
        result.add(new CaptureResult.Entry("Country of Birth ISO", extractField(this.result.getCountryOfBirthIso())));

        return result;
    }
}
