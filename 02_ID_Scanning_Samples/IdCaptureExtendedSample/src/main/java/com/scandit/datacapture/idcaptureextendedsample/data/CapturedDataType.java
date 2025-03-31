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

package com.scandit.datacapture.idcaptureextendedsample.data;

/**
 * The kind of personal identification document or its part where the data is currently extracted
 * from.
 *
 * In this sample, the user may extract data from:
 * * barcodes - for example PDF417 present at the back side of US Driver's Licenses,
 * * Machine Readable Zones (MRZ) - like ones in passports or European IDs,
 * * human-readable text (VIZ) - like the holder's name or date of birth printed on an ID.
 */
public enum CapturedDataType {
    BARCODE,
    MRZ,
    VIZ
}