package com.example.arvind.bustracker;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import java.io.*;


public class update extends ActionBarActivity {

    Button button;
    AutoCompleteTextView autocomplete;
    String[] temp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        autocomplete =(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        button=(Button)findViewById(R.id.button2);
        String everything="";
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Fonts/justbeautifulsimplicity.ttf");
        button.setTypeface(tf1);
        String[] bus=new String [1005];

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
        for (int i = 0; i < temp.length; i++)
        {
            boolean b =temp[i].matches(".*\\d+.*");
            if(b)
            {
                bus[++a]=temp[i++];
            }
        }
        delimiter = ",";
        String s = "";
        for(int i =0; i < a ; i++)
            s= s +","+ bus[i];
        s=s+","+"S65";
        String[] bu=s.split(delimiter);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,bu);
        autocomplete.setThreshold(1);
        autocomplete.setAdapter(adapter);
    }
    public Boolean match()
    {
        int a=-1;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].equalsIgnoreCase(autocomplete.getText().toString())) {
                a = i;
            }
        }
        if(!(autocomplete.getText().toString().matches(".*\\d+.*")))
        {
            a=-1;
        }
        if(a==-1)
        return true;
        else
            return false;
    }

    public void submit(View v)
    {
        String re = autocomplete.getText().toString();
        shared a = new shared();
        a.setBus(re);
        if (re.isEmpty())
            Toast.makeText(getApplicationContext(),
                    "Please choose a bus!", Toast.LENGTH_LONG)
                    .show();
            //Toast.makeText(getApplicationContext(),re,Toast.LENGTH_LONG).show();
            //Bundle b = new Bundle();
            //b.putStringArray("temp", temp);
            //b.putString("text", re);
        else if(match())
        {
            Toast.makeText(getApplicationContext(),
                    "Please choose a bus from the list!", Toast.LENGTH_LONG)
                    .show();
        }
        else {
            Intent n = new Intent(getApplicationContext(), Locationupdate.class);
            n.putExtra("text", re);
            n.putExtra("t",temp);
            startActivity(n);
        }
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