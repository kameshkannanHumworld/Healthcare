package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class UsernameTextWatcher implements TextWatcher {

    private final TextInputEditText editText;
    private final TextInputLayout usernameTextInputLayout;

    public UsernameTextWatcher(TextInputEditText editText,TextInputLayout usernameTextInputLayout) {
        this.editText = editText;
        this.usernameTextInputLayout = usernameTextInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String username = s.toString();


        //Special character text watcher
        editText.addTextChangedListener(new SpecialCharacterTextWatcher(editText));


        // Remove any spaces
        username = username.replaceAll("\\s", "");



        // Check for special character '@'
        int atCount = 0;
        for (char c : username.toCharArray()) {
            if (c == '@') {
                atCount++;
            }
        }
        if (atCount > 1 ) {
            usernameTextInputLayout.setError("Username should contain @ only once.");
            return;
        }

        // Check if the username contains only numbers, letters
        if (!username.matches("[a-zA-Z0-9]+")) {
            usernameTextInputLayout.setError("Username should contain only numbers, letters, and @.");
            return;
        }

        // Enforce minimum and maximum length
        if (username.length() < 8 || username.length() > 25) {
            usernameTextInputLayout.setError("Username should be between 8 and 25 characters.");
            return;
        }

        StringBuilder cleanInput = new StringBuilder();
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '@' || c == '.' || c == '_') {
                cleanInput.append(c);
            }
        }

        if (!s.toString().equals(cleanInput.toString())) {
            editText.setText(cleanInput.toString());
            editText.setSelection(cleanInput.length());
        }


        // If all rules are met, remove the error message
        usernameTextInputLayout.setError(null);
    }
}
