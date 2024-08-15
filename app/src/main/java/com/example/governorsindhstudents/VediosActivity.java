package com.example.governorsindhstudents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class VediosActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> videoDescriptions;
    private List<String> videoUrls;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedios);

        listView = findViewById(R.id.listView_videos);
        progressBar = findViewById(R.id.progressBar_videos);

        videoDescriptions = new ArrayList<>();
        videoUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, videoDescriptions);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchVideosFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = videoUrls.get(position);
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
                            videoDescriptions.clear(); // Clear the list to avoid duplication
                            videoUrls.clear();        // Clear the URLs list as well

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String videoDescription = document.getString("videoDescription");
                                String videoUrl = document.getString("videoUrl");

                                videoDescriptions.add(videoDescription);
                                videoUrls.add(videoUrl);
                            }
                            adapter.notifyDataSetChanged();
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
