package com.example.governorsindhstudents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class VediosFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> videoDescriptions;
    private List<String> videoUrls;
    private FirebaseFirestore firestore;

    public VediosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vedios, container, false);

        listView = view.findViewById(R.id.listView_videos);
        progressBar = view.findViewById(R.id.progressBar_videos);

        videoDescriptions = new ArrayList<>();
        videoUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, videoDescriptions);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchVideosFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = videoUrls.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return view;
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
                            Toast.makeText(getContext(), "No videos found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load videos.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
