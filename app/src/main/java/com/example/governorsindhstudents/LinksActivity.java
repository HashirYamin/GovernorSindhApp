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

public class LinksActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private List<Map<String, String>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_links);

        listView = findViewById(R.id.listView_links);
        progressBar = findViewById(R.id.progressBar_links);
        firestore = FirebaseFirestore.getInstance();
        dataList = new ArrayList<>();

        fetchLinksFromFirestore();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String url = dataList.get(position).get("linkUrl");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }

    private void fetchLinksFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("links")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            dataList.clear();  // Clear the list to avoid duplication

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String linkDescription = document.getString("linkDescription");
                                String linkUrl = document.getString("linkUrl");

                                // Prepare the data
                                Map<String, String> dataMap = new HashMap<>();
                                dataMap.put("fileName", linkDescription); // Using "fileName" key to reuse the layout
                                dataMap.put("linkUrl", linkUrl);
                                dataList.add(dataMap);
                            }

                            // Bind data to the ListView using SimpleAdapter
                            SimpleAdapter adapter = new SimpleAdapter(
                                    LinksActivity.this,
                                    dataList,
                                    R.layout.index_liv, // Reusing the custom layout
                                    new String[]{"fileName"}, // Keys in dataMap
                                    new int[]{R.id.tvFileName} // TextView in items_list.xml
                            );

                            listView.setAdapter(adapter);
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
