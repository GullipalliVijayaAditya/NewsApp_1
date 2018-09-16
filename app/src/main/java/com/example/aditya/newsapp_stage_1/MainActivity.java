package com.example.aditya.newsapp_stage_1;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsData>>, Spinner.OnItemSelectedListener {

    private static final String FEEDS_URL = "https://content.guardianapis.com/search?api-key=561b5437-2d70-4b45-aefe-31ec0d32776e";
    private static final int NEWSFEED_LOADER_ID = 1;
    @BindView(R.id.category_spinner)
    Spinner categorySpinner;
    String[] images = new String[15];
    String[] category = new String[15];
    String searchCategory, searchString;
    String orderBy;
    @BindView(R.id.news_list)
    RecyclerView newsList;
    @BindView(R.id.status_text_view)
    TextView statusTextView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.network_status_text_view)
    TextView networkStatusTextView;
    @BindView(R.id.retryButton)
    Button retryButton;
    private RecyclerView.Adapter newsAdapter;
    private ArrayList<NewsData> _newsData;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        images = getResources().getStringArray(R.array.category_value);
        category = getResources().getStringArray(R.array.category_lable);
        CustomAdapter adapter = new CustomAdapter(this, category, images);

        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelected(false);
        categorySpinner.setSelection(0, false);
        categorySpinner.setOnItemSelectedListener(this);

        handleIntent(getIntent());
        _newsData = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, _newsData);

        newsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);
        newsList.setAdapter(newsAdapter);

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWSFEED_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            networkStatusTextView.setVisibility(View.VISIBLE);
            statusTextView.setVisibility(View.GONE);
        }

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    private Uri.Builder createQueryByFilters(Uri.Builder builder) {
        builder.appendQueryParameter(getString(R.string.query_string_show_fields), getString(R.string.query_parameter_thumbnail));
        builder.appendQueryParameter(getString(R.string.query_string_show_tags), getString(R.string.query_parameter_contributor));
        builder.appendQueryParameter(getString(R.string.query_string_order_date), orderBy);
        builder.appendQueryParameter(getString(R.string.query_string_page_size), "50");

        return builder;
    }

    @Override
    public Loader<List<NewsData>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        orderBy = sharedPreferences.getString(getString(R.string.setting_order_by_key), getString(R.string.setting_order_by_default));
        Uri baseUri = Uri.parse(FEEDS_URL);
        Uri.Builder builder = baseUri.buildUpon();

        if (searchString != null || searchCategory != null) {

            if (searchCategory != null) {
                builder = createQueryByFilters(builder);
                builder.appendQueryParameter(getString(R.string.query_string_section), searchCategory);
            }
            if (searchString != null) {
                builder = createQueryByFilters(builder);
                builder.appendQueryParameter(getString(R.string.query_string_q), searchString);
            }
        } else {
            builder = createQueryByFilters(builder);
        }

        return new NewsFeedLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsData>> loader, List<NewsData> data) {
        progressBar.setVisibility(View.GONE);
        _newsData.clear();
        newsAdapter.notifyDataSetChanged();

        if (isConnected()) {
            if (data != null && !data.isEmpty()) {
                _newsData.addAll(data);
                newsAdapter.notifyDataSetChanged();
                statusTextView.setVisibility(View.GONE);
            } else {
                statusTextView.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            networkStatusTextView.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsData>> loader) {
        _newsData.clear();
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_setting).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            searchCategory = null;
            getLoaderManager().restartLoader(NEWSFEED_LOADER_ID, null, this);
        } else {
            searchCategory = getResources().getStringArray(R.array.category_search)[position];
            getLoaderManager().restartLoader(NEWSFEED_LOADER_ID, null, this);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            getLoaderManager().restartLoader(NEWSFEED_LOADER_ID, null, this);
        }

    }
}