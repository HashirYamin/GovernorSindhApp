package com.example.governorsindhstudents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UpdatesAdapter extends ArrayAdapter<UpdateItem> {
    public UpdatesAdapter(Context context, List<UpdateItem> updates) {
        super(context, 0, updates);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UpdateItem updateItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_update, parent, false);
        }

        TextView updateType = convertView.findViewById(R.id.updateType);
        TextView updateTimestamp = convertView.findViewById(R.id.updateTimestamp);
        TextView newTextView = convertView.findViewById(R.id.new_text);

        // Set the update type text
        updateType.setText(updateItem.getType());

        // Set the timestamp text
        updateTimestamp.setText(updateItem.getTimestamp());

        // If the update is new, display "New"
        if (updateItem.isNew()) {
            newTextView.setText("New");
        } else {
            newTextView.setText(""); // Clear the text if not new
        }

        return convertView;
    }


}
