package com.example.arvind.bustracker;



import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Locationupdate extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    // LogCat tag
    public static final String TAG = MainActivity.class.getSimpleName();

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public Location mLastLocation;

    // Google client to interact with Google API
    public GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    public boolean mRequestingLocationUpdates = false;

    public LocationRequest mLocationRequest;


    // Location updates intervals in sec
    public static int UPDATE_INTERVAL = 10000; // 10 sec
    public static int FATEST_INTERVAL = 5000; // 5 sec
    public static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    public TextView lblLocation,vi;
    public Button btnShowLocation, btnStartLocationUpdates;
    Button b;
    AutoCompleteTextView autocomplete1;
    String everything;
    String[] temp,temp1;

    private LocationManager locationManager= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update1);
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }
        //displayLocation();
        //togglePeriodicLocationUpdates();

        autocomplete1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        b=(Button)findViewById(R.id.button5);
        vi=(TextView)findViewById(R.id.textView6);
        String[] via=new String[1005];
        String[] path=new String[1005];
        Intent x=getIntent();
        String re=x.getStringExtra("text");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        StringBuilder buf=new StringBuilder();
        try {
            InputStream json = getAssets().open("Busdata.txt");

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }


            in.close();
        }
        catch(Exception e){

        }
        everything =buf.toString();
        int a=-1;
        String delimiter = "\t";
        temp = everything.split(delimiter);

        //String[] temp=x.getStringArrayExtra("t");
        //Toast.makeText(getApplicationContext(),temp[2],Toast.LENGTH_LONG).show();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].equals(re)) {
                a = i;
            }
        }
        for (int i = 1; i <= 5; i++) {
            if (temp[a + i].matches(".*\\d+.*")) {
                break;
            }
            via[i] = temp[a + i];
        }
        int i = 0;
        path[i++] = via[1];
        delimiter = ",";
        String p = via[1] + "," + via[3] + "," + via[2];
        temp1 = p.split(delimiter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, temp1);
        autocomplete1.setThreshold(1);
        autocomplete1.setAdapter(adapter1);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    public void displayLocation() {


    }

    /**
     * Method to toggle periodic location updates
     * */
    public void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btnStartLocationUpdates
                    .setText(getString(R.string.btn_stop_location_updates));

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btnStartLocationUpdates
                    .setText(getString(R.string.btn_start_location_updates));

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        //displayLocation();
    }

    public Boolean match()
    {
        int a=-1;
        for (int i = 0; i < temp1.length; i++) {
            if (temp1[i].equalsIgnoreCase(autocomplete1.getText().toString())) {
                a = i;
            }
        }
        if(a==-1)
            return true;
        else
            return false;
    }

    public void submit1(View v) {
        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            Toast.makeText(this, "GPS is Disabled in your device. Please enable it to proceed further!", Toast.LENGTH_SHORT).show();
        } else {
            String re1 = autocomplete1.getText().toString();
            if (re1.isEmpty())
                Toast.makeText(getApplicationContext(),
                        "Please choose a stop!", Toast.LENGTH_LONG)
                        .show();
                //Toast.makeText(getApplicationContext(),re,Toast.LENGTH_LONG).show();
                //Bundle b = new Bundle();
                //b.putStringArray("temp", temp);
                //b.putString("text", re);
            else if (match()) {
                Toast.makeText(getApplicationContext(),
                        "Please choose a stop from the list!", Toast.LENGTH_LONG)
                        .show();
            }
             else {
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
                    try {
                        //vi.setText("Hello");
                    mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);

                    if (mLastLocation != null) {
                        double latitude = mLastLocation.getLatitude();
                        double longitude = mLastLocation.getLongitude();
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(this, Locale.getDefault());
                        //vi.setText(latitude+" "+longitude);
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getSubLocality();
                            vi.setText("You are at: " + address + " "+ city +". Please update accordingly.");
                            if (re1.equalsIgnoreCase(city) || re1.toLowerCase().contains(city.toLowerCase()) || address.toLowerCase().contains(re1.toLowerCase()))
                            {
                                shared s1 = new shared();
                                String e = s1.getEmail();
                                String b = s1.getBus();
                                //Toast.makeText(getApplicationContext(), e+b , Toast.LENGTH_SHORT).show();
                                login1(re1, e, b);
                            } else {
                                //vi.setText("You are at: " + address + "," + city+","+state+ ". Please update accordingly.");
                                Toast.makeText(getApplicationContext(), "Can't update.", Toast.LENGTH_LONG).show();
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Services not available.", Toast.LENGTH_LONG).show();
                    }


                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
            case R.id.list: {
                Intent n = new Intent(getApplicationContext(), busview.class);
                startActivity(n);
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
        }
        return super.onOptionsItemSelected(item);
    }
    private void login1(final String username,final String password,final String bus) {
        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(Locationupdate.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                String pass = params[1];
                String b=params[2];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("stop", uname));
                nameValuePairs.add(new BasicNameValuePair("email", pass));
                nameValuePairs.add(new BasicNameValuePair("bus", b));
                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://bhavana.16mb.com/update.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }


            @Override
            protected void onPostExecute(String result){
                String s = result.trim();
                loadingDialog.dismiss();
                if(s.equalsIgnoreCase("Successful")){
                    Toast.makeText(getApplicationContext(), "Successfuly updated. Thanks for your time. See your credits section.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Locationupdate.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Couldn't be updated. Try again!", Toast.LENGTH_LONG).show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username,password,bus);

    }

}
