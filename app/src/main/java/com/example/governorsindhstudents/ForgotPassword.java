package com.example.governorsindhstudents;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPassword extends AppCompatActivity {

    private EditText Email;
    private Button ResetBtn, BackBtn;
    FirebaseAuth mAuth;
    String stdEmail;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        Email = findViewById(R.id.email_box);
        ResetBtn = findViewById(R.id.reset_btn);
        BackBtn = findViewById(R.id.back_btn);

        ResetBtn.setOnClickListener(view -> {
            stdEmail = Email.getText().toString().trim();
            if (TextUtils.isEmpty(stdEmail) || !Patterns.EMAIL_ADDRESS.matcher(stdEmail).matches()) {
                Email.setError("Enter a valid registered Email Id");
            } else {
                ResetPasswrod();
            }
        });
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



//        NewPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String password = NewPassword.getText().toString();
//                if (isValidPassword(password)) {
//                    newPasswordLayout.setHelperText("Strong Password!!");
//                    newPasswordLayout.setError(null);
//                } else {
//                    newPasswordLayout.setError("Password must contain alphabets, a special character, and a digit");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });

//        ChangePassword.setOnClickListener(view -> {
//            String newPassword = NewPassword.getText().toString().trim();
//            String confirmPassword = ConfirmPassword.getText().toString().trim();
//
//            if (TextUtils.isEmpty(newPassword)) {
//                NewPassword.setError("Enter New Password");
//            } else if (!isValidPassword(newPassword)) {
//                NewPassword.setError("Password must be at least 8 characters long and include a letter, a digit, and a special symbol");
//            } else if (!newPassword.equals(confirmPassword)) {
//                ConfirmPassword.setError("Password and Confirm Password didn't match");
//            } else {
//                updatePasswordWithoutReauthentication(newPassword);
//            }
//        });
    }

    private void ResetPasswrod() {
        mAuth.sendPasswordResetEmail(stdEmail)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ForgotPassword.this, "Reset Password link has beend to your registered Email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ForgotPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();  // This will handle the normal back button behavior.
        }
    }
}

//    private void updatePasswordWithoutReauthentication(String newPassword) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // Update password
//            user.updatePassword(newPassword).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // Sign out the user after password change
//                    FirebaseAuth.getInstance().signOut();
//
//                    // Redirect to MainActivity to login with the new password
//                    Toast.makeText(ForgotPassword.this, "Password updated successfully. Please log in with your new password.", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Toast.makeText(ForgotPassword.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
////
////    public static boolean isValidPassword(String password) {
////        if (password.length() < 8) {
////            return false;
////        }
////
////        boolean hasLetter = false;
////        boolean hasDigit = false;
////        boolean hasSpecialChar = false;
////
////        for (char c : password.toCharArray()) {
////            if (Character.isLetter(c)) {
////                hasLetter = true;
////            } else if (Character.isDigit(c)) {
////                hasDigit = true;
////            } else if (!Character.isLetterOrDigit(c)) {
////                hasSpecialChar = true;
////            }
////        }
////        return hasLetter && hasDigit && hasSpecialChar;
////    }


