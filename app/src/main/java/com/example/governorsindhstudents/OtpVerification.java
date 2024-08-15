package com.example.governorsindhstudents;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpVerification extends AppCompatActivity {

    private EditText otpEditText;
    private Button verifyOtpButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEditText = findViewById(R.id.otp_verification);
        verifyOtpButton = findViewById(R.id.otp_next_btn);
        progressBar = findViewById(R.id.otp_progress_bar);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        verificationId = intent.getStringExtra("verificationId");

        verifyOtpButton.setOnClickListener(v -> {
            String code = otpEditText.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                otpEditText.setError("Enter the OTP");
                return;
            }
            verifyCode(code);
        });
    }

    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpVerification.this, "Verification successful", Toast.LENGTH_SHORT).show();
                            // Redirect to the password reset activity
                            Intent intent = new Intent(OtpVerification.this, ChangePassword.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OtpVerification.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OtpVerification.this, "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
