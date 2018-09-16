package com.example.aditya.newsapp_stage_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    Context context;
    String[] category;
    String[] images;
    LayoutInflater inflater;

    public CustomAdapter(Context context, String[] category, String[] images) {
        this.context = context;
        this.category = category;
        this.images = images;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return category.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.custom_spinner_layout, null);
        ImageView imageView = convertView.findViewById(R.id.custom_spinner_image_view);
        TextView textView = convertView.findViewById(R.id.custom_spinner_text_view);

        if (position == 0) {
            imageView.setVisibility(View.GONE);
            textView.setClickable(false);
            textView.setTextIsSelectable(false);
        }
        String path = context.getString(R.string.spinner_images_path_identifier) + images[position];
        imageView.setImageResource(context.getResources().getIdentifier(path, null, context.getPackageName()));
        textView.setText(category[position]);
        return convertView;
    }
}