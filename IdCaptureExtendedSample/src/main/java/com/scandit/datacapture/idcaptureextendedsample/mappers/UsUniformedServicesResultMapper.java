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
import com.scandit.datacapture.id.data.UsUniformedServicesBarcodeResult;
import com.scandit.datacapture.idcaptureextendedsample.ui.result.ResultEntry;

import java.util.ArrayList;

public final class UsUniformedServicesResultMapper extends ResultMapper {

    private final UsUniformedServicesBarcodeResult usServiceResult;

    public UsUniformedServicesResultMapper(CapturedId capturedId) {
        super(capturedId);
        usServiceResult = capturedId.getUsUniformedServicesBarcode();
    }

    /*
     * We extract all the UsUniformedServicesBarcodeResult's specific fields and add them to the
     * ones from CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();

        result.add(new ResultEntry("Height", extractField(usServiceResult.getHeight())));
        result.add(new ResultEntry("Weight", extractField(usServiceResult.getWeight())));
        result.add(new ResultEntry("Blood Type", extractField(usServiceResult.getBloodType())));
        result.add(new ResultEntry("Eye Color", extractField(usServiceResult.getEyeColor())));
        result.add(new ResultEntry("Hair Color", extractField(usServiceResult.getHairColor())));
        result.add(new ResultEntry("Relationship Code", extractField(usServiceResult.getRelationshipCode())));
        result.add(new ResultEntry("Relationship Description", extractField(usServiceResult.getRelationshipDescription())));
        result.add(new ResultEntry("Branch of Service", extractField(usServiceResult.getBranchOfService())));
        result.add(new ResultEntry("Champus Effective Date", extractField(usServiceResult.getChampusEffectiveDate())));
        result.add(new ResultEntry("Champus Expiry Date", extractField(usServiceResult.getChampusExpiryDate())));
        result.add(new ResultEntry("Civilian Healthcare Flag Code", extractField(usServiceResult.getCivilianHealthCareFlagCode())));
        result.add(new ResultEntry("Civilian Healthcare Flag Description", extractField(usServiceResult.getCivilianHealthCareFlagDescription())));
        result.add(new ResultEntry("Deers Dependent Suffix Code", extractField(usServiceResult.getDeersDependentSuffixCode())));
        result.add(new ResultEntry("Deers Dependent Suffix Description", extractField(usServiceResult.getDeersDependentSuffixDescription())));
        result.add(new ResultEntry("Direct Care Flag Code", extractField(usServiceResult.getDirectCareFlagCode())));
        result.add(new ResultEntry("Direct Care Flag Description", extractField(usServiceResult.getDirectCareFlagDescription())));
        result.add(new ResultEntry("Family Sequence Number", extractField(usServiceResult.getFamilySequenceNumber())));
        result.add(new ResultEntry("Geneva Convention Category", extractField(usServiceResult.getGenevaConventionCategory())));
        result.add(new ResultEntry("Mwr Flag Code", extractField(usServiceResult.getMwrFlagCode())));
        result.add(new ResultEntry("Mwr Flag Description", extractField(usServiceResult.getMwrFlagDescription())));
        result.add(new ResultEntry("Form Number", extractField(usServiceResult.getFormNumber())));
        result.add(new ResultEntry("Status Code", extractField(usServiceResult.getStatusCode())));
        result.add(new ResultEntry("Status Code Description", extractField(usServiceResult.getStatusCodeDescription())));
        result.add(new ResultEntry("Rank", extractField(usServiceResult.getRank())));
        result.add(new ResultEntry("Pay Grade", extractField(usServiceResult.getPayGrade())));
        result.add(new ResultEntry("Person Designator Document", extractField(usServiceResult.getPersonDesignatorDocument())));
        result.add(new ResultEntry("Sponsor Name", extractField(usServiceResult.getSponsorName())));
        result.add(new ResultEntry("Sponsor Flag", extractField(usServiceResult.getSponsorFlag())));
        result.add(new ResultEntry("Sponsor Person Designator Identifier", extractField(usServiceResult.getSponsorPersonDesignatorIdentifier())));
        result.add(new ResultEntry("Security Code", extractField(usServiceResult.getSecurityCode())));
        result.add(new ResultEntry("Service Code", extractField(usServiceResult.getServiceCode())));
        result.add(new ResultEntry("Exchange Flag Code", extractField(usServiceResult.getExchangeFlagCode())));
        result.add(new ResultEntry("Exchange Flag Description", extractField(usServiceResult.getExchangeFlagDescription())));
        result.add(new ResultEntry("Commissary Flag Code", extractField(usServiceResult.getCommissaryFlagCode())));
        result.add(new ResultEntry("Commissary Flag Description", extractField(usServiceResult.getCommissaryFlagDescription())));
        result.add(new ResultEntry("Version", extractField(usServiceResult.getVersion())));

        return result;
    }
}
