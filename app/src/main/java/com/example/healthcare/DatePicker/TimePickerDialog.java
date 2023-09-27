package com.example.healthcare.DatePicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
public class TimePickerDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new android.app.TimePickerDialog(requireActivity(),
                (android.app.TimePickerDialog.OnTimeSetListener) requireActivity(), hour, minute, false);
    }
}
