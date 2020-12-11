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

package com.scandit.datacapture.barcodecapturesettingssample.settings.view;

import androidx.annotation.StringRes;
import com.scandit.datacapture.barcodecapturesettingssample.R;

public enum ViewSettingsEntry {
    SCAN_AREA(R.string.scan_area),
    POINT_OF_INTEREST(R.string.point_of_interest),
    OVERLAY(R.string.overlay),
    VIEWFINDER(R.string.view_finder),
    LOGO(R.string.logo),
    GESTURES(R.string.gestures),
    CONTROLS(R.string.controls);

    @StringRes public final int displayNameResource;

    ViewSettingsEntry(@StringRes int displayNameResource) {
        this.displayNameResource = displayNameResource;
    }
}
