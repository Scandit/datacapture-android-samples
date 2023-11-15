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

package com.scandit.datacapture.ageverifieddeliverysample.ui.id;

import androidx.annotation.Nullable;

import java.time.LocalDate;

/**
 * Represent the information extracted from the recipient's document for the verification. This
 * information may be obtained through the scanning process or by entering it manually.
 */
public class DocumentData {
    /**
     * The recipient's date of birth.
     */
    private final LocalDate dateOfBirth;

    /**
     * The recipient's full name.
     */
    private final String fullName;

    /**
     * The document's date of expiry or null if none.
     */
    @Nullable
    private final LocalDate dateOfExpiry;

    /**
     * Create a new instance of this class.
     */
    public DocumentData(LocalDate dateOfBirth, String fullName, LocalDate dateOfExpiry) {
        this.dateOfBirth = dateOfBirth;
        this.fullName = fullName;
        this.dateOfExpiry = dateOfExpiry;
    }

    /**
     * Get he recipient's date of birth.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Get the recipient's full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Get the document's date of expiry or null if none.
     */
    @Nullable
    public LocalDate getDateOfExpiry() {
        return dateOfExpiry;
    }
}
