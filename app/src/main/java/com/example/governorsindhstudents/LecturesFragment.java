package com.example.governorsindhstudents;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LecturesFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> fileNames;
    private List<String> fileUrls;
    private FirebaseFirestore firestore;

    public LecturesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lectures, container, false);

        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);

        fileNames = new ArrayList<>();
        fileUrls = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        fetchFilesFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = fileUrls.get(position);
            String fileName = fileNames.get(position);
            downloadFile(url, fileName);
        });

        return view;
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

                                Log.d("LecturesFragment", "File Name: " + fileName);
                                Log.d("LecturesFragment", "File URL: " + fileUrl);

                                fileNames.add(fileName);
                                fileUrls.add(fileUrl);
                            }
                            populateListView();
                        } else {
                            Toast.makeText(getContext(), "No files found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load files.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void populateListView() {
        listView.setAdapter(new ArrayAdapter<String>(requireContext(), R.layout.items_list, R.id.tvFileName, fileNames) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tvFileName = view.findViewById(R.id.tvFileName);
                tvFileName.setText(fileNames.get(position));
                return view;
            }
        });
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(fileName);
        request.setDescription("Downloading file...");
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