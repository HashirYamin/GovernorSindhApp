package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignmentsActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> assignmentTitles;
    private List<String> assignmentUrls;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        listView = findViewById(R.id.listView_assign);
        progressBar = findViewById(R.id.progressBar_assign);

        assignmentTitles = new ArrayList<>();
        assignmentUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assignmentTitles);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchAssignmentsFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = assignmentUrls.get(position);
            String title = assignmentTitles.get(position);
            downloadFile(url, title);
        });
    }

    private void fetchAssignmentsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        // Order by timestamp in descending order
        firestore.collection("assignments")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            assignmentTitles.clear(); // Clear existing list to avoid duplicates
                            assignmentUrls.clear();

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String title = document.getString("title");
                                String linkOrFile = document.getString("linkOrFile");

                                assignmentTitles.add(title);
                                assignmentUrls.add(linkOrFile);
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
