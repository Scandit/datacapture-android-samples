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

    private final UsUniformedServicesBarcodeResult result;

    public UsUniformedServicesResultMapper(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getUsUniformedServicesBarcode();
    }

    /*
     * We extract all the UsUniformedServicesBarcodeResult's specific fields and add them to the
     * ones from CapturedId.
     */
    @Override
    public ArrayList<ResultEntry> mapResult() {
        ArrayList<ResultEntry> result = super.mapResult();

        result.add(new ResultEntry("Height", extractField(this.result.getHeight())));
        result.add(new ResultEntry("Weight", extractField(this.result.getWeight())));
        result.add(new ResultEntry("Blood Type", extractField(this.result.getBloodType())));
        result.add(new ResultEntry("Eye Color", extractField(this.result.getEyeColor())));
        result.add(new ResultEntry("Hair Color", extractField(this.result.getHairColor())));
        result.add(new ResultEntry("Relationship Code", extractField(this.result.getRelationshipCode())));
        result.add(new ResultEntry("Relationship Description", extractField(this.result.getRelationshipDescription())));
        result.add(new ResultEntry("Branch of Service", extractField(this.result.getBranchOfService())));
        result.add(new ResultEntry("Champus Effective Date", extractField(this.result.getChampusEffectiveDate())));
        result.add(new ResultEntry("Champus Expiry Date", extractField(this.result.getChampusExpiryDate())));
        result.add(new ResultEntry("Civilian Healthcare Flag Code", extractField(this.result.getCivilianHealthCareFlagCode())));
        result.add(new ResultEntry("Civilian Healthcare Flag Description", extractField(this.result.getCivilianHealthCareFlagDescription())));
        result.add(new ResultEntry("Deers Dependent Suffix Code", extractField(this.result.getDeersDependentSuffixCode())));
        result.add(new ResultEntry("Deers Dependent Suffix Description", extractField(this.result.getDeersDependentSuffixDescription())));
        result.add(new ResultEntry("Direct Care Flag Code", extractField(this.result.getDirectCareFlagCode())));
        result.add(new ResultEntry("Direct Care Flag Description", extractField(this.result.getDirectCareFlagDescription())));
        result.add(new ResultEntry("Family Sequence Number", extractField(this.result.getFamilySequenceNumber())));
        result.add(new ResultEntry("Geneva Convention Category", extractField(this.result.getGenevaConventionCategory())));
        result.add(new ResultEntry("Mwr Flag Code", extractField(this.result.getMwrFlagCode())));
        result.add(new ResultEntry("Mwr Flag Description", extractField(this.result.getMwrFlagDescription())));
        result.add(new ResultEntry("Form Number", extractField(this.result.getFormNumber())));
        result.add(new ResultEntry("Status Code", extractField(this.result.getStatusCode())));
        result.add(new ResultEntry("Status Code Description", extractField(this.result.getStatusCodeDescription())));
        result.add(new ResultEntry("Rank", extractField(this.result.getRank())));
        result.add(new ResultEntry("Pay Grade", extractField(this.result.getPayGrade())));
        result.add(new ResultEntry("Person Designator Document", extractField(this.result.getPersonDesignatorDocument())));
        result.add(new ResultEntry("Sponsor Name", extractField(this.result.getSponsorName())));
        result.add(new ResultEntry("Sponsor Flag", extractField(this.result.getSponsorFlag())));
        result.add(new ResultEntry("Sponsor Person Designator Identifier", extractField(this.result.getSponsorPersonDesignatorIdentifier())));
        result.add(new ResultEntry("Security Code", extractField(this.result.getSecurityCode())));
        result.add(new ResultEntry("Service Code", extractField(this.result.getServiceCode())));
        result.add(new ResultEntry("Exchange Flag Code", extractField(this.result.getExchangeFlagCode())));
        result.add(new ResultEntry("Exchange Flag Description", extractField(this.result.getExchangeFlagDescription())));
        result.add(new ResultEntry("Commissary Flag Code", extractField(this.result.getCommissaryFlagCode())));
        result.add(new ResultEntry("Commissary Flag Description", extractField(this.result.getCommissaryFlagDescription())));
        result.add(new ResultEntry("Version", extractField(this.result.getVersion())));

        return result;
    }
}
