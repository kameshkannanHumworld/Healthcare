package com.example.healthcare.TextWatcher;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class ClearErrorTextWatcher implements TextWatcher {
    private final TextInputLayout layout;

    public ClearErrorTextWatcher(TextInputLayout layout) {
        this.layout = layout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        layout.setError("");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        layout.setError(null); // Clear error when text changes
    }
}
