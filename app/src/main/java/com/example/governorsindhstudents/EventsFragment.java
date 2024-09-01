package com.example.governorsindhstudents;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EventsFragment extends Fragment {

    CardView Events;
    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        Events = view.findViewById(R.id.cardview_events_home);

        Events.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventsActivity.class);
            startActivity(intent);
        });
        return view;
    }
}