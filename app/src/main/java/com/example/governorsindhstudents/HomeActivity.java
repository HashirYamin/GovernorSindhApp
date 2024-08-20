package com.example.governorsindhstudents;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private TextView notifyStudentsText, Name, RollNo;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CardView notificationCardView, ContactUs;
    private ActivityResultLauncher<Intent> updatesLauncher;
    private CardView academicsCardView, circularCardView, contactUsCardView;
    private CardView lastSelectedCardView = null;
    private CardView currentlySelectedCardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigation_view);
        toolbar = findViewById(R.id.toolbar);
        Name = findViewById(R.id.name);
        RollNo = findViewById(R.id.roll_no);
        notificationCardView = findViewById(R.id.news_and_announcement);
        notifyStudentsText = findViewById(R.id.notify_students_text);
        academicsCardView = findViewById(R.id.acedamics);
        circularCardView = findViewById(R.id.circular_cardview); // Example ID, replace with actual IDs
        contactUsCardView = findViewById(R.id.contact_us_cardview);

        currentlySelectedCardView = academicsCardView;
        academicsCardView.setSelected(true);

        academicsCardView.setOnClickListener(view -> {
            onCardViewClicked(academicsCardView);
            replaceFragment(getFragmentFromCardView(R.id.acedamics));
        });
        circularCardView.setOnClickListener(view -> {
            onCardViewClicked(circularCardView);
            replaceFragment(getFragmentFromCardView(R.id.circular_cardview));
        });
        contactUsCardView.setOnClickListener(view -> {
            onCardViewClicked(contactUsCardView);
            replaceFragment(getFragmentFromCardView(R.id.contact_us_cardview));
        });


        listenForUpdates();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        retrieveStudentData();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home_bottom);
        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            boolean showProfileLayout = false;

            if (item.getItemId() == R.id.profile) {
                fragment = new ProfileFragment();
                showProfileLayout = true;
            } else {
                fragment = getFragmentForMenuItem(item.getItemId());
            }

            if (fragment != null) {
                manageFrameLayoutsVisibility(showProfileLayout);
                getSupportFragmentManager().beginTransaction()
                        .replace(showProfileLayout ? R.id.profile_frame_layout : R.id.frame_layout, fragment)
                        .commit();
            }

            return true;
        });

        getUpdateCountFromDB();

        updatesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            boolean resetNotification = result.getData().getBooleanExtra("resetNotification", false);
                            if (resetNotification) {
                                updateNotificationUI(0); // Reset the notification UI
                            }
                        }
                    }
                }
        );

        notificationCardView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Updates.class);
            updatesLauncher.launch(intent);
        });

    }

    private void onCardViewClicked(CardView selectedCardView) {
        if (currentlySelectedCardView != null) {
            currentlySelectedCardView.setSelected(false); // Remove effect from previous
        }

        selectedCardView.setSelected(true); // Apply effect to new
        currentlySelectedCardView = selectedCardView; // Update the currently selected CardView
    }

    private void startActivityById(int itemId) {
        if (itemId == R.id.notifications) {
            Intent intent = new Intent(this, Updates.class);
            startActivity(intent);
        }
    }


    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commitNow();
        }
    }

    private Fragment getFragmentFromCardView(int cardviewId) {
        if (cardviewId == R.id.acedamics) {
            return new HomeFragment();
        } else if (cardviewId == R.id.circular_cardview) {
            return new NewsFragment();
        } else if (cardviewId == R.id.contact_us_cardview) {
            return new ContactUsFragment();
        } else {
            return null;
        }
    }

    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.home_bottom) {
            return new HomeFragment();
        } else if (itemId == R.id.circular) {
            return new NewsFragment();
        } else if (itemId == R.id.home_nav) {
            return new HomeFragment();
        } else if (itemId == R.id.contact_us_cardview) {
            return new ContactUsFragment();
        } else if (itemId == R.id.circular_bottom) {
            return new NewsFragment();
        } else if (itemId == R.id.contacts) {
            return new ContactUsFragment();
        } else if (itemId == R.id.contact_us_bottom) {
            return new ContactUsFragment();
        } else if (itemId == R.id.yt_videos) {
            return new VediosFragment();
        } else if (itemId == R.id.assignments) {
            return new AssignmentFragment();
        } else if (itemId == R.id.lectures) {
            return new LecturesFragment();
        } else if (itemId == R.id.links) {
            return new LinksFragment();
        } else if (itemId == R.id.logout) {
            showLogoutConfirmationDialog();
            return null;
        } else {
            return null;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Check if the profile fragment is currently shown
        startActivityById(item.getItemId());
        Fragment profileFragment = fragmentManager.findFragmentById(R.id.profile_frame_layout);
        if (profileFragment != null) {
            fragmentManager.beginTransaction().remove(profileFragment).commitNow();
            // Hide the profile frame layout
            findViewById(R.id.profile_frame_layout).setVisibility(View.GONE);
            findViewById(R.id.relative_profile_layout).setVisibility(View.GONE);
            findViewById(R.id.frame_layout).setVisibility(View.VISIBLE);
        }

        // Replace the current fragment with the selected one from the navigation drawer
        replaceFragment(getFragmentForMenuItem(item.getItemId()));
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    private void retrieveStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference documentReference = db.collection("students").document(userId);

            // Load data from SharedPreferences first
            SharedPreferences sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
            String savedName = sharedPreferences.getString("studentName", "");
            String savedRollNo = sharedPreferences.getString("rollNo", "");

            if (!savedName.isEmpty() && !savedRollNo.isEmpty()) {
                Name.setText(savedName);
                RollNo.setText(savedRollNo);
            }

            // Fetch data from Firestore
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("studentName");
                        String rollNo = documentSnapshot.getString("rollNo");

                        Name.setText(studentName);
                        RollNo.setText(rollNo);

                        // Save the data to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("studentName", studentName);
                        editor.putString("rollNo", rollNo);
                        editor.apply();
                    } else {
                        Toast.makeText(HomeActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to fetch data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(HomeActivity.this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(HomeActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateNotificationUI(int updateCount) {
        TextView notificationTextView = findViewById(R.id.notify_students_text);
        CardView notificationCardView = findViewById(R.id.news_and_announcement);

        if (updateCount > 0) {
            notificationTextView.setTextColor(Color.RED);
            notificationTextView.setText("New Updates Available");
            // Show notification icon
            notificationCardView.findViewById(R.id.notify_icon).setVisibility(View.VISIBLE);
            notificationCardView.findViewById(R.id.notify_circle_icon).setVisibility(View.VISIBLE);
        } else {
            notificationCardView.findViewById(R.id.no_notify_icon).setVisibility(View.VISIBLE);
            notificationTextView.setTextColor(Color.BLACK);
            notificationTextView.setText("No Updates available");
            notificationCardView.findViewById(R.id.notify_circle_icon).setVisibility(View.INVISIBLE);
            // Hide notification icon
            notificationCardView.findViewById(R.id.notify_icon).setVisibility(View.GONE);
        }
    }

    // Placeholder method for getting the update count from the database
    private void getUpdateCountFromDB() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            CollectionReference updatesRef = db.collection("updates");

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            long lastSeenTime = sharedPreferences.getLong("lastSeenUpdateTime", 0);

            updatesRef.whereGreaterThan("timestamp", new Timestamp(new Date(lastSeenTime)))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int newUpdateCount = task.getResult().size();
                            updateNotificationUI(newUpdateCount);
                        } else {
                            Log.w("HomeActivity", "Error getting new updates count.", task.getException());
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("resetNotification", false)) {
            updateNotificationUI(0); // Reset the notification UI
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean resetNotification = data.getBooleanExtra("resetNotification", false);
            if (resetNotification) {
                updateNotificationUI(0); // Reset the notification UI to "No Updates Available"
            }
        }
    }



    private void listenForUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference updatesRef = db.collection("updates");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        long lastSeenTime = sharedPreferences.getLong("lastSeenUpdateTime", 0);

        updatesRef.whereGreaterThan("timestamp", new Timestamp(new Date(lastSeenTime)))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("MainActivity", "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        int updateCount = snapshots.size();
                        updateNotificationUI(updateCount);
                    } else {
                        updateNotificationUI(0);
                    }
                });
    }
    private void manageFrameLayoutsVisibility(boolean showProfileLayout) {
        findViewById(R.id.frame_layout).setVisibility(showProfileLayout ? View.GONE : View.VISIBLE);
        findViewById(R.id.profile_frame_layout).setVisibility(showProfileLayout ? View.VISIBLE : View.GONE);
        findViewById(R.id.relative_profile_layout).setVisibility(showProfileLayout ? View.VISIBLE : View.GONE);
    }
    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        // Set the positive button ("Yes")
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked "Yes", so log them out
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Set the negative button ("No")
        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked "No", so dismiss the dialog
            dialog.dismiss();
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
