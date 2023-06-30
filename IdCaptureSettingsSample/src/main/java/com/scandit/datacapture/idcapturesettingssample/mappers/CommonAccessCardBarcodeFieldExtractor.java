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

import com.scandit.datacapture.id.data.CommonAccessCardBarcodeResult;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class CommonAccessCardBarcodeFieldExtractor extends FieldExtractor {

    private final CommonAccessCardBarcodeResult commonAccessCardBarcodeResult;

    public CommonAccessCardBarcodeFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        commonAccessCardBarcodeResult = capturedId.getCommonAccessCardBarcode();
    }

    /*
     * We extract all the CommonAccessCardBarcodeResult's specific fields and add them to the ones from
     * CapturedId.
     */
    @Override
    public List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry("Version", extractField(
                   commonAccessCardBarcodeResult.getVersion())));
        result.add(new CaptureResult.Entry("Person Designator Identifier", extractField(
                commonAccessCardBarcodeResult.getPersonDesignatorDocument())));
        result.add(new CaptureResult.Entry("EDIPI", extractField(
                commonAccessCardBarcodeResult.getEdiPersonIdentifier())));
        result.add(new CaptureResult.Entry("Personnel Category Code", extractField(
                commonAccessCardBarcodeResult.getPersonnelCategoryCode())));
        result.add(new CaptureResult.Entry("Branch Code", extractField(
                commonAccessCardBarcodeResult.getBranchOfService())));
        result.add(new CaptureResult.Entry("Personnel Category Code Entitlement ConditionType", extractField(
                commonAccessCardBarcodeResult.getPersonnelEntitlementConditionType())));
        result.add(new CaptureResult.Entry("Rank", extractField(
                commonAccessCardBarcodeResult.getRank())));
        result.add(new CaptureResult.Entry("Play Plan Code", extractField(
                commonAccessCardBarcodeResult.getPayPlanCode())));
        result.add(new CaptureResult.Entry("Pay Plan Grade Code", extractField(
                commonAccessCardBarcodeResult.getPayPlanGradeCode())));
        result.add(new CaptureResult.Entry("Card Instance Identifier", extractField(
                commonAccessCardBarcodeResult.getCardInstanceIdentifier())));
        result.add(new CaptureResult.Entry("Person Middle Initial", extractField(
                commonAccessCardBarcodeResult.getPersonMiddleInitial())));

        return result;
    }
}
