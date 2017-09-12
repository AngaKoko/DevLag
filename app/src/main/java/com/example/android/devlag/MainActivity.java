package com.example.android.devlag;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Profile>> {

    /** URL for earthquake data from the GITHUB dataset */
    private static final String GITHUB_REQUEST_URL = "https://api.github.com/search/users";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public static final int PROFILE_LOADER_ID = 0;

    //Instance of the ProfileAdapter class
    private ProfileAdapter mAdapter;

    /** Call the TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Call the Loader View */
    private ProgressBar mProgressBAr;

    //Create a static variable to store the searched string
    private static String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView profileListView = (ListView) findViewById(R.id.list_view);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_state_text_view);
        profileListView.setEmptyView(mEmptyStateTextView);

        //Find reference to the progress bar
        mProgressBAr = (ProgressBar) findViewById(R.id.progress_bar);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(PROFILE_LOADER_ID, null, this);
        }else{
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mProgressBAr.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

        mAdapter = new ProfileAdapter(this, new ArrayList<Profile>());

        profileListView.setAdapter(mAdapter);

        //Find Reference to the search view
        SearchView searchView = (SearchView)findViewById(R.id.developer_search_view);
        //setOnQueryTextListener or the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                userId = query;

                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();

                //Restart our loader manager Already disposed: Module: 'app'
                getLoaderManager().restartLoader(PROFILE_LOADER_ID, null, MainActivity.this);
                return false;
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Profile currentProfile = mAdapter.getItem(position);

                // Create a new intent to view the earthquake URI
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

                /**
                 * Put the profile ID, Url and ImageLink to be received by the intent
                 */
                intent.putExtra("USER_ID", currentProfile.getUserName());
                intent.putExtra("URL", currentProfile.getUrl());
                intent.putExtra("IMAGE_LINK", currentProfile.getImageLink());

                // Send the intent to launch a new activity
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<ArrayList<Profile>> onCreateLoader(int id, Bundle args) {

        //Make the progressbar Visible when thread Starts
        mProgressBAr.setVisibility(View.VISIBLE);

        Uri baseUri = Uri.parse(GITHUB_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", userId);
        String location = "+location:lagos";

        return new ProfileLoader(this, uriBuilder.toString()+location);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Profile>> loader, ArrayList<Profile> data) {

        //Clear previous data from adapter
        mAdapter.clear();

        //Make the visibility of the progress bar gone
        mProgressBAr.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_developer_found);

        // If there is a valid list, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Profile>> loader) {

        //Clear data in the adapter
        mAdapter.clear();

    }
}
