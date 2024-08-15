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

public class LinksFragment extends Fragment {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> linkDescriptions;
    private List<String> linkUrls;
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

        linkDescriptions = new ArrayList<>();
        linkUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, linkDescriptions);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchLinksFromFirestore();

        listView.setOnItemClickListener((adapterView, view1, position, id) -> {
            String url = linkUrls.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return view;
    }

    private void fetchLinksFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("links")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            linkDescriptions.clear(); // Clear the list to avoid duplication
                            linkUrls.clear();        // Clear the URLs list as well

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String linkDescription = document.getString("linkDescription");
                                String linkUrl = document.getString("linkUrl");

                                linkDescriptions.add(linkDescription);
                                linkUrls.add(linkUrl);
                            }
                            adapter.notifyDataSetChanged();
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
