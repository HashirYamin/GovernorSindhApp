package com.example.governorsindhstudents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VediosFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private SimpleAdapter adapter;
    private List<Map<String, String>> videoList;
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

        videoList = new ArrayList<>();

        adapter = new SimpleAdapter(
                getContext(),
                videoList,
                R.layout.index_liv,
                new String[]{"description"},
                new int[]{R.id.tvFileName}
        );
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchVideosFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = videoList.get(position).get("url");
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
                            videoList.clear(); // Clear the list to avoid duplication

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String videoDescription = document.getString("videoDescription");
                                String videoUrl = document.getString("videoUrl");

                                Map<String, String> videoData = new HashMap<>();
                                videoData.put("description", videoDescription);
                                videoData.put("url", videoUrl);

                                videoList.add(videoData);
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
