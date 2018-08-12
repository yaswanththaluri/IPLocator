package com.example.yaswanththaluri.iplocator;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class Info extends AppCompatActivity {
    String ip;

    public static final String LOG_TAG = Info.class.getSimpleName();
    public static String REQUESTURL = "http://ip-api.com/json/";
//    SwipeRefreshLayout swipe;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

//        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//        swipe.setOnRefreshListener(Info.this);
//        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        Bundle extras = getIntent().getExtras();
        if(extras != null)
             ip = extras.getString("IPADDRESS");

        LinearLayout L = (LinearLayout)findViewById(R.id.nointernet);
        LinearLayout r = (LinearLayout)findViewById(R.id.mainlayout);


        TextView t =(TextView)findViewById(R.id.enteredip);
        t.setText(ip);

        REQUESTURL = REQUESTURL+ip;


        try
        {
            if(isInternetConnectionAvailable())
        {
            r.setVisibility(View.VISIBLE);
            L.setVisibility(View.INVISIBLE);
//            IpAsyncTask task = new IpAsyncTask();
//            task.execute();

        }
        }
        catch (Exception e)
        {
            L.setVisibility(View.VISIBLE);
            r.setVisibility(View.INVISIBLE);
        }

        Button b = (Button)findViewById(R.id.anotherid);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i= new Intent(Info.this, MainActivity.class);
                startActivity(i);
            }
        });



    }

    public void updateUi(Event ip)
    {
        TextView cityTextView = (TextView) findViewById(R.id.city);
        cityTextView.setText(ip.city);

        TextView regionTextView = (TextView) findViewById(R.id.regionname);
        regionTextView.setText(ip.regionname);

        TextView countryTextView = (TextView) findViewById(R.id.country);
        countryTextView.setText(ip.country);

        TextView latTextView = (TextView) findViewById(R.id.latitude);
        latTextView.setText(ip.lalitude);

        TextView lonTextView = (TextView) findViewById(R.id.longitude);
        lonTextView.setText(ip.longitude);
    }

    private boolean isInternetConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnected();
    }

    private class IpAsyncTask extends AsyncTask<URL, Void, Event>
    {

        @Override
        protected Event doInBackground(URL... urls) {
            URL url = createUrl(REQUESTURL);

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            Event ip =extractFeatureFromJson(jsonResponse);
            return ip;
        }

        @Override
        protected void onPostExecute(Event ip) {
            if (ip == null) {
                return;
            }
            updateUi(ip);
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

        private String makeHttpRequest(URL url) throws IOException
        {
            String jsonResponse = "";

            if(url==null)
            {
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


                if(urlConnection.getResponseCode() == 200)
                {
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

        private Event extractFeatureFromJson(String ipJSON)
        {
            if(TextUtils.isEmpty(ipJSON))
            {
                return null;
            }

            try
            {
                JSONObject baseJsonResponse = new JSONObject(ipJSON);
                String city = baseJsonResponse.getString("city");
                String region = baseJsonResponse.getString("regionName");
                String country = baseJsonResponse.getString("country");
                String latitude = baseJsonResponse.getString("lat");
                String longitude = baseJsonResponse.getString("lon");

                return new Event(city, region, country, latitude, longitude);
            }
             catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
