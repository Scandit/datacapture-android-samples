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

import com.scandit.datacapture.id.data.AamvaBarcodeResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class AamvaFieldExtractor extends FieldExtractor {

    private final AamvaBarcodeResult aamvaResult;

    public AamvaFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        aamvaResult = capturedId.getAamvaBarcode();
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    protected List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();
        result.add(new CaptureResult.Entry("AAMVA Version", extractField(aamvaResult.getAamvaVersion())));
        result.add(new CaptureResult.Entry("Jurisdiction Version", extractField(aamvaResult.getJurisdictionVersion())));
        result.add(new CaptureResult.Entry("IIN", extractField(aamvaResult.getIin())));
        result.add(new CaptureResult.Entry("Is Real ID", extractField(aamvaResult.isRealId())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(aamvaResult.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction ISO", extractField(aamvaResult.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Eye Color", extractField(aamvaResult.getEyeColor())));
        result.add(new CaptureResult.Entry("Hair Color", extractField(aamvaResult.getHairColor())));
        result.add(new CaptureResult.Entry("Height (inch)", extractField(aamvaResult.getHeightInch())));
        result.add(new CaptureResult.Entry("Height (cm)", extractField(aamvaResult.getHeightCm())));
        result.add(new CaptureResult.Entry("Weight (lbs)", extractField(aamvaResult.getWeightLbs())));
        result.add(new CaptureResult.Entry("Weight (kg)", extractField(aamvaResult.getWeightKg())));
        result.add(new CaptureResult.Entry("Place Of Birth", extractField(aamvaResult.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(aamvaResult.getRace())));
        result.add(new CaptureResult.Entry("Document Discriminator Number", extractField(aamvaResult.getDocumentDiscriminatorNumber())));
        result.add(new CaptureResult.Entry("Vehicle Class", extractField(aamvaResult.getVehicleClass())));
        result.add(new CaptureResult.Entry("Restrictions Code", extractField(aamvaResult.getRestrictionsCode())));
        result.add(new CaptureResult.Entry("Endorsements Code", extractField(aamvaResult.getEndorsementsCode())));
        result.add(new CaptureResult.Entry("Card Revision Date", extractField(aamvaResult.getCardRevisionDate())));
        result.add(new CaptureResult.Entry("First Name Without Middle Name", extractField(aamvaResult.getFirstNameWithoutMiddleName())));
        result.add(new CaptureResult.Entry("Middle Name", extractField(aamvaResult.getMiddleName())));
        result.add(new CaptureResult.Entry("Driver Name Suffix", extractField(aamvaResult.getDriverNameSuffix())));
        result.add(new CaptureResult.Entry("Driver Name Prefix", extractField(aamvaResult.getDriverNamePrefix())));
        result.add(new CaptureResult.Entry("Last Name Truncation", extractField(aamvaResult.getLastNameTruncation())));
        result.add(new CaptureResult.Entry("First Name Truncation", extractField(aamvaResult.getFirstNameTruncation())));
        result.add(new CaptureResult.Entry("Middle Name Truncation", extractField(aamvaResult.getMiddleNameTruncation())));
        result.add(new CaptureResult.Entry("Alias Family Name", extractField(aamvaResult.getAliasFamilyName())));
        result.add(new CaptureResult.Entry("Alias Given Name", extractField(aamvaResult.getAliasGivenName())));
        result.add(new CaptureResult.Entry("Alias Suffix Name", extractField(aamvaResult.getAliasSuffixName())));
        return result;
    }
}
