package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private EditText StudentName, Email, Password, ConfirmPassword;
    private Button btnRegister, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private boolean isEmailValid;
    private View tickMark;
    private Handler handler; // Declare Handler
    String UserId;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registration);

        StudentName = findViewById(R.id.std_name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.rg_password);
        ConfirmPassword = findViewById(R.id.rg_confirm_password);
        btnRegister = findViewById(R.id.register_btn);
        btnBack = findViewById(R.id.sign_up_back_btn);
        progressBar = findViewById(R.id.reg_progrss_bar);

//
        Email.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.baseline_email_24, // left drawable
                0, // top drawable
                0, // right drawable (initially hidden)
                0  // bottom drawable
        );
        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEmailValid = isValidEmail(s.toString());
                // Update drawable visibility based on email validity
                updateDrawableVisibility(isEmailValid);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        handler = new Handler();

        btnBack.setOnClickListener(view -> startActivity(new Intent(Registration.this, MainActivity.class)));

        btnRegister.setBackgroundColor(Color.parseColor("#7A2700"));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        btnRegister.setOnClickListener(view -> {
            if (validateInputs()) {
                setInProgress(true);

                final String studentName = StudentName.getText().toString().trim();
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            setInProgress(false);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            // Store user data in Firestore
                                            storeUserDataInFirestore(user, studentName, email);

                                            // Inform the user to check their email
                                            Toast.makeText(Registration.this, "Verification email sent. Please verify your email.", Toast.LENGTH_LONG).show();

                                            // Redirect to a waiting screen or login screen
                                            redirectToLoginOrVerificationScreen();
                                        } else {
                                            // Failed to send verification email
                                            String errorMessage = emailTask.getException() != null ? emailTask.getException().getMessage() : "Unknown error";
                                            Log.d("TAG", "sendEmailVerification failure: " + errorMessage);
                                            Toast.makeText(Registration.this, "Failed to send verification email: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // Registration failed
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.d("TAG", "createUserWithEmailAndPassword failure: " + errorMessage);
                                Toast.makeText(Registration.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private boolean validateInputs() {
        final String studentName = StudentName.getText().toString().trim();
        final String email = Email.getText().toString().trim();
        final String password = Password.getText().toString().trim();
        final String confirmPassword = ConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(studentName)) {
            StudentName.setError("Please enter your name");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            Email.setError("Please enter your email");
            isValid = false;
        } else if (!isValidEmail(email)) {
            Email.setError("Invalid email format");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            Password.setError("Please set your password");
            isValid = false;
        } else if (!isValidPassword(password)) {
            Password.setError("Password must be at least 8 characters long and include a letter, a digit, and a special symbol");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            ConfirmPassword.setError("Password and Confirm Password don't match");
            isValid = false;
        }

        return isValid;
    }


    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com");
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

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
        }
    }
    private void updateDrawableVisibility(boolean isVisible) {
        if (isVisible) {
            // Show tick mark
            Email.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_email_24, // left drawable
                    0, // top drawable
                    R.drawable.sharp_check_circle_24, // right drawable
                    0 // bottom drawable
            );
        } else {
            // Hide tick mark
            Email.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.baseline_email_24, // left drawable
                    0, // top drawable
                    0, // right drawable (hidden)
                    0 // bottom drawable
            );
        }
    }
    // Method to store user data in Firestore
    private void storeUserDataInFirestore(FirebaseUser user, String studentName, String email) {
        DocumentReference documentReference = db.collection("students").document(user.getUid());
        Map<String, Object> student = new HashMap<>();
        student.put("studentName", studentName);
        student.put("email", email);

        documentReference.set(student)
                .addOnSuccessListener(unused -> Log.d("TAG", "User data successfully stored in Firestore"))
                .addOnFailureListener(e -> Log.d("TAG", "Error saving data: " + e));
    }

    // Method to redirect user to login or verification screen
    private void redirectToLoginOrVerificationScreen() {
        // Redirect to a screen where the user is prompted to verify their email
        Intent intent = new Intent(Registration.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the registration activity
    }
}
