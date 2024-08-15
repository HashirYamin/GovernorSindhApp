package com.example.governorsindhstudents;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PatternMatcher implements TextWatcher {
    private EditText editText;
    private String current = "";

    public PatternMatcher(EditText editText) {
        this.editText = editText;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().equals(current)) {
            String cleanString = s.toString().replaceAll("[^\\d]", "");
            String formattedString = formatCNIC(cleanString);

            current = formattedString;
            editText.removeTextChangedListener(this);
            editText.setText(formattedString);
            editText.setSelection(formattedString.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatCNIC(String input) {
        StringBuilder sb = new StringBuilder(input);

        if (sb.length() > 5) {
            sb.insert(5, '-');
        }
        if (sb.length() > 13) {
            sb.insert(13, '-');
        }

        return sb.toString();
    }
}
