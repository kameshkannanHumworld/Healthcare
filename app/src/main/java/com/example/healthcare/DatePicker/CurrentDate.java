package com.example.healthcare.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class CurrentDate {
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
