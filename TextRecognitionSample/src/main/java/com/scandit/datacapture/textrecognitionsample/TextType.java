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

/**
 * Various types of text we recognize in this sample along with regexes that allow TextCapture
 * to filter them.
 */
public enum TextType {
    IBAN("([A-Z]{2}[A-Z0-9]{2}\\\\s*([A-Z0-9]{4}\\\\s*){4,8}([A-Z0-9]{1,4}))"),
    /*
     * In fact GS1 AI comes in such variety that makes it impossible to cover all the cases with
     * reasonable regex. Therefore we recognize only a very specific subset. Similarly, if
     * recognizing such codes is your use-case, then you probably don't need to recognize just any
     * GS1 AI, but a specific kind.
     */
    GS1_AI(
            "(" +
                "(\\\\(01\\\\)[0-9]{13,14})" + /* GTIN-14 */
                "(\\\\s*" + /* and at least one of */
                    "(\\\\(10\\\\)[0-9a-zA-Z]{1,20})|" + /* Batch Number */
                    "(\\\\(11\\\\)[0-9]{6})|" + /* Production Date */
                    "(\\\\(17\\\\)[0-9]{6})|" + /* Expiration Date */
                    "(\\\\(21\\\\)[0-9a-zA-Z]{1,20})" + /* Serial Number */
                ")+" +
            ")"
    ),
    PRICE("((^|\\\\s+)[0-9]{1,}\\\\.[0-9]{1,}(\\\\s+|$))");

    private String regex;

    TextType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}