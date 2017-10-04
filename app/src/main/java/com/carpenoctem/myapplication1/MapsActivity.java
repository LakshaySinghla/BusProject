package com.carpenoctem.myapplication1;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

    //FusedLocationProviderClient mFusedLocationClient;

    private DatabaseReference mDatabase;
    GPSTracker gps;


    boolean readyMap =false;
    ImageView current;
    EditText current_ed, destination;
    Button write, read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //FirebaseApp.initializeApp(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        read=  (Button) findViewById(R.id.read);
        write = (Button) findViewById(R.id.write);
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDatabase.setValue("Lakshay");
                //writeNewUser("AbC","Lakshay","email");
            }
        });

/*
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                Toast.makeText(MapsActivity.this,"UserName: "+user.username ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Lakshay", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
*/

        current = (ImageView) findViewById(R.id.current_location);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(readyMap){

                        try {
                            //mFusedLocationClient = LocationServices.FusedLocationApi;
                            Location mLastLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                mylocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                Toast.makeText(MapsActivity.this,"Your Location is - \nLat: " + mLastLocation.getLatitude() + "\nLong: " + mLastLocation.getLongitude(),Toast.LENGTH_SHORT).show();

                                mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker in Sydney"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                                Log.v("Lakshay", "Longitude: " + mLastLocation.getLongitude() + "Latitude: " + mLastLocation.getLatitude());

                            } else
                                Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        } catch (SecurityException e) {
                            Toast.makeText(MapsActivity.this, "Turn On the Location to get Current Location", Toast.LENGTH_LONG).show();
                            Log.v("LAKSHAY", "Can't find current location");
                        }

                    /*
                    // create class object
                    gps = new GPSTracker(MapsActivity.this);
                    GetLocation();
                    */
                }
                else{
                    Toast.makeText(MapsActivity.this,"Turn On the Location to get Current Location",Toast.LENGTH_LONG).show();
                }
            }
        });

        //checkPlayServices();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*

        //Location location = mMap.getMyLocation();
        //try {
        //    mMap.setMyLocationEnabled(true);
        //}catch (SecurityException e){
        //    Toast.makeText(this, "Open Location",Toast.LENGTH_SHORT).show();
        //    Log.e("LAK",e.toString());
        //}
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

    */

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
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

                        mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker in Sydney"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

                        Log.i("Lakshay",  "Lat:" + location.getLatitude() + "Long: "+location.getLongitude() );
                        // Update UI with location data
                        // ...
                    }
                };
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);


        }
        catch (SecurityException e){

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("LAKSHAY", connectionResult.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        readyMap = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }

    public void GetLocation(){

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            mylocation = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(mylocation).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
            Log.v("Lakshay","Longitude: "+ longitude + "Latitude: "+latitude);

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
        }else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

}
