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

package com.scandit.datacapture.expirymanagementsample.barcodecount;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.batch.data.TrackedBarcode;
import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountStatus;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusItem;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusProvider;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusProviderCallback;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusResult;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusResultSuccess;
import com.scandit.datacapture.core.ui.icon.ScanditIcon;
import com.scandit.datacapture.core.ui.icon.ScanditIconShape;
import com.scandit.datacapture.core.ui.icon.ScanditIconType;
import com.scandit.datacapture.expirymanagementsample.R;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeManager;

import java.util.ArrayList;
import java.util.List;

public class StatusProvider implements BarcodeCountStatusProvider {

    private static final long STATUS_DELAY = 500L;
    private static final int EXPIRED_ICON_BORDER_WIDTH_DP = 1;

    private final Handler handler = new Handler();

    private final String modeEnableMessage;
    private final String modeDisableMessage;

    private final float expiredIconBorderWidth;

    public StatusProvider(Context context) {
        this.modeEnableMessage = context.getString(R.string.status_mode_enable_message);
        this.modeDisableMessage = context.getString(R.string.status_mode_disable_message);
        this.expiredIconBorderWidth = dpToPx(context, EXPIRED_ICON_BORDER_WIDTH_DP);
    }

    @Override
    public void onStatusRequested(
        @NonNull List<TrackedBarcode> barcodes,
        @NonNull BarcodeCountStatusProviderCallback callback
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Add a delay to simulate fetching the data
                    Thread.sleep(STATUS_DELAY);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // If we have information that the item is expired
                            // add a BarcodeCountStatusItem with a custom icon to the result.
                            List<BarcodeCountStatusItem> statusList = new ArrayList<>();
                            for (TrackedBarcode barcode : barcodes) {
                                boolean isExpired = BarcodeManager.getInstance()
                                    .isBarcodeDataExpired(barcode.getBarcode().getData());

                                statusList.add(BarcodeCountStatusItem.createWithIcon(
                                    barcode,
                                    isExpired ? createExpiredIcon() : null
                                ));
                            }

                            BarcodeCountStatusResult result = BarcodeCountStatusResultSuccess.create(
                                statusList,
                                modeEnableMessage,
                                modeDisableMessage
                            );
                            callback.onStatusReady(result);
                        }
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private ScanditIcon createExpiredIcon() {
        return ScanditIcon.builder()
            .withIcon(ScanditIconType.EXPIRED_ITEM)
            .withIconColor(Color.BLACK)
            .withBackgroundColor(0xFFD32F2F)
            .withBackgroundShape(ScanditIconShape.CIRCLE)
            .withBackgroundStrokeColor(Color.WHITE)
            .withBackgroundStrokeWidth(expiredIconBorderWidth)
            .build();
    }

    private static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
