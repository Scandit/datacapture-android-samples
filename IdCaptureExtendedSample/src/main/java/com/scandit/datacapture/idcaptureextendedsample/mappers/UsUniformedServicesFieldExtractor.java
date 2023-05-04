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
import com.scandit.datacapture.idcaptureextendedsample.ui.result.CaptureResult;

import java.util.ArrayList;

public final class UsUniformedServicesFieldExtractor extends FieldExtractor {

    private final UsUniformedServicesBarcodeResult result;

    public UsUniformedServicesFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        result = capturedId.getUsUniformedServicesBarcode();
    }

    /*
     * We extract all the UsUniformedServicesBarcodeResult's specific fields and add them to the
     * ones from CapturedId.
     */
    @Override
    public ArrayList<CaptureResult.Entry> extract() {
        ArrayList<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Height", extractField(this.result.getHeight())));
        result.add(new CaptureResult.Entry("Weight", extractField(this.result.getWeight())));
        result.add(new CaptureResult.Entry("Blood Type", extractField(this.result.getBloodType())));
        result.add(new CaptureResult.Entry("Eye Color", extractField(this.result.getEyeColor())));
        result.add(new CaptureResult.Entry("Hair Color", extractField(this.result.getHairColor())));
        result.add(new CaptureResult.Entry("Relationship Code", extractField(this.result.getRelationshipCode())));
        result.add(new CaptureResult.Entry("Relationship Description", extractField(this.result.getRelationshipDescription())));
        result.add(new CaptureResult.Entry("Branch of Service", extractField(this.result.getBranchOfService())));
        result.add(new CaptureResult.Entry("Champus Effective Date", extractField(this.result.getChampusEffectiveDate())));
        result.add(new CaptureResult.Entry("Champus Expiry Date", extractField(this.result.getChampusExpiryDate())));
        result.add(new CaptureResult.Entry("Civilian Healthcare Flag Code", extractField(this.result.getCivilianHealthCareFlagCode())));
        result.add(new CaptureResult.Entry("Civilian Healthcare Flag Description", extractField(this.result.getCivilianHealthCareFlagDescription())));
        result.add(new CaptureResult.Entry("Deers Dependent Suffix Code", extractField(this.result.getDeersDependentSuffixCode())));
        result.add(new CaptureResult.Entry("Deers Dependent Suffix Description", extractField(this.result.getDeersDependentSuffixDescription())));
        result.add(new CaptureResult.Entry("Direct Care Flag Code", extractField(this.result.getDirectCareFlagCode())));
        result.add(new CaptureResult.Entry("Direct Care Flag Description", extractField(this.result.getDirectCareFlagDescription())));
        result.add(new CaptureResult.Entry("Family Sequence Number", extractField(this.result.getFamilySequenceNumber())));
        result.add(new CaptureResult.Entry("Geneva Convention Category", extractField(this.result.getGenevaConventionCategory())));
        result.add(new CaptureResult.Entry("Mwr Flag Code", extractField(this.result.getMwrFlagCode())));
        result.add(new CaptureResult.Entry("Mwr Flag Description", extractField(this.result.getMwrFlagDescription())));
        result.add(new CaptureResult.Entry("Form Number", extractField(this.result.getFormNumber())));
        result.add(new CaptureResult.Entry("Status Code", extractField(this.result.getStatusCode())));
        result.add(new CaptureResult.Entry("Status Code Description", extractField(this.result.getStatusCodeDescription())));
        result.add(new CaptureResult.Entry("Rank", extractField(this.result.getRank())));
        result.add(new CaptureResult.Entry("Pay Grade", extractField(this.result.getPayGrade())));
        result.add(new CaptureResult.Entry("Person Designator Document", extractField(this.result.getPersonDesignatorDocument())));
        result.add(new CaptureResult.Entry("Sponsor Name", extractField(this.result.getSponsorName())));
        result.add(new CaptureResult.Entry("Sponsor Flag", extractField(this.result.getSponsorFlag())));
        result.add(new CaptureResult.Entry("Sponsor Person Designator Identifier", extractField(this.result.getSponsorPersonDesignatorIdentifier())));
        result.add(new CaptureResult.Entry("Security Code", extractField(this.result.getSecurityCode())));
        result.add(new CaptureResult.Entry("Service Code", extractField(this.result.getServiceCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Code", extractField(this.result.getExchangeFlagCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Description", extractField(this.result.getExchangeFlagDescription())));
        result.add(new CaptureResult.Entry("Commissary Flag Code", extractField(this.result.getCommissaryFlagCode())));
        result.add(new CaptureResult.Entry("Commissary Flag Description", extractField(this.result.getCommissaryFlagDescription())));
        result.add(new CaptureResult.Entry("Version", extractField(this.result.getVersion())));

        return result;
    }
}
