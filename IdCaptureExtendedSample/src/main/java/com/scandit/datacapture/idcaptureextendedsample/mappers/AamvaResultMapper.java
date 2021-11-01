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

    private final AamvaBarcodeResult aamvaResult;

    public AamvaResultMapper(CapturedId capturedId) {
        super(capturedId);
        aamvaResult = capturedId.getAamvaBarcode();
    }

    /*
     * We extract all the AamvaBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();
        result.add(new ResultEntry("AAMVA Version", extractField(aamvaResult.getAamvaVersion())));
        result.add(new ResultEntry("Jurisdiction Version", extractField(aamvaResult.getJurisdictionVersion())));
        result.add(new ResultEntry("IIN", extractField(aamvaResult.getIin())));
        result.add(new ResultEntry("Issuing Jurisdiction", extractField(aamvaResult.getIssuingJurisdiction())));
        result.add(new ResultEntry("Issuing Jurisdiction ISO", extractField(aamvaResult.getIssuingJurisdictionIso())));
        result.add(new ResultEntry("Eye Color", extractField(aamvaResult.getEyeColor())));
        result.add(new ResultEntry("Hair Color", extractField(aamvaResult.getHairColor())));
        result.add(new ResultEntry("Height (inch)", extractField(aamvaResult.getHeightInch())));
        result.add(new ResultEntry("Height (cm)", extractField(aamvaResult.getHeightCm())));
        result.add(new ResultEntry("Weight (lbs)", extractField(aamvaResult.getWeightLbs())));
        result.add(new ResultEntry("Weight (kg)", extractField(aamvaResult.getWeightKg())));
        result.add(new ResultEntry("Place Of Birth", extractField(aamvaResult.getPlaceOfBirth())));
        result.add(new ResultEntry("Race", extractField(aamvaResult.getRace())));
        result.add(new ResultEntry("Document Discriminator Number", extractField(aamvaResult.getDocumentDiscriminatorNumber())));
        result.add(new ResultEntry("Vehicle Class", extractField(aamvaResult.getVehicleClass())));
        result.add(new ResultEntry("Restrictions Code", extractField(aamvaResult.getRestrictionsCode())));
        result.add(new ResultEntry("Endorsements Code", extractField(aamvaResult.getEndorsementsCode())));
        result.add(new ResultEntry("Card Revision Date", extractField(aamvaResult.getCardRevisionDate())));
        result.add(new ResultEntry("Middle Name", extractField(aamvaResult.getMiddleName())));
        result.add(new ResultEntry("Driver Name Suffix", extractField(aamvaResult.getDriverNameSuffix())));
        result.add(new ResultEntry("Driver Name Prefix", extractField(aamvaResult.getDriverNamePrefix())));
        result.add(new ResultEntry("Last Name Truncation", extractField(aamvaResult.getLastNameTruncation())));
        result.add(new ResultEntry("First Name Truncation", extractField(aamvaResult.getFirstNameTruncation())));
        result.add(new ResultEntry("Middle Name Truncation", extractField(aamvaResult.getMiddleNameTruncation())));
        result.add(new ResultEntry("Alias Family Name", extractField(aamvaResult.getAliasFamilyName())));
        result.add(new ResultEntry("Alias Given Name", extractField(aamvaResult.getAliasGivenName())));
        result.add(new ResultEntry("Alias Suffix Name", extractField(aamvaResult.getAliasSuffixName())));
        return result;
    }
}
