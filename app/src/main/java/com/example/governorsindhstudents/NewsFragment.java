package com.example.governorsindhstudents;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NewsFragment extends Fragment {

    CardView Circular;
    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        Circular = view.findViewById(R.id.circular_home);

        Circular.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewsActivity.class);
            startActivity(intent);
        });
        return view;
    }
}