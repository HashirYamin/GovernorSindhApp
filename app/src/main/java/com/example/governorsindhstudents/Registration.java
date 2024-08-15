package com.example.governorsindhstudents;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private EditText StudentName, RollNo, Cnic, Email, Password, ConfirmPassword;
    EditText PhoneNo;
    private Button btnRegister, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Handler handler; // Declare Handler
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        StudentName = findViewById(R.id.std_name);
        RollNo = findViewById(R.id.roll_no);
        Cnic = findViewById(R.id.cnic);
        PhoneNo = findViewById(R.id.phone_no);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.rg_password);
        ConfirmPassword = findViewById(R.id.rg_confirm_password);
        btnRegister = findViewById(R.id.register_btn);
        btnBack = findViewById(R.id.sign_up_back_btn);
        progressBar = findViewById(R.id.reg_progrss_bar);

        handler = new Handler();

        btnBack.setOnClickListener(view -> startActivity(new Intent(Registration.this, MainActivity.class)));

        btnRegister.setBackgroundColor(Color.parseColor("#7A2700"));
        Cnic.addTextChangedListener(new PatternMatcher(Cnic));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(view -> {
            if (validateInputs()) {
                setInProgress(true);

                final String studentName = StudentName.getText().toString().trim();
                final String rollNo = RollNo.getText().toString().trim();
                final String cnic = Cnic.getText().toString().trim();
                final String phoneNo = PhoneNo.getText().toString().trim();
                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();

                String formattedPhoneNumber = formatPhoneNumber(phoneNo);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            setInProgress(false);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserId = mAuth.getCurrentUser().getUid();

                                DocumentReference documentReference = db.collection("students").document(UserId);

                                Map<String, Object> student = new HashMap<>();
                                student.put("studentName", studentName);
                                student.put("rollNo", rollNo);
                                student.put("cnic", cnic);
                                student.put("phoneNo", formattedPhoneNumber);
                                student.put("email", email);
                                student.put("password", password);

                                documentReference.set(student).addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Profile is created for " + UserId, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Registration.this, HomeActivity.class);
                                    startActivity(intent);
                                }).addOnFailureListener(e -> {
                                    Log.d("TAG", "onFailure: " + e);
                                    Toast.makeText(Registration.this, "Error saving data", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Toast.makeText(Registration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }

    private boolean validateInputs() {
        final String studentName = StudentName.getText().toString().trim();
        final String rollNo = RollNo.getText().toString().trim();
        final String cnic = Cnic.getText().toString().trim();
        final String phoneNo = PhoneNo.getText().toString().trim();
        final String email = Email.getText().toString().trim();
        final String password = Password.getText().toString().trim();
        final String confirmPassword = ConfirmPassword.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(studentName)) {
            StudentName.setError("Please enter your name");
            isValid = false;
        }

        if (TextUtils.isEmpty(rollNo)) {
            RollNo.setError("Please enter your Roll No");
            isValid = false;
        } else if (!isValidRollNo(rollNo)) {
            RollNo.setError("Roll No must be exactly 8 digits and start with '00'");
            isValid = false;
        }

        if (TextUtils.isEmpty(cnic)) {
            Cnic.setError("Please enter CNIC or Bayform");
            isValid = false;
        } else if (!isValidCNIC(cnic)) {
            Cnic.setError("CNIC format not matched (xxxxx-xxxxxxx-x)");
            isValid = false;
        }

        if (TextUtils.isEmpty(phoneNo)) {
            PhoneNo.setError("Please enter your phone number");
            isValid = false;
        } else if (!isValidPhoneNo(phoneNo)) {
            PhoneNo.setError("Phone number is not valid ");
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

    private boolean isValidRollNo(String rollNo) {
        return Pattern.matches("00\\d{6}", rollNo);
    }

    private boolean isValidCNIC(String cnic) {
        return Pattern.matches("\\d{5}-\\d{7}-\\d", cnic);
    }

    private boolean isValidPhoneNo(String phoneNo) {
        return Pattern.matches("^03[0-9]{9}$", phoneNo);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
    private String formatPhoneNumber(String phoneNo) {
        if (phoneNo.startsWith("0")) {
            phoneNo = phoneNo.substring(1);
        }
        return "+92" + phoneNo;
    }

}
