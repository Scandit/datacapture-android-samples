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

package com.scandit.datacapture.ageverifieddeliverysample.ui.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * The DialogFragment to pick a date.
 */
public class LocalDatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    /**
     * The keys of the arguments of this fragment.
     */
    private static final String KEY_DAY = "DAY";
    private static final String KEY_MONTH = "MONTH";
    private static final String KEY_YEAR = "YEAR";
    private static final String KEY_HIDE_FUTURE_DATES = "HIDE_FUTURE_DATES";

    /**
     * The listener called when the user picks a date.
     */
    @Nullable
    private OnDateSetListener listener;

    /**
     * Create an instance of this fragment with the given date preselected.
     */
    public static LocalDatePickerDialogFragment create(LocalDate date, boolean hideFutureDates) {
        LocalDatePickerDialogFragment fragment = new LocalDatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_DAY, date.getDayOfMonth());
        args.putInt(KEY_MONTH, date.getMonth().getValue());
        args.putInt(KEY_YEAR, date.getYear());
        args.putBoolean(KEY_HIDE_FUTURE_DATES, hideFutureDates);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /*
         * Display the date picker.
         */
        int day = getArguments().getInt(KEY_DAY);
        int month = getArguments().getInt(KEY_MONTH);
        int year = getArguments().getInt(KEY_YEAR);
        boolean hideFutureDates = getArguments().getBoolean(KEY_HIDE_FUTURE_DATES);

        // LocalDate numbers months from 1, DatePickerDialog from 0.
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month - 1, day);

        if (hideFutureDates) {
            datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        }

        return datePickerDialog;
    }

    /**
     * Inform the listener if any that a date is selected.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (listener != null) {
            // LocalDate numbers months from 1, DatePickerDialog from 0.
            listener.onDateSet(LocalDate.of(year, month + 1, dayOfMonth));
        }
    }

    /**
     * Set the listener called when the user picks a date.
     */
    public void setOnDateSetListener(@Nullable OnDateSetListener listener) {
        this.listener = listener;
    }

    /**
     * The listener called when the user picks a date.
     */
    public interface OnDateSetListener {
        void onDateSet(LocalDate date);
    }
}
