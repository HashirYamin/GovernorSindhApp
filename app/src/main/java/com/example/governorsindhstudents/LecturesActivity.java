package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LecturesActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> fileNames;
    private List<String> fileUrls;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        fileNames = new ArrayList<>();
        fileUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchFilesFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = fileUrls.get(position);
            String fileName = fileNames.get(position);
            downloadFile(url, fileName);
        });
    }

    private void fetchFilesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("files")
                .orderBy("timestamp", Query.Direction.DESCENDING)  // Order by timestamp in descending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            fileNames.clear();  // Clear the list to avoid duplication
                            fileUrls.clear();   // Clear the URLs list as well

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String fileName = document.getString("fileName");
                                String fileUrl = document.getString("fileUrl");

                                Log.d("LecturesActivity", "File Name: " + fileName);
                                Log.d("LecturesActivity", "File URL: " + fileUrl);

                                fileNames.add(fileName);
                                fileUrls.add(fileUrl);
                            }
                            adapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(LecturesActivity.this, "No files found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LecturesActivity.this, "Failed to load files.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(fileName);
        request.setDescription("Downloading file...");
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
