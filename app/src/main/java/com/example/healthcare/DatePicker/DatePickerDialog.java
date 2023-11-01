package com.example.healthcare.DatePicker;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Set the maximum date to today
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        long maxDate = mCalendar.getTimeInMillis();

        // Set the minimum date
        mCalendar.set(Calendar.YEAR, 1900); //set a very old year as the minimum
        long minDate = mCalendar.getTimeInMillis();

        android.app.DatePickerDialog dialog = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dialog = new android.app.DatePickerDialog(requireActivity(),
                    (android.app.DatePickerDialog.OnDateSetListener) requireActivity(),
                    year, month, dayOfMonth);
            // Set the minimum date
            dialog.getDatePicker().setMinDate(minDate);

            // Set the maximum date
            dialog.getDatePicker().setMaxDate(maxDate);
        }
        return dialog;
    }
}


