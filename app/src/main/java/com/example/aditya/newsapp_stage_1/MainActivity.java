package com.example.aditya.newsapp_stage_1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>> {
    private static final int NEWSFEED_LOADER_ID = 1;
    private static final String FEEDS_URL = "https://content.guardianapis.com/search?show-fields=thumbnail&show-tags=contributor&api-key=561b5437-2d70-4b45-aefe-31ec0d32776e";
    public RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.news_list)
    RecyclerView newsList;
    @BindView(R.id.status_text_view)
    TextView statusTextView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.network_status_text_view)
    TextView networkStatusTextView;
    private RecyclerView.Adapter newsAdapter;
    private ArrayList<NewsData> _newsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        _newsData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, _newsData);

        newsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);
        newsList.setAdapter(newsAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWSFEED_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            networkStatusTextView.setVisibility(View.VISIBLE);
            networkStatusTextView.setText(R.string.network_status);
        }
    }

    @Override
    public Loader<List<NewsData>> onCreateLoader(int id, Bundle args) {
        return new NewsFeedLoader(this, FEEDS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsData>> loader, List<NewsData> data) {
        progressBar.setVisibility(View.GONE);
        _newsData.clear();
        newsAdapter.notifyDataSetChanged();

        if (data != null && !data.isEmpty()) {
            _newsData.addAll(data);
            newsAdapter.notifyDataSetChanged();
            statusTextView.setVisibility(View.GONE);
        } else {
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(R.string.status_text);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsData>> loader) {
        _newsData.clear();
        newsAdapter.notifyDataSetChanged();
    }
}