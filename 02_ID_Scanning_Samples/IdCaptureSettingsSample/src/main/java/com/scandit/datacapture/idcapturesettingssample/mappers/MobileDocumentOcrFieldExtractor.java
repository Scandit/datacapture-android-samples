/*
 * This file is part of the Scandit Data Capture SDK
 *
 * Copyright (C) 2025- Scandit AG. All rights reserved.
 */
package com.scandit.datacapture.idcapturesettingssample.mappers;

import com.scandit.datacapture.id.capture.IdCaptureRegions;
import com.scandit.datacapture.id.data.CapturedId;
import com.scandit.datacapture.id.data.MobileDocumentOcrResult;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

import java.util.ArrayList;
import java.util.List;

public final class MobileDocumentOcrFieldExtractor extends FieldExtractor {

    private final MobileDocumentOcrResult mobileDocumentOcrResult;

    public MobileDocumentOcrFieldExtractor(CapturedId capturedId) {
        super(capturedId);
        mobileDocumentOcrResult = capturedId.getMobileDocumentOcr();
    }

    @Override
    protected List<CaptureResult.Entry> extract() {
        List<CaptureResult.Entry> result = new ArrayList<>();

        result.add(new CaptureResult.Entry(
            "Mobile Document OCR First Name",
            extractField(mobileDocumentOcrResult.getFirstName())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Last Name",
            extractField(mobileDocumentOcrResult.getLastName())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Full Name",
            extractField(mobileDocumentOcrResult.getFullName())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Date of Birth",
            extractField(mobileDocumentOcrResult.getDateOfBirth())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Sex",
            extractField(mobileDocumentOcrResult.getSex())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Nationality",
            extractField(mobileDocumentOcrResult.getNationality())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Address",
            extractField(mobileDocumentOcrResult.getAddress())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Document Number",
            extractField(mobileDocumentOcrResult.getDocumentNumber())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Document Additional Number",
            extractField(mobileDocumentOcrResult.getDocumentAdditionalNumber())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Date of Expiry",
            extractField(mobileDocumentOcrResult.getDateOfExpiry())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Date of Issue",
            extractField(mobileDocumentOcrResult.getDateOfIssue())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Issuing Jurisdiction ISO",
            extractField(mobileDocumentOcrResult.getIssuingJurisdictionIso())
        ));
        result.add(new CaptureResult.Entry(
            "Mobile Document OCR Issuing Jurisdiction",
            extractField(mobileDocumentOcrResult.getIssuingJurisdiction())
        ));

        return result;
    }
}
