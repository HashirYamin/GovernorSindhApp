package com.example.governorsindhstudents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class HomeFragment extends Fragment {

    private CardView lecturesCard, assignmentsCard, linksCard, videosCard;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find CardViews by ID
        lecturesCard = view.findViewById(R.id.cardview_lectures_home);
        assignmentsCard = view.findViewById(R.id.assignments_home);
        linksCard = view.findViewById(R.id.links_home);
        videosCard = view.findViewById(R.id.vedios_home);

//        // Set up listeners
//        setupCardListeners();
//
//        // Check for updates
//        checkForUpdates();
        lecturesCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LecturesActivity.class));
        });
        assignmentsCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AssignmentsActivity.class));
        });
        linksCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LinksActivity.class));
            });
        videosCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), VediosActivity.class));
        });

        return view;
    }
//
//    private void setupCardListeners() {
//        lecturesCard.setOnClickListener(v -> {
//            clearNotificationIcon("lectures");
//            updateLastSeenTime("lectures");
//            startActivity(new Intent(getActivity(), LecturesActivity.class));
//        });
//
//        assignmentsCard.setOnClickListener(v -> {
//            clearNotificationIcon("assignments");
//            updateLastSeenTime("assignments");
//            startActivity(new Intent(getActivity(), AssignmentsActivity.class));
//        });
//
//        linksCard.setOnClickListener(v -> {
//            clearNotificationIcon("links");
//            updateLastSeenTime("links");
//            startActivity(new Intent(getActivity(), LinksActivity.class));
//        });
//
//        videosCard.setOnClickListener(v -> {
//            if(v)
//            videosCard.findViewById(R.id.notify_circle_icon_ved).setVisibility(View.INVISIBLE);
//            updateLastSeenTime("videos");
//            startActivity(new Intent(getActivity(), VediosActivity.class));
//        });
//    }
//
//    private void clearNotificationIcon(String collectionName) {
//        CardView cardView = getCardViewByName(collectionName);
//        if (cardView == null) {
//
//        }
//    }
//
//    private void updateLastSeenTime(String collectionName) {
//        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE).edit();
//        editor.putLong("lastSeenUpdateTime_" + collectionName, new Date().getTime());
//        editor.apply();
//    }
//
//    private void checkForUpdates() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String[] collections = {"lectures", "assignments", "links", "videos"};
//
//        for (String collection : collections) {
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
//            long lastSeenTime = sharedPreferences.getLong("lastSeenUpdateTime_" + collection, 0);
//
//            db.collection(collection)
//                    .whereGreaterThan("timestamp", new Timestamp(new Date(lastSeenTime)))
//                    .addSnapshotListener((snapshots, e) -> {
//                        if (e != null) {
//                            return;
//                        }
//                        if (snapshots != null && !snapshots.isEmpty()) {
//                            updateNotificationUI(collection, snapshots.size());
//                        }
//                    });
//        }
//    }
//
//    private void updateNotificationUI(String collectionName, int updateCount) {
//        View rootView = getView();
//        if (rootView == null) {
//            return;
//        }
//
//        View notificationIcon = null;
//
//        switch (collectionName) {
//            case "lectures":
//                notificationIcon = rootView.findViewById(R.id.notify_circle_icon_lecs);
//                break;
//            case "assignments":
//                notificationIcon = rootView.findViewById(R.id.notify_circle_icon_assign);
//                break;
//            case "links":
//                notificationIcon = rootView.findViewById(R.id.notify_circle_icon_links);
//                break;
//            case "videos":
//                notificationIcon = rootView.findViewById(R.id.notify_circle_icon_ved);
//                break;
//            default:
//                return;
//        }
//
//        if (notificationIcon != null) {
//            notificationIcon.setVisibility(updateCount > 0 ? View.VISIBLE : View.INVISIBLE);
//        }
//    }
//
//    private CardView getCardViewByName(String collectionName) {
//        switch (collectionName) {
//            case "lectures":
//                return lecturesCard;
//            case "assignments":
//                return assignmentsCard;
//            case "links":
//                return linksCard;
//            case "videos":
//                return videosCard;
//            default:
//                return null;
//        }
//    }
}
