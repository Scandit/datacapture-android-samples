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

import com.scandit.datacapture.id.data.CapturedId;

public class ScanResult {

    private final CapturedId capturedId;
    private final boolean continuousMode;
    private final boolean needsBackScan;

    public ScanResult(CapturedId capturedId, boolean continuousMode, boolean needsBackSCan) {
        this.capturedId = capturedId;
        this.continuousMode = continuousMode;
        this.needsBackScan = needsBackSCan;
    }

    public CapturedId getCapturedId() {
        return capturedId;
    }

    public boolean isContinuousMode() {
        return continuousMode;
    }

    public boolean isNeedsBackScan() {
        return needsBackScan;
    }
}
