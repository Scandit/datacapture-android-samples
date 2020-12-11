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

package com.scandit.datacapture.textrecognitionsample;

import java.math.BigInteger;

/**
 * Validator for IBAN numbers.
 */
public class IbanValidator {

    private final String ibanInput;

    public IbanValidator(String ibanInput) {
        this.ibanInput = ibanInput;
    }

    public boolean isValid() {
        String iban = ibanInput.replace(" ", "");
        StringBuilder builder = new StringBuilder();
        appendIntegers(builder, iban.substring(4));
        appendIntegers(builder, iban.substring(0, 4));
        try {
            BigInteger moduloCandidate = new BigInteger(builder.toString());
            return moduloCandidate.mod(new BigInteger("97")).equals(new BigInteger("1"));
        } catch (NumberFormatException e) {
            // Reject the IBAN if the conversion to integers failed. This generally means that the
            // original character set was larger than just 0-9A-Z.
            return false;
        }
    }

    private void appendIntegers(StringBuilder stringBuilder, String originalString) {
        for (char character : originalString.toCharArray()){
            int charAsInt = (int)character;
            int alphabetPosition = -1;
            if (charAsInt > 64 && charAsInt <= 64 + 26) {
                alphabetPosition = charAsInt - 64;
            }
            if (alphabetPosition <= 0) {
                stringBuilder.append(character);
            } else {
                stringBuilder.append(Integer.toString(alphabetPosition + 9));
            }
        }
    }
}
