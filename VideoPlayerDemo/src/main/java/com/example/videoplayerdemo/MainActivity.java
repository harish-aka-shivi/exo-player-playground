package com.example.videoplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.videoplayerdemo.finalApp.ExoPlayerListActivity;
import com.inmobi.sdk.InMobiSdk;


public class MainActivity extends AppCompatActivity {

    public static final long MY_VIDEO_DEMO_INTERSTIAL_AD_PLACEMENT_ID = 1500689132001l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        Button button = (Button) findViewById(R.id.button);
        button.setText("open videos list");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent
                        (MainActivity.this,ExoPlayerListActivity.class));

            }
        });

        Button button1 = (Button) findViewById(R.id.imageButton);
        button1.setText("Open View Pager");
        button1.setVisibility(View.GONE);

        String inMobiAccountId = "b64a862938c74c81820fb3185b3c35f1";
        InMobiSdk.init(MainActivity.this, inMobiAccountId);
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to t;he action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


