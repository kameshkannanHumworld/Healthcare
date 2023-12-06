package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;


public class MedicineNameTextWatcher implements TextWatcher {

    private final AutoCompleteTextView autoCompleteTextView;

    public MedicineNameTextWatcher(AutoCompleteTextView autoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = editable.toString();

        // Check each character in the input
        StringBuilder cleanInput = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetterOrDigit(c) || isAllowedSpecialCharacter(c)) {
                cleanInput.append(c);
            }
        }

        // Update the input field with the cleaned input
        if (!editable.toString().equals(cleanInput.toString())) {
            autoCompleteTextView.setText(cleanInput.toString());
            autoCompleteTextView.setSelection(cleanInput.length());
        }
    }

    private boolean isAllowedSpecialCharacter(char c) {
        String allowedSpecialCharacters = "!@#$%^&*()_+-=[]|,./<>?:;\\{}\"'`~ ";
//        String allowedSpecialCharacters = "!@#$%^&*";
        return allowedSpecialCharacters.contains(String.valueOf(c));
    }
}

