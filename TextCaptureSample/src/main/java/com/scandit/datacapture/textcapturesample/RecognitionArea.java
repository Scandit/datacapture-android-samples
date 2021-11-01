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

/*
 * Controls which part of the frame will be taken into account when performing text recognition.
 * The rest of the frame will be ignored. This option is useful when you know exactly where
 * the text that you want to recognize will appear - it allows you to remove noise and improve
 * accuracy.
 *
 * While this sample uses only general `TOP`, `CENTER` and `BOTTOM`, the location selection might
 * be for example a rectangle or a circle that you can size and position to tailor it to your
 * needs.
 */
public enum RecognitionArea {
    TOP(0.25f),
    CENTER(0.5f),
    BOTTOM(0.75f);

    private float centerY;

    RecognitionArea(float centerY) {
        this.centerY = centerY;
    }

    public float getCenterY() {
        return centerY;
    }
}
