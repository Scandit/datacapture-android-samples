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

package com.scandit.datacapture.vincodessample.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.parser.ParsedData;
import com.scandit.datacapture.parser.Parser;
import com.scandit.datacapture.parser.ParserDataFormat;
import com.scandit.datacapture.parser.ParserException;
import com.scandit.datacapture.text.data.CapturedText;

import java.util.HashMap;
import java.util.Map;

/*
 * A class that extracts needed fields from parsed result and returns scan result object.
 */
public class VinParserRepository {
    /**
     * The stream of parsed VIN codes.
     */
    private final MutableLiveData<VinResult> vins = new MutableLiveData<>();

    /**
     * The stream of errors encountered during parsing.
     */
    private final MutableLiveData<String> errorMessages = new MutableLiveData<>();

    /**
     * Parser handles the actual parsing of either the data decoded from a barcode or the captured
     * text.
     */
    private final Parser parser;

    public VinParserRepository(
            DataCaptureContext dataCaptureContext,
            BarcodeCaptureRepository barcodeCaptureRepository,
            TextCaptureRepository textCaptureRepository
    ) {
        barcodeCaptureRepository.capturedBarcodes().observeForever(this::onVinBarcodeCaptured);
        textCaptureRepository.capturedTexts().observeForever(this::onVinTextCaptured);

        // Create parser for capture context.
        parser = Parser.forFormat(dataCaptureContext, ParserDataFormat.VIN);

        /*
         * strictMode - Accept only VINs with valid checksums.
         * falsePositiveCompensation - Since VINs cannot contain 'Q', 'O', 'I', if they appear,
         * assume it's an OCR engine mistake and replace them with similar characters.
         */
        Map<String, Object> options = new HashMap<>();
        options.put("strictMode", true);
        options.put("falsePositiveCompensation", true);

        parser.setOptions(options);
    }

    /**
     * The stream of parsed VIN codes.
     */
    public LiveData<VinResult> getVins() {
        return vins;
    }

    /**
     * The stream of errors encountered during parsing.
     */
    public LiveData<String> getErrorMessages() {
        return errorMessages;
    }

    private void onVinBarcodeCaptured(Barcode barcode) {
        onVinCaptured(barcode.getData());
    }

    private void onVinTextCaptured(CapturedText text) {
        onVinCaptured(text.getValue());
    }

    private void onVinCaptured(String vinString) {
        /*
         * Try to parse the scanned data and pass all fields to the vin field view.
         */
        try {
            ParsedData parsedData = parser.parseString(vinString);

            /*
             * Get scan result object
             */
            VinResult vin = VinResult.from(parsedData);

            /*
             * Send the parsed VIN data to the UI layer.
             */
            vins.postValue(vin);
        } catch (ParserException e) {
            /*
             * Send the error to the UI layer.
             */
            errorMessages.postValue(e.getMessage());
        }
    }
}
