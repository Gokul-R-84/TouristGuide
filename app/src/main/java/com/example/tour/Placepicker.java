package com.example.tour;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class Placepicker extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //MarkerOptions source, destination;
    String latitude, longitude, locationName;
    Button btn;
    Double lat, lng, userLat, userLongi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        lat = Double.valueOf(intent.getStringExtra("latitude"));
        lng = Double.valueOf(intent.getStringExtra("longitude"));
        System.out.println(lat+",....using intent...."+lng);

        btn = findViewById(R.id.set);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(latitude+".,.,.,.,.,onClick.,,,,.,.,."+longitude);
                Intent intent = new Intent();
                intent.putExtra("lat", latitude);
                intent.putExtra("lng", longitude);
                intent.putExtra("loc", locationName);
                System.out.println("----setting Result----");
                setResult(2, intent);
                finish();
            }
        });
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
        float zoomLevel = 16.0f;
        LatLng latLng = new LatLng(lat, lng);
        final MarkerOptions markerOptions = new MarkerOptions();
        mMap.addMarker(markerOptions.position(latLng));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
        );

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                LatLng latLng1 = mMap.getCameraPosition().target;
                mMap.clear();
                mMap.addMarker(markerOptions.position(latLng1));
                latitude = String.valueOf(latLng1.latitude);
                longitude = String.valueOf(latLng1.longitude);
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude),1);
                    locationName = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(latitude+".,.,.,.,.,.,...,,,,.,.,."+longitude);
            }
        });
    }

}