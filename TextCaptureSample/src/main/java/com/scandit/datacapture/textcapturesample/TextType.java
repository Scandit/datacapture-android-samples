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

package com.scandit.datacapture.textcapturesample;

/**
 * Various types of text we recognize in this sample along with regexes that allow TextCapture
 * to filter them.
 */
public enum TextType {
    /*
     * In fact GS1 AI comes in such variety that makes it impossible to cover all the cases with
     * reasonable regex. If recognizing such codes is your use-case, then you probably don't need
     * to recognize just any GS1 AI, but a specific kind.
     */
    GS1_AI("((?:\\\\([0-9]+\\\\)[A-Za-z0-9]+)+)"),
    LOT("([A-Z0-9]{6,8})");

    private String regex;

    TextType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}