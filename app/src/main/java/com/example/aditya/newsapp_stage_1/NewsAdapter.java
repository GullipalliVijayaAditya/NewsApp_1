package com.example.aditya.newsapp_stage_1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsData> newsData;
    private Context context;

    public NewsAdapter(Context context, ArrayList<NewsData> newsData) {
        this.context = context;
        this.newsData = newsData;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        NewsData data = newsData.get(position);
        holder.feedTitle.setText(data.getTitle());
        holder.feedAuthor.setText(data.getAuthor());
        holder.sectionName.setText(data.getSectionName());
        Glide.with(context).load(data.getFeedImage()).into(holder.thumbnail);
        String[] dateTime = data.getDateOfPublish().split(context.getString(R.string.date_spilter));
        holder.feedDate.setText(dateTime[0]);
    }

    @Override
    public int getItemCount() {
        return newsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.feed_title)
        TextView feedTitle;
        @BindView(R.id.feed_author)
        TextView feedAuthor;
        @BindView(R.id.feed_date)
        TextView feedDate;
        @BindView(R.id.feed_image)
        ImageView thumbnail;
        @BindView(R.id.feed_section_name)
        TextView sectionName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Uri webpage = Uri.parse(newsData.get(getAdapterPosition()).getWebUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            }
        }
    }
}