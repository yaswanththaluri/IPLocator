package com.example.yaswanththaluri.iplocator;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class Info extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Event>, SwipeRefreshLayout.OnRefreshListener {
    public static final String LOG_TAG = Info.class.getSimpleName();
    public static String REQUESTURL = "http://ip-api.com/json/";
    private static int LOADER_ID = 0;
    public String lat, lon;
    String ip;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(Info.this);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            ip = extras.getString("IPADDRESS");


        TextView t = (TextView) findViewById(R.id.enteredip);
        t.setText(ip);

        REQUESTURL = REQUESTURL + ip;


        Button b = (Button) findViewById(R.id.anotherid);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(Info.this, MainActivity.class);
                startActivity(i);
            }
        });

        Button b2 = (Button) findViewById(R.id.map);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maps();
            }
        });

        if (isNetworkAvailable()) {
            getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
        } else {
            LinearLayout l = (LinearLayout) findViewById(R.id.main);
            l.setVisibility(View.INVISIBLE);
            LinearLayout l2 = (LinearLayout) findViewById(R.id.network);
            l2.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void maps() {
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void updateUi(Event ip) {

        Log.i("status", ip.status);


        lat = ip.lalitude;
        lon = ip.longitude;
        TextView cityTextView = (TextView) findViewById(R.id.city);
        cityTextView.setText(ip.city);

        TextView regionTextView = (TextView) findViewById(R.id.regionname);
        regionTextView.setText(ip.regionname);

        TextView orgTextView = (TextView) findViewById(R.id.org);
        orgTextView.setText(ip.organisation);

        TextView countryTextView = (TextView) findViewById(R.id.country);
        countryTextView.setText(ip.country);

        TextView latTextView = (TextView) findViewById(R.id.latitude);
        latTextView.setText(ip.lalitude);

        TextView lonTextView = (TextView) findViewById(R.id.longitude);
        lonTextView.setText(ip.longitude);

    }

    @Override
    public Loader<Event> onCreateLoader(int id, Bundle args) {

        return new IpLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Event> loader, Event data) {
        swipe.setRefreshing(false);
        if (data == null) {
            LinearLayout l = (LinearLayout) findViewById(R.id.mainlayout);
            l.setVisibility(View.INVISIBLE);
            TextView t = (TextView) findViewById(R.id.privateid);
            t.setVisibility(View.VISIBLE);
            Button b2 = (Button) findViewById(R.id.map);
            b2.setEnabled(false);
            return;
        }
        updateUi(data);
    }

    @Override
    public void onLoaderReset(Loader<Event> loader) {

    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public static class IpLoader extends AsyncTaskLoader<Event> {
        private IpLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
        }


        @Override
        public Event loadInBackground() {
            String jsonResponse = "";
            Event ip = null;
            URL url = createUrl(REQUESTURL);
            REQUESTURL = "http://ip-api.com/json/";

            try {

                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ip = extractFeatureFromJson(jsonResponse);
            return ip;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();


                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        private Event extractFeatureFromJson(String ipJSON) {
            if (TextUtils.isEmpty(ipJSON)) {
                return null;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(ipJSON);
                String city = baseJsonResponse.getString("city");
                String region = baseJsonResponse.getString("regionName");
                String country = baseJsonResponse.getString("country");
                String latitude = baseJsonResponse.getString("lat");
                String longitude = baseJsonResponse.getString("lon");
                String org = baseJsonResponse.getString("org");
                String status = baseJsonResponse.getString("status");


                return new Event(city, region, country, latitude, longitude, org, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }


}
