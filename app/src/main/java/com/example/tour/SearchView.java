package com.example.tour;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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

public class SearchView extends AppCompatActivity {

    ListView listView;

    ImageView img;
    TextView name, ratings, open, viewOnMap;
    RatingBar ratingBar;
    RequestQueue rq;
    String place_id;
    int isReview = 0;
    int photoLength = 0;

    List<String> author_name;
    List<String> profile_photo;
    List<String> rating;
    List<String> reviews;

    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.container), iconFont);

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true); // Arrow Button
            actionBar.setDisplayShowHomeEnabled(true);
        }

        author_name = new ArrayList<String>();
        profile_photo = new ArrayList<String>();
        rating = new ArrayList<String>();
        reviews = new ArrayList<String>();

        listView = findViewById(R.id.reviewsList);

        rq = Volley.newRequestQueue(this);

        img = findViewById(R.id.imageView);
        name = findViewById(R.id.resName);
        ratingBar = findViewById(R.id.ratingBar);
        ratings = findViewById(R.id.resRatings);
        open = findViewById(R.id.openingHours);
        viewOnMap = findViewById(R.id.viewOnMap);

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmer();

        Intent intent = getIntent();

        final String placename = intent.getStringExtra("Name");
        String rating = intent.getStringExtra("Rating");
        String photoRef = intent.getStringExtra("photoRef");
        place_id = intent.getStringExtra("PlaceId");
        System.out.println("\n--------place id = "+place_id);
        String placeopen = intent.getStringExtra("Open");
        final String lat = intent.getStringExtra("lat");
        final String lng = intent.getStringExtra("lng");

        //final String userLat = intent.getStringExtra("userlat");
        //final String userLongi = intent.getStringExtra("userlng");

        name.setText(placename);

        if(rating.equals("No Ratings yet")) {
            ratingBar.setVisibility(View.GONE);
            ratings.setText("No Ratings yet");
        }else{
            ratingBar.setRating(Float.parseFloat(rating));
            ratings.setText(rating);
        }

        if(photoRef.equals("No photo")) {
            img.setImageResource(R.drawable.no_image);
        }else{
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoRef + "&key=AIzaSyDqxDds19QCp0ItfZEI7VQ7WW3nC_TDRAA";
            Picasso.get().load(url).into(img);
        }

        if(placeopen.equals("true")) {
            open.setText("Open");
            open.setTextColor(Color.parseColor("#008000"));
        }else{
            open.setText("Closed");
            open.setTextColor(Color.parseColor("#ff3333"));
        }

        actionBar.setTitle(placename);

        viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchView.this, MapsActivity.class);
                intent.putExtra("name", placename);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);

                //intent.putExtra("userlat", userLat);
                //intent.putExtra("userlng", userLongi);
                startActivity(intent);
            }
        });

        parseJSON();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAdapter();
            }
        }, 2000);
    }

    private void createAdapter() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        final SearchViewAdapter searchViewAdapter = new SearchViewAdapter(this, author_name, profile_photo, rating, reviews);
        listView.setAdapter(searchViewAdapter);
    }

    public void parseJSON() {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id="+place_id+"&key=AIzaSyDqxDds19QCp0ItfZEI7VQ7WW3nC_TDRAA";
        System.out.println(url);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject reviewObject = null;
                            JSONObject resultObject = response.optJSONObject("result");

                            JSONArray reviewsArray = resultObject.optJSONArray("reviews");
                            System.out.println(reviewsArray);
                            if(reviewsArray != null) {

                                for (int i = 0; i < reviewsArray.length(); i++) {

                                    reviewObject = reviewsArray.getJSONObject(i);

                                    String authorname = reviewObject.optString("author_name");
                                    String profile_photo_url = reviewObject.optString("profile_photo_url");
                                    String author_rating = reviewObject.optString("rating");
                                    String review = reviewObject.optString("text");

                                    author_name.add(authorname);

                                    if (profile_photo_url != null) {
                                        profile_photo.add(profile_photo_url);
                                    } else {
                                        profile_photo.add("No Photo");
                                    }

                                    rating.add(author_rating);
                                    reviews.add(review);

                                    photoLength = profile_photo.size();
                                }

                            } else{
                                author_name.add("No author name");
                                profile_photo.add("No photo");
                                rating.add("No ratings yet");
                                reviews.add("No reviews yet");
                            }

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
    }

    class SearchViewAdapter extends ArrayAdapter<String> {
        Context context;
        List<String> author_name;
        List<String> author_photo;
        List<String> author_rating;
        List<String> author_review;

        SearchViewAdapter(@NonNull Context c, List<String> name, List<String> photo, List<String> rating, List<String> review) {
            super(c, R.layout.custom_review_layout, R.id.authorName, name);
            this.context = c;
            this.author_name = name;
            this.author_photo = photo;
            this.author_rating = rating;
            this.author_review = review;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            isReview = 0;

            ImageView profilePhoto;

            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.custom_review_layout, parent, false);

            TextView authorName = view.findViewById(R.id.authorName);
            TextView Rating = view.findViewById(R.id.authorRatings);
            TextView Review = view.findViewById(R.id.authorReview);
            profilePhoto = view.findViewById(R.id.coverImage);
            TextView ViewMoreReviews = findViewById(R.id.viewMoreReviews);

            ViewMoreReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchView.this, ViewReviews.class);
                    intent.putStringArrayListExtra("name", (ArrayList<String>) author_name);
                    intent.putStringArrayListExtra("photo", (ArrayList<String>) author_photo);
                    intent.putStringArrayListExtra("rating", (ArrayList<String>) author_rating);
                    intent.putStringArrayListExtra("review", (ArrayList<String>) author_review);
                    startActivity(intent);
                }
            });

            if(author_name.get(position) != "No author name"){
                authorName.setText(author_name.get(position));
            } else{
                isReview = isReview + 1;
            }

            if(author_rating.get(position) != "No ratings yet") {
                Rating.setText(author_rating.get(position));
            } else{
                isReview = isReview + 1;
            }

            if(position <= photoLength && author_photo.get(position) != "No photo") {
                String url = author_photo.get(position);
                Picasso.get().load(url).into(profilePhoto);
            }else{
                isReview = isReview + 1;
                //profilePhoto.setImageResource(R.drawable.no_image);
            }

            if(author_review.get(position) != "No reviews yet"){
                Review.setText(author_review.get(position));
            } else{
                isReview = isReview + 1;
                //Review.setText("No reviews yet");
            }

            if(isReview == 4){
                listView.setVisibility(View.GONE);
                ViewMoreReviews.setText("No reviews yet");
                ViewMoreReviews.setOnClickListener(null);
            }

            return view;
        }
    }
}