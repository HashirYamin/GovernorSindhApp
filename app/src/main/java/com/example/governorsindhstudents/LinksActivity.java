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

public class LinksActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> linkDescriptions;
    private List<String> linkUrls;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        listView = findViewById(R.id.listView_links);
        progressBar = findViewById(R.id.progressBar_links);

        linkDescriptions = new ArrayList<>();
        linkUrls = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, linkDescriptions);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchLinksFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = linkUrls.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
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
                            Toast.makeText(LinksActivity.this, "No links found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LinksActivity.this, "Failed to load links.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

}
