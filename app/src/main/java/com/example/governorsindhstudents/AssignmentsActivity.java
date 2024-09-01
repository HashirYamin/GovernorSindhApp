package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.governorsindhstudents.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentsActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private SimpleAdapter adapter;
    private List<Map<String, String>> assignmentList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_assignments);

        listView = findViewById(R.id.listView_assign);
        progressBar = findViewById(R.id.progressBar_assign);

        assignmentList = new ArrayList<>();

        adapter = new SimpleAdapter(
                this,
                assignmentList,
                R.layout.items_list, // Your custom layout
                new String[]{"title", "description"}, // The keys used in the HashMap
                new int[]{R.id.tvFileName, R.id.tvDescription} // The TextViews in items_list.xml
        );
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchAssignmentsFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = assignmentList.get(position).get("linkOrFile");
            String title = assignmentList.get(position).get("title");
            downloadFile(url, title);
        });
    }

    private void fetchAssignmentsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("assignments")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            assignmentList.clear(); // Clear existing list to avoid duplicates

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String title = document.getString("title");
                                String description = document.getString("description");
                                String linkOrFile = document.getString("linkOrFile");

                                // Add data to the list
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("title", title);
                                dataMap.put("description", description); // Include description
                                dataMap.put("linkOrFile", linkOrFile);
                                assignmentList.add(dataMap);
                            }
                            adapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(AssignmentsActivity.this, "No assignments found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AssignmentsActivity.this, "Failed to load assignments.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(fileName);
        request.setDescription("Downloading assignment...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Downloading " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Download Manager not available", Toast.LENGTH_SHORT).show();
        }
    }
}
