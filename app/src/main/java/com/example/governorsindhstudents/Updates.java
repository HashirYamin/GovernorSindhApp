package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.HashSet;
import java.util.Set;

public class Updates extends AppCompatActivity {
    private ListView updatesListView;
    private UpdatesAdapter updatesAdapter;
    private List<UpdateItem> updatesList = new ArrayList<>();
    private Button deleteBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

        updatesListView = findViewById(R.id.updatesListView);
        updatesAdapter = new UpdatesAdapter(this, updatesList);
        updatesListView.setAdapter(updatesAdapter);
        deleteBtn = findViewById(R.id.deleteAllButton);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        loadUpdates();

        deleteBtn.setOnClickListener(v -> {
            saveDeletedUpdates();
            updatesList.clear();
            updatesAdapter.notifyDataSetChanged();
        });
        updatesListView.setOnItemClickListener((parent, view, position, id) -> {
            UpdateItem selectedUpdate = updatesList.get(position);
            String updateType = selectedUpdate.getType();

            // Log the updateType value
            Log.d("UpdatesActivity", "Update Type: " + updateType);

            Intent intent = null;
            switch (updateType) {
                case "files":
                    intent = new Intent(Updates.this, LecturesActivity.class);
                    break;
                case "links":
                    intent = new Intent(Updates.this, LinksActivity.class);
                    break;
                case "assignments":
                    intent = new Intent(Updates.this, AssignmentsActivity.class);
                    break;
                case "videos":
                    intent = new Intent(Updates.this, VediosActivity.class);
                    break;
                default:
                    Log.w("UpdatesActivity", "Unknown update type: " + updateType);
                    break;
            }

            if (intent != null) {
                Toast.makeText(Updates.this, "Opening: " + updateType, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

    }

    private void loadUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference updatesRef = db.collection("updates");

        Set<String> deletedUpdates = sharedPreferences.getStringSet("deletedUpdates", new HashSet<>());

        updatesRef.orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm a");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        for (DocumentSnapshot document : task.getResult()) {
                            String type = document.getString("type");
                            String documentId = document.getId();

                            // Skip if the update is in the deletedUpdates set
                            if (deletedUpdates.contains(documentId)) {
                                continue;
                            }

                            Date date = document.getTimestamp("timestamp").toDate();
                            String formattedDate = dateFormat.format(date);

                            updatesList.add(new UpdateItem(document.getId(), type, formattedDate));

                        }
                        updatesAdapter.notifyDataSetChanged();

                        // Save the current time as the last seen update time
                        long currentTime = System.currentTimeMillis();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("lastSeenUpdateTime", currentTime);
                        editor.apply();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("resetNotification", true);
                        setResult(RESULT_OK, resultIntent);
                    } else {
                        Log.w("UpdatesActivity", "Error getting documents.", task.getException());
                    }
                });
    }

    private void saveDeletedUpdates() {
        Set<String> deletedUpdates = sharedPreferences.getStringSet("deletedUpdates", new HashSet<>());

        for (UpdateItem update : updatesList) {
            deletedUpdates.add(update.getId());
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("deletedUpdates", deletedUpdates);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
