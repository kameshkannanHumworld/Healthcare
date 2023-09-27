package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordTextWatcher implements TextWatcher {

    private TextInputEditText editText;
    private TextInputLayout passwordTextInputLayout;

    public PasswordTextWatcher(TextInputEditText editText,TextInputLayout passwordTextInputLayout) {
        this.editText = editText;
        this.passwordTextInputLayout = passwordTextInputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String password = s.toString();

        // Remove any spaces
        password = password.replaceAll("\\s", "");

        // Enforce minimum and maximum length
        if (password.length() < 8 || password.length() > 20) {
            passwordTextInputLayout.setError("Password should be between 8 and 20 characters.");
            return;
        }

        // Check for at least one number, one uppercase letter, one lowercase letter, and one special character
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$")) {
            passwordTextInputLayout.setError("Password should contain at least one number, one uppercase letter, one lowercase letter, and one special character.");
            return;
        }

        // If all rules are met, remove the error message
        passwordTextInputLayout.setError(null);
    }
}

