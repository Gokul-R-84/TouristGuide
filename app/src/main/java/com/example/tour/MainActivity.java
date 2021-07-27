package com.example.tour;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    TextView currentloc, placePicker, search;
    CardView top_picks, gas_station, restaurant, atm;
    String latitude, longitude, type, placeType, locationName;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] PlaceTypes = { "accounting", "airport", "aquarium", "bakery", "bank", "bar",
                "bus station", "cafe", "car rental", "car repair", "casino",
                "church", "hindu temple", "mosque", "hospital", "library",
                "movie theater", "night club", "park", "pharmacy",
                "shopping mall", "spa", "supermarket", "train station", "travel agency" };

        /*String[] PlaceTypes = { "accounting", "airport", "aquarium", "bakery", "bank", "bar",
                "bus_station", "cafe", "car_rental", "car_repair", "casino",
                "church", "hindu_temple", "hospital", "library", "movie_theater",
                "night_club", "park", "pharmacy", "shopping_mall", "spa",
                "supermarket", "train_station", "travel_agency" };*/

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.container), iconFont);


        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        autoCompleteTextView = findViewById(R.id.autoComplete);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, PlaceTypes);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(arrayAdapter);

        /*Intent intent = getIntent();
        latitude = intent.getStringExtra("lat");
        longitude = intent.getStringExtra("lng");
        System.out.println(latitude+",,,,,"+longitude);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude),1);
            currentloc.setText(addresses.get(0).getLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeType = autoCompleteTextView.getText().toString();
                placeType = placeType.replace(" ","_");

                Intent intent = new Intent(MainActivity.this, Search.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("placeType", placeType);
                startActivity(intent);
            }
        });

        top_picks = findViewById(R.id.top_picks);
        top_picks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TopPicks.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

        gas_station = findViewById(R.id.gasStation);
        gas_station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "gas_station";

                Intent intent = new Intent(MainActivity.this, Search.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("placeType", type);
                startActivity(intent);
            }
        });

        restaurant = findViewById(R.id.restaurant);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("-----------restaurant clicked------------\n");
                type = "restaurant";

                Intent intent = new Intent(MainActivity.this, Search.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("placeType", type);
                startActivity(intent);
            }
        });

        atm = findViewById(R.id.atm);
        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("-----------atm clicked------------\n");
                type = "atm";

                Intent intent = new Intent(MainActivity.this, Search.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("placeType", type);
                startActivity(intent);
            }
        });

        currentloc = findViewById(R.id.current_location);

        placePicker = findViewById(R.id.getLocation);
        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Placepicker.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, 2);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCOde = "+requestCode);
        if(requestCode == 2) {
            latitude = data.getStringExtra("lat");
            longitude = data.getStringExtra("lng");
            locationName = data.getStringExtra("loc");
            currentloc.setText(locationName);
            System.out.println(latitude+",,,main activity,,"+longitude);
        }
    }

    private void locationEnabled() {
        System.out.println("\n---------Location enabled--------\n");
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("Wanderlust need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("No, Thanks", null)
                    .show();
        }
    }

    @SuppressLint("MissingPermission")
    void getLocation() {
        System.out.println("\n-----------getLocation---------\n");
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("\n---------onLocation changed---------\n");
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            currentloc.setText(addresses.get(0).getLocality());
            latitude = String.valueOf(addresses.get(0).getLatitude());
            longitude = String.valueOf(addresses.get(0).getLongitude());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}