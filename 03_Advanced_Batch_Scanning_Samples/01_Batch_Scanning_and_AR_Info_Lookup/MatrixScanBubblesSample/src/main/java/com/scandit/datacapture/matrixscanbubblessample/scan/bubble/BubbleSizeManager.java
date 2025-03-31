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

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.core.common.geometry.Quadrilateral;
import com.scandit.datacapture.core.ui.DataCaptureView;

public final class BubbleSizeManager {

    private static final float SCREEN_PERCENTAGE_WIDTH_REQUIRED = 0.1f;

    private final float displayWidth;

    public BubbleSizeManager(Context context) {
        displayWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    // We want to show the bubble overlay only if the barcode takes >= 10% of the screen width.
    public boolean isBarcodeLargeEnoughForBubble(DataCaptureView dataCaptureView, Barcode barcode) {
        // The coordinates of the code in the image-space.
        // This means that the coordinates correspond to actual pixels in the camera image.
        Quadrilateral barcodePreviewLocation = barcode.getLocation();
        // To obtain the current device's display coordinates of the barcode,
        // we need to first convert the above image-space coordinates into screen-space ones,
        // using DataCaptureView::MapFrameQuadrilateralToView.
        // You can check https://docs.scandit.com/data-capture-sdk/android/add-augmented-reality-overlay.html
        // for more information about overlays, views and coordinates.
        Quadrilateral barcodeViewLocation = dataCaptureView.mapFrameQuadrilateralToView(barcodePreviewLocation);
        // Let's now retrieve the quadrilateral corners and check if we want to show the bubble overlay
        // or not based on the width of the barcode.
        float topRightX = barcodeViewLocation.getTopRight().getX();
        float topLeftX = barcodeViewLocation.getTopLeft().getX();
        float bottomRightX = barcodeViewLocation.getBottomRight().getX();
        float bottomLeftX = barcodeViewLocation.getBottomLeft().getX();
        float avgWidth = ((topRightX - topLeftX) + (bottomRightX - bottomLeftX)) / 2;
        return (avgWidth / displayWidth) >= SCREEN_PERCENTAGE_WIDTH_REQUIRED;
    }
}
