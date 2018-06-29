package com.carpenoctem.myapplication1;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng mylocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    Marker marker;
    int index, REQUEST_CHECK_SETTINGS = 200;
    String number;
    private DatabaseReference mDatabase;
    Button stop;

    boolean readyMap =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        index = getIntent().getIntExtra("index",0);
        number = getIntent().getStringExtra("number");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(number).child("bus"+index).child("check").setValue("true");
                mDatabase.child(number).child("bus"+index).child("lat").setValue("");
                mDatabase.child(number).child("bus"+index).child("long").setValue("");
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize location requests here.
                getCurrentLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CHECK_SETTINGS ){
            if(resultCode == -1){
                //Success, start tracking user
                getCurrentLocation();
            }
            else{
                //Failure, Stop app
                mDatabase.child(number).child("bus" + index).child("check").setValue("true");
                finish();
            }
        }
    }

    private void getCurrentLocation(){
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.v("Lakshay","Previous lat long obtained");
                            }
                        }
                    });

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i("Lakshay",  "Changed" );
                    for (Location location : locationResult.getLocations()) {
                        Toast.makeText(MapsActivity.this, "Lat:" + location.getLatitude() + "Long: "+location.getLongitude(),Toast.LENGTH_SHORT).show();

                        mylocation = new LatLng(location.getLatitude(), location.getLongitude());

                        if(marker == null){
                            mMap.moveCamera(CameraUpdateFactory.zoomBy(14f));
                            marker = mMap.addMarker(new MarkerOptions().position(mylocation));
                        }
                        marker.setPosition(mylocation);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

                        mDatabase.child(number).child("bus"+index).child("lat").setValue(location.getLatitude()+"");
                        mDatabase.child(number).child("bus"+index).child("long").setValue(location.getLongitude()+"");

                        Log.i("Lakshay",  "Lat:" + location.getLatitude() + "Long: "+location.getLongitude() );
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
            stop.setVisibility(View.VISIBLE);

        }
        catch (SecurityException e){

        }
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "Hello",Toast.LENGTH_SHORT).show();
        mDatabase.child(number).child("bus"+index).child("check").setValue("true");
        mDatabase.child(number).child("bus"+index).child("lat").setValue("");
        mDatabase.child(number).child("bus"+index).child("long").setValue("");
        if(mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("LAK Connection Failed", connectionResult.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        readyMap = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if ( googleApiAvailability.isUserResolvableError(resultCode) ) {
                googleApiAvailability.getErrorDialog(this,resultCode, 404).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }



}
