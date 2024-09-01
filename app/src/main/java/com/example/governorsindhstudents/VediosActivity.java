package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
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

public class VediosActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private List<Map<String, String>> dataList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedios);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_vedios);

        listView = findViewById(R.id.listView_videos);
        progressBar = findViewById(R.id.progressBar_videos);

        dataList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        fetchVideosFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = dataList.get(position).get("videoUrl");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    private void fetchVideosFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("youtubeVideos")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            dataList.clear(); // Clear the list to avoid duplication

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String videoDescription = document.getString("videoDescription");
                                String videoUrl = document.getString("videoUrl");

                                // Prepare data for each video
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("fileName", videoDescription);  // Assign description to fileName
                                dataMap.put("videoUrl", videoUrl);          // Store the URL
                                dataList.add(dataMap);
                            }

                            // Bind data to the ListView using SimpleAdapter
                            SimpleAdapter adapter = new SimpleAdapter(
                                    VediosActivity.this,
                                    dataList,
                                    R.layout.index_liv,  // Custom layout for list items
                                    new String[]{"fileName"}, // Keys in dataMap
                                    new int[]{R.id.tvFileName}  // TextView in items_list.xml
                            );

                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(VediosActivity.this, "No videos found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VediosActivity.this, "Failed to load videos.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
