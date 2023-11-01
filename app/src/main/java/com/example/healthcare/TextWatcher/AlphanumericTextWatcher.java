package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AlphanumericTextWatcher implements TextWatcher {
    private final TextInputEditText textInputEditText;

    public AlphanumericTextWatcher(TextInputEditText editText) {
        this.textInputEditText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
            textInputEditText.setText(cleanInput.toString());
            textInputEditText.setSelection(cleanInput.length());
        }
    }

    private boolean isAllowedSpecialCharacter(char c) {
        String allowedSpecialCharacters = "!@#$%^&*()_+-=[]|,./<>?:;\\{}\"'`~";
//        String allowedSpecialCharacters = "!@#$%^&*";
        return allowedSpecialCharacters.contains(String.valueOf(c));
    }
}