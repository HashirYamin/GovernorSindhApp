package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LecturesActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private List<Map<String, String>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        listView = findViewById(R.id.lectures_list);
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        dataList = new ArrayList<>();

        fetchFilesFromFirestore();

        // Handle item clicks for downloading
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = dataList.get(position).get("fileUrl");
            String fileName = dataList.get(position).get("fileName");
            downloadFile(url, fileName);
        });
    }

    private void fetchFilesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("files")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            dataList.clear();  // Clear the list to avoid duplication

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String fileName = document.getString("fileName");
                                String fileUrl = document.getString("fileUrl");

                                Log.d("LecturesActivity", "File Name: " + fileName);
                                Log.d("LecturesActivity", "File URL: " + fileUrl);

                                // Prepare the data
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("fileName", fileName);
                                dataMap.put("fileUrl", fileUrl);
                                dataList.add(dataMap);
                            }

                            // Bind data to the ListView using SimpleAdapter
                            SimpleAdapter adapter = new SimpleAdapter(
                                    LecturesActivity.this,
                                    dataList,
                                    R.layout.items_list, // Custom layout for list items
                                    new String[]{"fileName"}, // Keys in dataMap
                                    new int[]{R.id.tvFileName} // TextView in lecture_list_item.xml
                            );

                            listView.setAdapter(adapter);
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
