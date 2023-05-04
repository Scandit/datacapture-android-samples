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
import com.scandit.datacapture.id.data.UsUniformedServicesBarcodeResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class UsUniformedServiceBarcodeFieldExtractor extends FieldExtractor {

    private final UsUniformedServicesBarcodeResult usServiceResult;

    public UsUniformedServiceBarcodeFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        usServiceResult = capturedId.getUsUniformedServicesBarcode();
    }

    /*
     * We extract all the UsUniformedServicesBarcodeResult's specific fields and add them to the
     * ones from CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Height", extractField(usServiceResult.getHeight())));
        result.add(new CaptureResult.Entry("Weight", extractField(usServiceResult.getWeight())));
        result.add(new CaptureResult.Entry("Blood Type", extractField(usServiceResult.getBloodType())));
        result.add(new CaptureResult.Entry("Eye Color", extractField(usServiceResult.getEyeColor())));
        result.add(new CaptureResult.Entry("Hair Color", extractField(usServiceResult.getHairColor())));
        result.add(new CaptureResult.Entry("Relationship Code", extractField(usServiceResult.getRelationshipCode())));
        result.add(new CaptureResult.Entry("Relationship Description", extractField(usServiceResult.getRelationshipDescription())));
        result.add(new CaptureResult.Entry("Branch of Service", extractField(usServiceResult.getBranchOfService())));
        result.add(new CaptureResult.Entry("CHAMPUS Effective Date", extractField(usServiceResult.getChampusEffectiveDate())));
        result.add(new CaptureResult.Entry("CHAMPUS Expiry Date", extractField(usServiceResult.getChampusExpiryDate())));
        result.add(new CaptureResult.Entry("Civilian Healthcare Flag Code", extractField(usServiceResult.getCivilianHealthCareFlagCode())));
        result.add(new CaptureResult.Entry("Civilian Healthcare Flag Description", extractField(usServiceResult.getCivilianHealthCareFlagDescription())));
        result.add(new CaptureResult.Entry("DEERS Dependent Suffix Code", extractField(usServiceResult.getDeersDependentSuffixCode())));
        result.add(new CaptureResult.Entry("DEERS Dependent Suffix Description", extractField(usServiceResult.getDeersDependentSuffixDescription())));
        result.add(new CaptureResult.Entry("Direct Care Flag Code", extractField(usServiceResult.getDirectCareFlagCode())));
        result.add(new CaptureResult.Entry("Direct Care Flag Description", extractField(usServiceResult.getDirectCareFlagDescription())));
        result.add(new CaptureResult.Entry("Family Sequence Number", extractField(usServiceResult.getFamilySequenceNumber())));
        result.add(new CaptureResult.Entry("Geneva Convention Category", extractField(usServiceResult.getGenevaConventionCategory())));
        result.add(new CaptureResult.Entry("MWR Flag Code", extractField(usServiceResult.getMwrFlagCode())));
        result.add(new CaptureResult.Entry("MWR Flag Description", extractField(usServiceResult.getMwrFlagDescription())));
        result.add(new CaptureResult.Entry("Form Number", extractField(usServiceResult.getFormNumber())));
        result.add(new CaptureResult.Entry("Status Code", extractField(usServiceResult.getStatusCode())));
        result.add(new CaptureResult.Entry("Status Code Description", extractField(usServiceResult.getStatusCodeDescription())));
        result.add(new CaptureResult.Entry("Rank", extractField(usServiceResult.getRank())));
        result.add(new CaptureResult.Entry("Pay Grade", extractField(usServiceResult.getPayGrade())));
        result.add(new CaptureResult.Entry("Person Designator Document", extractField(usServiceResult.getPersonDesignatorDocument())));
        result.add(new CaptureResult.Entry("Sponsor Name", extractField(usServiceResult.getSponsorName())));
        result.add(new CaptureResult.Entry("Sponsor Flag", extractField(usServiceResult.getSponsorFlag())));
        result.add(new CaptureResult.Entry("Sponsor Person Designator Identifier", extractField(usServiceResult.getSponsorPersonDesignatorIdentifier())));
        result.add(new CaptureResult.Entry("Security Code", extractField(usServiceResult.getSecurityCode())));
        result.add(new CaptureResult.Entry("Service Code", extractField(usServiceResult.getServiceCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Code", extractField(usServiceResult.getExchangeFlagCode())));
        result.add(new CaptureResult.Entry("Exchange Flag Description", extractField(usServiceResult.getExchangeFlagDescription())));
        result.add(new CaptureResult.Entry("Commissary Flag Code", extractField(usServiceResult.getCommissaryFlagCode())));
        result.add(new CaptureResult.Entry("Commissary Flag Description", extractField(usServiceResult.getCommissaryFlagDescription())));
        result.add(new CaptureResult.Entry("Version", extractField(usServiceResult.getVersion())));

        return result;
    }
}
