package com.example.aditya.newsapp_stage_1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsFeedLoader extends AsyncTaskLoader<List<NewsData>> {

    private String url;

    public NewsFeedLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<NewsData> loadInBackground() {
        if (url == null) {
            return null;
        }

        List<NewsData> result = QueryUtils.fetchEarthquakeData(url);
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}

