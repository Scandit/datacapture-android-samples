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
import android.os.Handler;

import androidx.annotation.NonNull;

import com.scandit.datacapture.barcode.count.ui.view.BarcodeCountStatus;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusItem;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusProvider;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusProviderCallback;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusResult;
import com.scandit.datacapture.barcode.count.ui.view.status.BarcodeCountStatusResultSuccess;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.expirymanagementsample.R;
import com.scandit.datacapture.expirymanagementsample.managers.BarcodeManager;

import java.util.ArrayList;
import java.util.List;

public class StatusProvider implements BarcodeCountStatusProvider {

    private static final long STATUS_DELAY = 500L;

    private final Handler handler = new Handler();

    private final String modeEnableMessage;
    private final String modeDisableMessage;

    public StatusProvider(Context context) {
        this.modeEnableMessage = context.getString(R.string.status_mode_enable_message);
        this.modeDisableMessage = context.getString(R.string.status_mode_disable_message);
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
                            // add a BarcodeCountStatusItem with status EXPIRED to the result.
                            List<BarcodeCountStatusItem> statusList = new ArrayList<>();
                            for (TrackedBarcode barcode : barcodes) {
                                boolean isExpired = BarcodeManager.getInstance()
                                    .isBarcodeDataExpired(barcode.getBarcode().getData());
                                statusList.add(BarcodeCountStatusItem.create(
                                    barcode,
                                    isExpired ? BarcodeCountStatus.EXPIRED : BarcodeCountStatus.NONE
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
}
