package com.example.arvind.bustracker;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class busview extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = busview.class.getSimpleName();

    private String URL_TOP_250 = "http://bhavana.16mb.com/imdb_top_250.php?offset=";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ListAdapter adapter;
    private List<Movie> movieList;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.busviexml);
            Typeface tf1 = Typeface.createFromAsset(getAssets(), "Fonts/28 Days Later.ttf");
            listView = (ListView) findViewById(R.id.listView);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
            movieList = new ArrayList<>();
            adapter = new ListAdapter(this, movieList);
            listView.setAdapter(adapter);

            swipeRefreshLayout.setOnRefreshListener(this);
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (!isConnected) {
                Toast.makeText(getApplicationContext(),
                        "Please connect to network!", Toast.LENGTH_LONG)
                        .show();
            } else {

                Toast.makeText(getApplicationContext(),
                        "Swipe down to update the list", Toast.LENGTH_LONG)
                        .show();
                /**
                 * Showing Swipe Refresh animation on activity create
                 * As animation won't start on onCreate, post runnable is used
                 */
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);

                                                fetchMovies();
                                            }
                                        }
                );

            }
        }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchMovies();
    }

    /**
     * Fetching movies json by making http call
     */
    private void fetchMovies() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = URL_TOP_250 + offSet;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject movieObj = response.getJSONObject(i);

                                    int rank = movieObj.getInt("rank");
                                    String title = movieObj.getString("title");

                                    Movie m = new Movie(rank, title);

                                    movieList.add(0, m);

                                    // updating offset value to highest value
                                    if (rank >= offSet)
                                        offSet = rank;

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            //noinspection SimplifiableIfStatement
            case R.id.Home: {
                Intent n = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(n);
                return true;
            }
            case R.id.Logout: {
                Intent n = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(n);
                finish();
                return true;
            }
            case R.id.Update: {
                Intent n = new Intent(getApplicationContext(), update.class);
                startActivity(n);
                return true;
            }
            case R.id.Credit: {
                Intent n = new Intent(getApplicationContext(), credits.class);
                startActivity(n);
                return true;
            }
            case R.id.list: {
                Intent n = new Intent(getApplicationContext(), busview.class);
                startActivity(n);
                return true;
            }
            case R.id.About: {
                Intent n = new Intent(getApplicationContext(), about.class);
                startActivity(n);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    }
