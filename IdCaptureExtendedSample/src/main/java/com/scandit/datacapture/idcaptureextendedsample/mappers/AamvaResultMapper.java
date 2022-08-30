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
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.util.ArrayList;

public final class AamvaResultMapper extends ResultMapper {

    private final AamvaBarcodeResult result;

    public AamvaResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getAamvaBarcode();
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();
        result.add(new ResultEntry("AAMVA Version", extractField(this.result.getAamvaVersion())));
        result.add(new ResultEntry("Jurisdiction Version", extractField(this.result.getJurisdictionVersion())));
        result.add(new ResultEntry("IIN", extractField(this.result.getIin())));
        result.add(new ResultEntry("Issuing Jurisdiction", extractField(this.result.getIssuingJurisdiction())));
        result.add(new ResultEntry("Issuing Jurisdiction ISO", extractField(this.result.getIssuingJurisdictionIso())));
        result.add(new ResultEntry("Eye Color", extractField(this.result.getEyeColor())));
        result.add(new ResultEntry("Hair Color", extractField(this.result.getHairColor())));
        result.add(new ResultEntry("Height (inch)", extractField(this.result.getHeightInch())));
        result.add(new ResultEntry("Height (cm)", extractField(this.result.getHeightCm())));
        result.add(new ResultEntry("Weight (lbs)", extractField(this.result.getWeightLbs())));
        result.add(new ResultEntry("Weight (kg)", extractField(this.result.getWeightKg())));
        result.add(new ResultEntry("Place Of Birth", extractField(this.result.getPlaceOfBirth())));
        result.add(new ResultEntry("Race", extractField(this.result.getRace())));
        result.add(new ResultEntry("Document Discriminator Number", extractField(this.result.getDocumentDiscriminatorNumber())));
        result.add(new ResultEntry("Vehicle Class", extractField(this.result.getVehicleClass())));
        result.add(new ResultEntry("Restrictions Code", extractField(this.result.getRestrictionsCode())));
        result.add(new ResultEntry("Endorsements Code", extractField(this.result.getEndorsementsCode())));
        result.add(new ResultEntry("Card Revision Date", extractField(this.result.getCardRevisionDate())));
        result.add(new ResultEntry("Middle Name", extractField(this.result.getMiddleName())));
        result.add(new ResultEntry("Driver Name Suffix", extractField(this.result.getDriverNameSuffix())));
        result.add(new ResultEntry("Driver Name Prefix", extractField(this.result.getDriverNamePrefix())));
        result.add(new ResultEntry("Last Name Truncation", extractField(this.result.getLastNameTruncation())));
        result.add(new ResultEntry("First Name Truncation", extractField(this.result.getFirstNameTruncation())));
        result.add(new ResultEntry("Middle Name Truncation", extractField(this.result.getMiddleNameTruncation())));
        result.add(new ResultEntry("Alias Family Name", extractField(this.result.getAliasFamilyName())));
        result.add(new ResultEntry("Alias Given Name", extractField(this.result.getAliasGivenName())));
        result.add(new ResultEntry("Alias Suffix Name", extractField(this.result.getAliasSuffixName())));
        return result;
    }
}
