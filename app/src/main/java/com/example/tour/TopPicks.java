package com.example.tour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopPicks extends AppCompatActivity {

    ListView listView;
    int photoLength = 0, count = 3;

    RequestQueue rq;
    String userLat, userLongi, type;

    List<String> resName;
    List<String> resRating;
    List<String> placeid;
    List<String> photoReference;
    List<String> openNow;
    List<String> Latitude;
    List<String> Longitude;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_picks);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Top picks");

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true); // Arrow Button
            actionBar.setDisplayShowHomeEnabled(true);
        }

        userLat = getIntent().getStringExtra("latitude");
        userLongi = getIntent().getStringExtra("longitude");

        resName = new ArrayList<String>();
        resRating = new ArrayList<String>();
        placeid = new ArrayList<String>();
        photoReference = new ArrayList<String>();
        openNow = new ArrayList<String>();
        Latitude = new ArrayList<String>();
        Longitude = new ArrayList<String>();

        listView = findViewById(R.id.searchListView);

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmer();

        rq = Volley.newRequestQueue(this);

        parseJSON();

        // Delay Create Adapter
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAdapter();
            }
        }, 3000);
    }

    private void createAdapter() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        final TopPicksAdapter topPicksAdapter = new TopPicksAdapter(this, resName, resRating, placeid, photoReference, openNow, Latitude, Longitude);
        listView.setAdapter(topPicksAdapter);
    }

    public void parseJSON() {
        while(count!=0) {

            if(count == 3) {
                type = "tourist_attraction";
            } else if(count == 2) {
                type = "zoo";
            } else {
                type = "museum";
            }

            System.out.println("\n--------type = "+type);

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + userLat + "," + userLongi + "&rankby=distance&type=" + type + "&key=AIzaSyDqxDds19QCp0ItfZEI7VQ7WW3nC_TDRAA";
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject photoObject = null;
                                JSONArray resultArray = response.getJSONArray("results");

                                for (int i = 0; i < resultArray.length(); i++) {

                                    JSONObject jsonObject = resultArray.getJSONObject(i);
                                    JSONObject location = resultArray.optJSONObject(i).optJSONObject("geometry").optJSONObject("location");
                                    JSONObject opening_hours = resultArray.getJSONObject(i).optJSONObject("opening_hours");

                                    Double lat = location.optDouble("lat");
                                    Double lng = location.optDouble("lng");

                                    String name = jsonObject.optString("name");
                                    Double rating = jsonObject.optDouble("rating");
                                    String place_id = jsonObject.optString("place_id");

                                    placeid.add(place_id);
                                    Latitude.add(String.valueOf(lat));
                                    Longitude.add(String.valueOf(lng));

                                    if (opening_hours != null) {
                                        String open_now = opening_hours.optString("open_now");
                                        openNow.add(open_now);
                                    } else {
                                        openNow.add("No opening hours");
                                    }

                                    JSONArray photoArray = jsonObject.optJSONArray("photos");

                                    if (photoArray != null) {
                                        photoObject = photoArray.getJSONObject(0);

                                        String photoreference = photoObject.optString("photo_reference");

                                        photoReference.add(photoreference);
                                    } else {
                                        photoReference.add("No photo");
                                    }

                                    resName.add(name);
                                    String n = String.valueOf(rating);
                                    if (n.equals("NaN")) {
                                        resRating.add("No Ratings yet");
                                    } else {
                                        resRating.add(String.valueOf(rating));
                                    }
                                }
                                System.out.println("place id = " + placeid);
                                photoLength = photoReference.size();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            rq.add(request);
            count = count - 1;
        }
    }

    class TopPicksAdapter extends ArrayAdapter<String>{
        Context context;
        List<String> resname;
        List<String> resrating;
        List<String> placeid;
        List<String> photoRef;
        List<String> resOpenNow;
        List<String> lat;
        List<String> lng;

        TopPicksAdapter(@NonNull Context c, List<String> name, List<String> rating, List<String> placeid, List<String> photoReference, List<String> openNow, List<String> lat, List<String> lng) {
            super(c, R.layout.custom_list_layout, R.id.restaurantTitle, name);
            this.context = c;
            this.resname = name;
            this.resrating = rating;
            this.placeid = placeid;
            this.photoRef = photoReference;
            this.resOpenNow = openNow;
            this.lat = lat;
            this.lng = lng;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ImageView resPhoto;

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.custom_list_layout, parent, false);

            TextView resTitle = view.findViewById(R.id.restaurantTitle);
            TextView resRatings = view.findViewById(R.id.ratings);
            TextView openNow = view.findViewById(R.id.openNow);
            resPhoto = view.findViewById(R.id.coverImage);

            if(position <= photoLength && photoRef.get(position) != "No photo") {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef.get(position) + "&key=AIzaSyDqxDds19QCp0ItfZEI7VQ7WW3nC_TDRAA";
                Picasso.get().load(url).into(resPhoto);
            }else{
                resPhoto.setImageResource(R.drawable.no_image);
            }

            if(resOpenNow.get(position) != "No opening hours"){
                openNow.setText("Open");
                openNow.setTextColor(Color.parseColor("#008000"));
            }else{
                openNow.setText("Closed");
                openNow.setTextColor(Color.parseColor("#ff3333"));
            }

            resTitle.setText(resname.get(position));
            resRatings.setText(resrating.get(position));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), SearchView.class);
                    intent.putExtra("photoRef", photoRef.get(i));
                    intent.putExtra("Name", resname.get(i));
                    intent.putExtra("Rating", resrating.get(i));
                    intent.putExtra("PlaceId", placeid.get(i));
                    intent.putExtra("Open", resOpenNow.get(i));
                    intent.putExtra("lat", lat.get(i));
                    intent.putExtra("lng", lng.get(i));
                    //intent.putExtra("userlat", userLat);
                    //intent.putExtra("userlng", userLongi);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}