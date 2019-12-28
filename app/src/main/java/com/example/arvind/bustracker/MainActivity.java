package com.example.arvind.bustracker;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    TextView textView,t1,t2;
    ImageButton b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textView);
        t1=(TextView)findViewById(R.id.textView2);
        t2=(TextView)findViewById(R.id.textView3);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "Fonts/GatsbyFLF-Bold.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "Fonts/Garineldo.otf");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String[] fin=currentDateTimeString.split(" ");
        String[] date=fin[3].split(":");
        String t=" : "+date[1]+" "+(fin[4].toUpperCase());
        Calendar c= Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        String d="";
        switch (day) {
            case Calendar.SUNDAY:
                d="Sun";
            case Calendar.MONDAY:
                d="Mon";
            case Calendar.TUESDAY:
                d="Tue";
            case Calendar.WEDNESDAY:
                d="Wed";
            case Calendar.THURSDAY:
                d="Thu";
            case Calendar.FRIDAY:
                d="Fri";
            case Calendar.SATURDAY:
                d="Sat";
        }
        d=" "+d+","+" "+fin[1]+" "+fin[0];
        textView.setText(date[0]);
        t1.setText(t);
        t2.setText(d);
        textView.setTypeface(tf1);
        t1.setTypeface(tf1);
        t2.setTypeface(tf2);
        b1=(ImageButton)findViewById(R.id.button1);
    }

    public void onclick1(View v)
    {
        Intent n = new Intent(getApplicationContext(),update.class);
        startActivity(n);
        finish();
    }

    public void onclick4(View v)
    {
        Intent n = new Intent(getApplicationContext(),LoginActivity.class);
        finish();
        startActivity(n);
    }

    public void onclick2(View v)
    {
        Intent n = new Intent(getApplicationContext(),busview.class);
        startActivity(n);
    }

    public void onclick3(View v)
    {
        Intent n = new Intent(getApplicationContext(),credits.class);
        startActivity(n);
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
            case R.id.About: {
                Intent n = new Intent(getApplicationContext(), about.class);
                startActivity(n);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
