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

package com.scandit.datacapture.idcapturesettingssample.ui.scan;

import com.scandit.datacapture.idcapturesettingssample.ui.Event;
import com.scandit.datacapture.idcapturesettingssample.ui.result.CaptureResult;

/**
 * An event for the fragment to display the blocking UI to inform that back scanning is available.
 */
class ShowBackScanAvailableEvent extends Event<CaptureResult> {
    public ShowBackScanAvailableEvent(CaptureResult content) {
        super(content);
    }
}
