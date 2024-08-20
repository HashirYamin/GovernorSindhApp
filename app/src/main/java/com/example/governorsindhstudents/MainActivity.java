package com.example.governorsindhstudents;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button buttonLogin, SignUp;
    ProgressBar progressBar;
    private Handler handler;
    TextView forgotPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

         //Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
            return;  // Exit onCreate method
        }

        // Initialize views
        Email = findViewById(R.id.std_email);
        Password = findViewById(R.id.login_password);
        buttonLogin = findViewById(R.id.login_btn);
        SignUp = findViewById(R.id.create_acc);
        progressBar = findViewById(R.id.login_progrss_bar);
        forgotPassword = findViewById(R.id.forgot_password);

        SignUp.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Registration.class)));

        Email = findViewById(R.id.std_email);
        Password = findViewById(R.id.login_password);
        buttonLogin = findViewById(R.id.login_btn);
        buttonLogin.setBackgroundResource(R.drawable.login_buttons_designs);
        SignUp = findViewById(R.id.create_acc);
        SignUp.setBackgroundResource(R.drawable.login_buttons_designs);
        progressBar = findViewById(R.id.login_progrss_bar);
        forgotPassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();



        handler = new Handler();

        buttonLogin.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            setInProgress(true);
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("students").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String studentName = documentSnapshot.getString("studentName");
                                            String rollNo = documentSnapshot.getString("rollNo");
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            intent.putExtra("studentName", studentName);
                                            intent.putExtra("rollNo", rollNo);
                                            startActivity(intent);
                                            finish(); // Close MainActivity
                                        } else {
                                            Toast.makeText(MainActivity.this, "Student data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.forgot_dialog, null);
            EditText emailBox = dialogView.findViewById(R.id.email_box);

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String stdEmail = emailBox.getText().toString().trim();

                    if (TextUtils.isEmpty(stdEmail) || !Patterns.EMAIL_ADDRESS.matcher(stdEmail).matches()) {
                        emailBox.setError("Enter a valid registered Email Id");
                        return;
                    }else {
                        // Check if email exists in Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("students").whereEqualTo("email", stdEmail)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            startActivity(new Intent(MainActivity.this,PhoneNumberVerification.class));
                                        } else {
                                            // Email does not exist
                                            emailBox.setError("This email is not registered");
                                        }
                                    }
                                });
                    }
                }
            });

            dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            dialog.show();
        });
    }
    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.VISIBLE);
        }
    }

}