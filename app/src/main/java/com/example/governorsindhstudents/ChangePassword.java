package com.example.governorsindhstudents;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private TextInputLayout newPasswordLayout;
    private EditText NewPassword;
    private EditText ConfirmPassword;
    private Button ChangePassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        NewPassword = findViewById(R.id.new_password);
        newPasswordLayout = findViewById(R.id.material_new_password);
        ConfirmPassword = findViewById(R.id.confirm_password);
        ChangePassword = findViewById(R.id.change_password);

        NewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = NewPassword.getText().toString();
                if (isValidPassword(password)) {
                    newPasswordLayout.setHelperText("Strong Password!!");
                    newPasswordLayout.setError("");
                } else {
                    newPasswordLayout.setError("");
                    newPasswordLayout.setError("Password must contain alphabets, a special char and a digit ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ChangePassword.setOnClickListener(view -> {
            String newPassword = NewPassword.getText().toString();
            String confirmPassword = ConfirmPassword.getText().toString();

            if(TextUtils.isEmpty(newPassword)){
                NewPassword.setError("Enter New Password");
            }else if (!isValidPassword(newPassword)){
                NewPassword.setError("Password must be at least 8 characters long and include a letter, a digit, and a special symbol");
            }else if (!newPassword.equals(confirmPassword)){
                ConfirmPassword.setError("Password and Confirm Password did'nt match");
            }else {
                updatePassword(newPassword);
            }
        });
    }
    public void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangePassword.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
    public static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        return hasLetter && hasDigit && hasSpecialChar;
    }

}