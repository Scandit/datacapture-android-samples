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

package com.scandit.datacapture.ageverifieddeliverysample.ui.scan;

/**
 * The side of the driver's license that the user may attempt to capture.
 */
enum DriverLicenseSide {
    /**
     * OCR the front of the document.
     */
    FRONT_VIZ,

    /**
     * Scan the barcode at the back of the document.
     */
    BACK_BARCODE
}