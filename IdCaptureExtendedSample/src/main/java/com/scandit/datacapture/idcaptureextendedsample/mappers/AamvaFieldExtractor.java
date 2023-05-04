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

import com.scandit.datacapture.id.data.AamvaBarcodeResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;

public final class AamvaFieldExtractor extends FieldExtractor {

    private final AamvaBarcodeResult result;

    public AamvaFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getAamvaBarcode();
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extract() {
        ArrayList<CaptureResult.Entry> result = new ArrayList<>();
        result.add(new CaptureResult.Entry("AAMVA Version", extractField(this.result.getAamvaVersion())));
        result.add(new CaptureResult.Entry("Jurisdiction Version", extractField(this.result.getJurisdictionVersion())));
        result.add(new CaptureResult.Entry("IIN", extractField(this.result.getIin())));
        result.add(new CaptureResult.Entry("Is Real ID", extractField(this.result.isRealId())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction", extractField(this.result.getIssuingJurisdiction())));
        result.add(new CaptureResult.Entry("Issuing Jurisdiction ISO", extractField(this.result.getIssuingJurisdictionIso())));
        result.add(new CaptureResult.Entry("Eye Color", extractField(this.result.getEyeColor())));
        result.add(new CaptureResult.Entry("Hair Color", extractField(this.result.getHairColor())));
        result.add(new CaptureResult.Entry("Height (inch)", extractField(this.result.getHeightInch())));
        result.add(new CaptureResult.Entry("Height (cm)", extractField(this.result.getHeightCm())));
        result.add(new CaptureResult.Entry("Weight (lbs)", extractField(this.result.getWeightLbs())));
        result.add(new CaptureResult.Entry("Weight (kg)", extractField(this.result.getWeightKg())));
        result.add(new CaptureResult.Entry("Place Of Birth", extractField(this.result.getPlaceOfBirth())));
        result.add(new CaptureResult.Entry("Race", extractField(this.result.getRace())));
        result.add(new CaptureResult.Entry("Document Discriminator Number", extractField(this.result.getDocumentDiscriminatorNumber())));
        result.add(new CaptureResult.Entry("Vehicle Class", extractField(this.result.getVehicleClass())));
        result.add(new CaptureResult.Entry("Restrictions Code", extractField(this.result.getRestrictionsCode())));
        result.add(new CaptureResult.Entry("Endorsements Code", extractField(this.result.getEndorsementsCode())));
        result.add(new CaptureResult.Entry("Card Revision Date", extractField(this.result.getCardRevisionDate())));
        result.add(new CaptureResult.Entry("Middle Name", extractField(this.result.getMiddleName())));
        result.add(new CaptureResult.Entry("Driver Name Suffix", extractField(this.result.getDriverNameSuffix())));
        result.add(new CaptureResult.Entry("Driver Name Prefix", extractField(this.result.getDriverNamePrefix())));
        result.add(new CaptureResult.Entry("Last Name Truncation", extractField(this.result.getLastNameTruncation())));
        result.add(new CaptureResult.Entry("First Name Truncation", extractField(this.result.getFirstNameTruncation())));
        result.add(new CaptureResult.Entry("Middle Name Truncation", extractField(this.result.getMiddleNameTruncation())));
        result.add(new CaptureResult.Entry("Alias Family Name", extractField(this.result.getAliasFamilyName())));
        result.add(new CaptureResult.Entry("Alias Given Name", extractField(this.result.getAliasGivenName())));
        result.add(new CaptureResult.Entry("Alias Suffix Name", extractField(this.result.getAliasSuffixName())));
        return result;
    }
}
