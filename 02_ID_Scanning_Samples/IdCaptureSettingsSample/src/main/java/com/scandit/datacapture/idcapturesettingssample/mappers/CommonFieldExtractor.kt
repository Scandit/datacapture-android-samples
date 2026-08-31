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

package com.scandit.datacapture.idcapturesettingssample.mappers

import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.capture.RegionSpecific
import com.scandit.datacapture.id.capture.description
import com.scandit.datacapture.id.data.CapturedId
import com.scandit.datacapture.id.data.IdFieldType
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

class CommonFieldExtractor(capturedId: CapturedId) : FieldExtractor(capturedId) {

    /**
     * Extract all the common CapturedId fields.
     */
    override fun extract(): Map<String, String> = mapOf(
        "Document Type" to capturedId.document.toDisplayValue(),
        "Subtype" to capturedId.document.subtypeDisplayValue(),
        "First Name" to capturedId.firstName.toDisplayValue(),
        "Last Name" to capturedId.lastName.toDisplayValue(),
        "Full Name" to capturedId.fullName.toDisplayValue(),
        "Sex" to capturedId.sexType.toString().toDisplayValue(),
        "Date of Birth" to capturedId.dateOfBirth.toDisplayValue(),
        "Age" to capturedId.age.toDisplayValue(),
        "Nationality" to capturedId.nationality.toDisplayValue(),
        "Nationality ISO" to capturedId.nationalityISO.toDisplayValue(),
        "Address" to capturedId.address.toDisplayValue(),
        "Issuing Country ISO" to capturedId.issuingCountryIso.toDisplayValue(),
        "Issuing Country" to capturedId.issuingCountry.description.toDisplayValue(),
        "Document Number" to capturedId.documentNumber.toDisplayValue(),
        "Visa Number" to capturedId.visaNumber.toDisplayValue(),
        "Passport Number" to capturedId.passportNumber.toDisplayValue(),
        "Document Additional Number" to capturedId.documentAdditionalNumber.toDisplayValue(),
        "Date of Expiry" to capturedId.dateOfExpiry.toDisplayValue(),
        "Is Expired" to capturedId.isExpired.toDisplayValue(),
        "Date of Issue" to capturedId.dateOfIssue.toDisplayValue(),
        "Is Citizen Passport" to capturedId.isCitizenPassport.toDisplayValue(),
        "Is Capturing Complete" to capturedId.isCapturingComplete.toDisplayValue(),
        "US REAL ID Status" to capturedId.usRealIdStatus.toString().toDisplayValue(),
        "Anonymized Fields" to capturedId.anonymizedFields.toDisplayValue(),
    )

    private fun IdCaptureDocument?.toDisplayValue(): String =
        this?.documentType?.toString()?.toNameCase().orEmptyText()

    private fun IdCaptureDocument?.subtypeDisplayValue(): String = if (this is RegionSpecific) {
        subtype.toString().toNameCase()
    } else {
        EMPTY_TEXT_VALUE
    }

    private fun Set<IdFieldType>.toDisplayValue(): String = joinToString().orEmptyText()
}
