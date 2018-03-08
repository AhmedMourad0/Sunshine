package com.example.ahmed.sunshine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class AutoCompleteCustomArrayAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final int layoutResourceId;

    @SuppressWarnings("unchecked")
    AutoCompleteCustomArrayAdapter(Context mContext, String[] data) {
        super(mContext, R.layout.autocomplete_custom_item, data);

        this.layoutResourceId = R.layout.autocomplete_custom_item;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.textView);
        textViewItem.setText(getItem(position));

        return convertView;
    }

}
