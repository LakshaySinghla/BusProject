package com.carpenoctem.myapplication1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;
import android.Manifest;

/**
 * Created by Lakshay Singhla on 27-Oct-17.
 */

public class BusNumberActivity extends AppCompatActivity implements View.OnClickListener {
    String kishan;
    TextView bus1, bus2, bus3, bus4, bus5, bus6, bus7;
    String number;
    Intent i ;
    final int LocationRequestCode = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_number);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationRequestCode);
        } else {
            // Permission has already been granted
            proceedAfterPermission();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationRequestCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    proceedAfterPermission();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void proceedAfterPermission(){

        i = new Intent(this, SplashScreen.class);
        findViewById(R.id.bus1).setOnClickListener(this);
        findViewById(R.id.bus2).setOnClickListener(this);
        findViewById(R.id.bus3).setOnClickListener(this);
        findViewById(R.id.bus4).setOnClickListener(this);
        findViewById(R.id.bus5).setOnClickListener(this);
        findViewById(R.id.bus6).setOnClickListener(this);
        findViewById(R.id.bus7).setOnClickListener(this);
        findViewById(R.id.bus8).setOnClickListener(this);
//        bus1 = (TextView) findViewById(R.id.text1);
//        bus2 = (TextView) findViewById(R.id.text2);

//        bus1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                number = "879A from Shahbad Dairy to Janakpuri";
//                i.putExtra("number",number);
//                startActivity(i);
//                finish();
//            }
//        });
//
//        bus2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                number = "879A from Janakpuri to Shahbad Dairy";
//                i.putExtra("number",number);
//                startActivity(i);
//                finish();
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bus1:
                number = ((TextView)view).getText().toString();
            case R.id.bus2:
                number = ((TextView)view).getText().toString();
            case R.id.bus3:
                number = ((TextView)view).getText().toString();
            case R.id.bus4:
                number = ((TextView)view).getText().toString();
            case R.id.bus5:
                number = ((TextView)view).getText().toString();
            case R.id.bus6:
                number = ((TextView)view).getText().toString();
            case R.id.bus7:
                number = ((TextView)view).getText().toString();
            case R.id.bus8:
                number = ((TextView)view).getText().toString();
                i.putExtra("number",number);
                startActivity(i);
                finish();
                break;
        }
    }
}
