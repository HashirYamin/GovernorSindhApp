package com.example.governorsindhstudents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactUsFragment extends Fragment {

    CardView web, fb, insta, twit, linkedIn;
    public ContactUsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

            web = view.findViewById(R.id.web_link);
            fb = view.findViewById(R.id.fb);
            insta = view.findViewById(R.id.insta);
            twit = view.findViewById(R.id.twitter);
            linkedIn = view.findViewById(R.id.linked_in);

            web.setOnClickListener(v -> {
                gotoUrl("https://www.governorsindh.com/");
            });
            fb.setOnClickListener(v -> {
                gotoUrl("https://www.facebook.com/groups/1287986351844517/?ref=share&mibextid=KtfwRi");
            });
            insta.setOnClickListener(v -> {
                gotoUrl("https://www.instagram.com/kamrantessoripk/?hl=en");
            });
            twit.setOnClickListener(v -> {
                gotoUrl("https://twitter.com/kamrantessoripk?lang=en");
            });
            linkedIn.setOnClickListener(v -> {
                gotoUrl("https://www.linkedin.com/company/governor-sindh-initiative/");
            });
        return view;
    }

    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}