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

package com.scandit.datacapture.ageverifieddeliverysample.ui.manualentry;

import com.scandit.datacapture.ageverifieddeliverysample.ui.Event;

import java.time.LocalDate;

/**
 * An event for the fragment to display a picker for the document holder's date of birth.
 */
class GoToDateOfBirthPicker extends Event<LocalDate> {
    public GoToDateOfBirthPicker(LocalDate selectedDate) {
        super(selectedDate);
    }
}
