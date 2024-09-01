package com.example.governorsindhstudents;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.governorsindhstudents.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private SimpleAdapter adapter;
    private List<Map<String, String>> eventsList;
    private FirebaseFirestore firestore;
    private String date, title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_events);

        listView = findViewById(R.id.listView_events);
        progressBar = findViewById(R.id.progress_bar_events);

        eventsList = new ArrayList<>();

        adapter = new SimpleAdapter(
                this,
                eventsList,
                R.layout.events_item_list,
                new String[]{"date", "title", "description"},
                new int[]{R.id.tvEventsDate,R.id.tvEventsName, R.id.tvEventsDetails}
        );
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchEventsFromFirestore();
    }

    private void fetchEventsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            eventsList.clear();

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                date = document.getString("date");
                                title = document.getString("title");
                                description = document.getString("description");

                                // Check if any field is null
                                if (date == null) date = "No Date";
                                if (title == null) title = "No Title";
                                if (description == null) date = "No Description";


                                // Create a map to hold the title and details
                                Map<String, String> eventItem = new HashMap<>();
                                eventItem.put("date", date);
                                eventItem.put("title", title);
                                eventItem.put("description", description);

                                eventsList.add(eventItem);
                            }

                            adapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(EventsActivity.this, "No events found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log the error message
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(EventsActivity.this, "Failed to load events: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
