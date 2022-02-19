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

package com.scandit.datacapture.vincodessample.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.scandit.datacapture.vincodessample.R;

/*
 * A fragment that displays an AlertDialog with the specified title and message.
 */
public class AlertDialogFragment extends DialogFragment {
    private static final String KEY_TITLE_RES = "title_res";
    private static final String KEY_MESSAGE = "message";

    private Callbacks callbacks;

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

    public static AlertDialogFragment newInstance(@StringRes int title, String message) {
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RES, title);
        arguments.putString(KEY_MESSAGE, message);

        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @StringRes int titleRes = getArguments().getInt(KEY_TITLE_RES);
        String message = getArguments().getString(KEY_MESSAGE);

        return new AlertDialog.Builder(requireContext())
                .setTitle(titleRes)
                .setMessage(message)
                .setPositiveButton(R.string.result_button_resume, null)
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        callbacks.onAlertDismissed();
    }

    public interface Callbacks {
        void onAlertDismissed();
    }
}
