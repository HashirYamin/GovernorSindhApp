package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText Email, Password;
    private Button buttonLogin;
    private ProgressBar progressBarGoogleSignIn;
//    private Button SignUp;


    ProgressBar progressBar;
    TextView googleText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userId;
    GoogleSignInOptions options;
    GoogleSignInClient client;
    RelativeLayout googleBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        googleBtn = findViewById(R.id.google_sign_in_button);
        progressBarGoogleSignIn = findViewById(R.id.progress_bar_google_sign_in);
        googleText = findViewById(R.id.google_sign_in_text);


        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);
        googleBtn.setOnClickListener(view -> {
            progressBarGoogleSignIn.setVisibility(View.VISIBLE);
            googleText.setVisibility(View.GONE);


            Intent i = client.getSignInIntent();
            startActivityForResult(i,123);

        });

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
//        SignUp = findViewById(R.id.create_acc);
        progressBar = findViewById(R.id.login_progrss_bar);
        int[] colors = {
                Color.parseColor("#DB4437"), // Google Red
                Color.parseColor("#F4B400"), // Google Yellow
                Color.parseColor("#0F9D58"), // Google Green
                Color.parseColor("#4285F4")  // Google Blue
        };
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                progressBar.getIndeterminateDrawable().setColorFilter(colors[i], PorterDuff.Mode.SRC_IN);
                i = (i + 1) % colors.length;
                handler.postDelayed(this, 500); // Change color every 500ms
            }
        };
        handler.post(runnable);
//        forgotPassword = findViewById(R.id.forgot_password);

//        SignUp.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Registration.class)));

        buttonLogin.setBackgroundResource(R.drawable.login_buttons_designs);
//        SignUp = findViewById(R.id.create_acc);
//        SignUp.setBackgroundResource(R.drawable.login_buttons_designs);
        progressBar = findViewById(R.id.login_progrss_bar);
        mAuth = FirebaseAuth.getInstance();




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
                                            String stdEmail = documentSnapshot.getString("email");
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            intent.putExtra("studentName", studentName);
                                            intent.putExtra("email", stdEmail);
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
//        forgotPassword.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ForgotPassword.class)));

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        String userName = user.getDisplayName();
                        String userEmail = user.getEmail();

                        // Save user data to Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", userName);
                        userData.put("email", userEmail);
                        userData.put("uuid", userId);
                        userData.put("emailVerified", user.isEmailVerified());
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users").document(userId).set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("studentName", userName);
                                    editor.putString("email", userEmail);
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}