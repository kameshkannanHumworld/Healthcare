package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SpecialCharacterTextWatcher implements TextWatcher {

    private final EditText editText;

    public SpecialCharacterTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        StringBuilder cleanInput = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '@' || c == '.' || c == '_') {
                cleanInput.append(c);
            }
        }

        if (!s.toString().equals(cleanInput.toString())) {
            editText.setText(cleanInput.toString());
            editText.setSelection(cleanInput.length());
        }
    }
}

