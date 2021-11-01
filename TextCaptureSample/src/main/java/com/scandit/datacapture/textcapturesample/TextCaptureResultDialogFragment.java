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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/*
 * A fragment that displays an AlertDialog with the recognized text.
 */
public class TextCaptureResultDialogFragment extends DialogFragment {
    private static final String KEY_RESULT = "result";

    private Callbacks callbacks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof Callbacks) {
            callbacks = (Callbacks) getParentFragment();
        } else {
            throw new ClassCastException("Parent fragment doesn't implement Callbacks!");
        }
    }

    public static TextCaptureResultDialogFragment newInstance(String result) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_RESULT, result);

        TextCaptureResultDialogFragment fragment = new TextCaptureResultDialogFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String result = getArguments().getString(KEY_RESULT);

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.result_title)
                .setMessage(result)
                .setPositiveButton(R.string.result_button_resume, null)
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        callbacks.onResultDismissed();
    }

    public interface Callbacks {
        void onResultDismissed();
    }
}
