package com.example.governorsindhstudents;

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

public class NewsActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> newsList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        listView = findViewById(R.id.listView_news);
        progressBar = findViewById(R.id.progressBar_news);

        newsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, newsList);
        listView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        fetchNewsFromFirestore();
    }

    private void fetchNewsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("news")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            newsList.clear();

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                String title = document.getString("title");
                                String content = document.getString("content");
                                String day = document.getString("day");

                                // Check if any field is null
                                if (title == null) title = "No Title";
                                if (content == null) content = "No Content";
                                if (day == null) day = "No Day";

                                String displayText = day + ": " + title + "\n" + content;
                                newsList.add(displayText);
                            }

                            adapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(NewsActivity.this, "No news found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Log the error message
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(NewsActivity.this, "Failed to load news: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

}
