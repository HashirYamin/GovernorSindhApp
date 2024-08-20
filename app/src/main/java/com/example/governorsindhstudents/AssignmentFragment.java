package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private List<Map<String, String>> dataList;
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

        dataList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        fetchAssignmentsFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = dataList.get(position).get("linkOrFile");
            String title = dataList.get(position).get("title");
            downloadFile(url, title);
        });

        return view;
    }

    private void fetchAssignmentsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("assignments")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            dataList.clear(); // Clear existing list to avoid duplicates

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String title = document.getString("title");
                                String linkOrFile = document.getString("linkOrFile");

                                // Prepare data for each assignment
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("assignmentName", title);  // Assign title to assignmentName
                                dataMap.put("linkOrFile", linkOrFile); // Store the URL or file path
                                dataList.add(dataMap);
                            }

                            // Bind data to the ListView using SimpleAdapter
                            SimpleAdapter adapter = new SimpleAdapter(
                                    getContext(),
                                    dataList,
                                    R.layout.items_list,  // Custom layout for list items
                                    new String[]{"assignmentName"}, // Keys in dataMap
                                    new int[]{R.id.tvFileName}  // TextView in items_list.xml
                            );

                            listView.setAdapter(adapter);
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
