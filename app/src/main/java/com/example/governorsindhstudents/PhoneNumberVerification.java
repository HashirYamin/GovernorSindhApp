package com.example.governorsindhstudents;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PhoneNumberVerification extends AppCompatActivity {

    EditText PhoneNo;
    Button sendOtp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verification);
        PhoneNo = findViewById(R.id.phone_no_otp_verification);
        sendOtp = findViewById(R.id.send_otp_btn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = PhoneNo.getText().toString().trim();
                if (!validate(phoneNo)) {
                    Toast.makeText(PhoneNumberVerification.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                } else {
                    String formattedPhoneNumber = formatPhoneNumber(phoneNo);
                    checkIfPhoneNumberRegistered(formattedPhoneNumber);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Auto-verification or instant verification completed
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    Log.d("PhoneNumberVerification", "Auto-retrieved OTP: " + code);
                    Intent intent = new Intent(PhoneNumberVerification.this, OtpVerification.class);
                    intent.putExtra("otp", code);
                    startActivity(intent);
                } else {
                    Log.d("PhoneNumberVerification", "Auto-retrieved OTP is null");
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneNumberVerification.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PhoneNumberVerification", "Verification failed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // Code has been sent to the user's phone number
                Log.d("PhoneNumberVerification", "Code sent to: " + PhoneNo.getText().toString().trim());
                Intent intent = new Intent(PhoneNumberVerification.this, OtpVerification.class);
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
            }
        };
    }

    private boolean validate(String phoneNo) {
        if (TextUtils.isEmpty(phoneNo)) {
            PhoneNo.setError("Please enter your phone number");
            return false;
        } else if (!isValidPhoneNo(phoneNo)) {
            PhoneNo.setError("Phone number is not valid");
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNo(String phoneNo) {
        return Pattern.matches("^03[0-9]{9}$", phoneNo);
    }

    private String formatPhoneNumber(String phoneNo) {
        if (phoneNo.startsWith("0")) {
            phoneNo = phoneNo.substring(1);
        }
        return "+92" + phoneNo;
    }

    private void checkIfPhoneNumberRegistered(String phoneNo) {
        db.collection("students")
                .whereEqualTo("phoneNo", phoneNo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        sendVerificationCode(phoneNo);
                    } else {
                        Toast.makeText(PhoneNumberVerification.this, "Phone number is not registered", Toast.LENGTH_SHORT).show();
                        Log.d("PhoneNumberVerification", "Phone number not found in Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PhoneNumberVerification.this, "Error checking phone number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PhoneNumberVerification", "Firestore error: " + e.getMessage());
                });
    }

    private void sendVerificationCode(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Log.d("PhoneNumberVerification", "Verification code sent to: " + phoneNo);
    }
}
