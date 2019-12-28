package com.example.arvind.bustracker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class credits extends ActionBarActivity{

        TextView t2;
        String name;
        ProgressBar q;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
        t2=(TextView)findViewById(R.id.textView8);
        q=(ProgressBar)findViewById(R.id.progressBar);
        shared s=new shared();
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "Fonts/Garineldo.otf");
        name = s.getEmail();
        t2.setTypeface(tf2);
        login1(name);
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


    private void login1(final String username) {
        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(credits.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", name));
                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://bhavana.16mb.com/credit.php");
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
            protected void onPostExecute(String result) {
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
                    String s = result.trim();
                    loadingDialog.dismiss();
                    if(s.equalsIgnoreCase("Nothing"))
                    {
                        String qw = name + "\n" + "Credits Earned: 2" + "\n";
                        t2.setText(qw);
                        q.setProgress(0);
                    }
                    else
                    {
                    String[] p = s.split(":");
                    Toast.makeText(getApplicationContext(), "Overall Updates refers to overall contribution made by everyone in the state", Toast.LENGTH_LONG).show();
                    String qw = name + "\n" + "Credits Earned: " + p[0] + "\n" + "Overall updates: " + p[1];
                    t2.setText(qw);
                    Integer n1 = Integer.parseInt(p[0]);
                    Integer n2 = Integer.parseInt(p[1]);
                    float h = n1 / n2;
                    double roundOff = Math.round(h * 100.0) / 100.0;
                    q.setProgress((int) (roundOff * 100));
                }}
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username);

    }

}
