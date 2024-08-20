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

public class LinksFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private List<Map<String, String>> dataList;
    private FirebaseFirestore firestore;

    public LinksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_links, container, false);

        listView = view.findViewById(R.id.listView_links);
        progressBar = view.findViewById(R.id.progressBar_links);
        dataList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        fetchLinksFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = dataList.get(position).get("linkUrl");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return view;
    }

    private void fetchLinksFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("links")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            dataList.clear(); // Clear the list to avoid duplication

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String linkDescription = document.getString("linkDescription");
                                String linkUrl = document.getString("linkUrl");

                                // Prepare the data
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("fileName", linkDescription); // Reusing fileName key for linkDescription
                                dataMap.put("linkUrl", linkUrl);
                                dataList.add(dataMap);
                            }

                            // Bind data to the ListView using SimpleAdapter
                            SimpleAdapter adapter = new SimpleAdapter(
                                    getContext(),
                                    dataList,
                                    R.layout.items_list, // Custom layout for list items
                                    new String[]{"fileName"}, // Keys in dataMap
                                    new int[]{R.id.tvFileName} // TextView in items_list.xml
                            );

                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "No links found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load links.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
