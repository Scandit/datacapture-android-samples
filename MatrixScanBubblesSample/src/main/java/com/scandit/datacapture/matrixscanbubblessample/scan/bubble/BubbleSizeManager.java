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

package com.scandit.datacapture.matrixscanbubblessample.scan.bubble;

import android.content.Context;
import com.scandit.datacapture.core.common.geometry.Quadrilateral;

public final class BubbleSizeManager {

    private static final float SCREEN_PERCENTAGE_WIDTH_REQUIRED = 0.1f;

    private final float displayWidth;

    public BubbleSizeManager(Context context) {
        displayWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    // We want to show the bubble overlay only if the barcode takes >= 10% of the screen width.
    public boolean isBarcodeLargeEnoughForBubble(Quadrilateral barcodeLocation) {
        float topRightX = barcodeLocation.getTopRight().getX();
        float topLeftX = barcodeLocation.getTopLeft().getX();
        float bottomRightX = barcodeLocation.getBottomRight().getX();
        float bottomLeftX = barcodeLocation.getBottomLeft().getX();
        float avgWidth = ((topRightX - bottomLeftX) + (bottomRightX - topLeftX)) / 2;
        return (avgWidth / displayWidth) >= SCREEN_PERCENTAGE_WIDTH_REQUIRED;
    }
}
