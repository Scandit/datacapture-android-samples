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

package com.scandit.datacapture.idcaptureextendedsample.ui.scan;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.scandit.datacapture.idcaptureextendedsample.R;

/*
 * A fragment that displays an AlertDialog with the recognized document.
 */
public class AskScanBackSideDialogFragment extends DialogFragment {
    /**
     * The callbacks that this fragment's host must implement.
     */
    private Callbacks callbacks;

    /**
     * Create an instance of this fragment.
     */
    public static AskScanBackSideDialogFragment create() {
        return new AskScanBackSideDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getHost() instanceof Callbacks) {
            callbacks = (Callbacks) getHost();
        } else if (getParentFragment() instanceof Callbacks) {
            callbacks = (Callbacks) getParentFragment();
        } else {
            throw new ClassCastException("Parent fragment doesn't implement Callbacks!");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.alert_scan_back_title)
                .setMessage(R.string.alert_scan_back_message)
                .setPositiveButton(R.string.scan, (dialog, which) -> {
                    /*
                     * If we continue with scanning the back of the document, the IdCapture settings
                     * will automatically allow only for this to be scanned, blocking you from
                     * scanning other front of IDs. The next `onIdCaptured` will contain data from
                     * both the front and the back scans.
                     */
                    callbacks.onBackSideScanAccepted();
                })
                .setNegativeButton(R.string.skip, (dialog, which) -> {
                    dialog.dismiss();
                    /*
                     * If we want to skip scanning the back of the document, we have to call
                     * `IdCapture().reset()` to allow for another front IDs to be scanned.
                     */
                    callbacks.onBackSideScanSkipped();
                })
                .setCancelable(false)
                .show();
        }

    /**
     * The callbacks that this fragment's host must implement.
     */
    public interface Callbacks {
        /**
         * Invoked when the user decided to capture also the back side of the document.
         */
        void onBackSideScanAccepted();

        /**
         * Invoked when the user decided to skip the back side of the document.
         */
        void onBackSideScanSkipped();
    }
}
