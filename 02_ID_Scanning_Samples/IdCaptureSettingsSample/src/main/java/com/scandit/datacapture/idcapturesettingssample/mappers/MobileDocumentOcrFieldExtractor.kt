/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2025- Scandit AG. All rights reserved.
 */
package com.scandit.datacapture.idcapturesettingssample.mappers

import com.scandit.datacapture.id.data.CapturedId

class MobileDocumentOcrFieldExtractor(capturedId: CapturedId) : FieldExtractor(capturedId) {

    /**
     * Extract all the zone's specific fields and add them to the ones from CapturedId.
     */
    override fun extract(): Map<String, String> {
        val zone = capturedId.mobileDocumentOcr ?: return emptyMap()
        return mapOf(
            "Mobile Document OCR First Name" to zone.firstName.toDisplayValue(),
            "Mobile Document OCR Last Name" to zone.lastName.toDisplayValue(),
            "Mobile Document OCR Full Name" to zone.fullName.toDisplayValue(),
            "Mobile Document OCR Date of Birth" to zone.dateOfBirth.toDisplayValue(),
            "Mobile Document OCR Sex" to zone.sex.toDisplayValue(),
            "Mobile Document OCR Nationality" to zone.nationality.toDisplayValue(),
            "Mobile Document OCR Address" to zone.address.toDisplayValue(),
            "Mobile Document OCR Document Number" to zone.documentNumber.toDisplayValue(),
            "Mobile Document OCR Document Additional Number" to zone.documentAdditionalNumber.toDisplayValue(),
            "Mobile Document OCR Date of Expiry" to zone.dateOfExpiry.toDisplayValue(),
            "Mobile Document OCR Date of Issue" to zone.dateOfIssue.toDisplayValue(),
            "Mobile Document OCR Issuing Jurisdiction ISO" to zone.issuingJurisdictionIso.toDisplayValue(),
            "Mobile Document OCR Issuing Jurisdiction" to zone.issuingJurisdiction.toDisplayValue()
        )
    }
}
