package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignmentFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> assignmentTitles;
    private List<String> assignmentUrls;
    private FirebaseFirestore firestore;

    public AssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assignment, container, false);

        listView = view.findViewById(R.id.listView_assign);
        progressBar = view.findViewById(R.id.progressBar_assign);

        assignmentTitles = new ArrayList<>();
        assignmentUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, assignmentTitles);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchAssignmentsFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = assignmentUrls.get(position);
            String title = assignmentTitles.get(position);
            downloadFile(url, title);
        });

        return view;
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
                            Toast.makeText(getContext(), "No assignments found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load assignments.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(fileName);
        request.setDescription("Downloading assignment...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(getContext(), "Downloading " + fileName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Download Manager not available", Toast.LENGTH_SHORT).show();
        }
    }
}
