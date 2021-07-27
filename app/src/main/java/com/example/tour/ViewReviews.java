package com.example.tour;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewReviews extends AppCompatActivity {

    ListView listView;
    ArrayList<String> name, photo, rating, review;
    ShimmerFrameLayout shimmerFrameLayout;

    int isReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Reviews");

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true); // Arrow Button
            actionBar.setDisplayShowHomeEnabled(true);
        }

        name = getIntent().getStringArrayListExtra("name");
        photo = getIntent().getStringArrayListExtra("photo");
        rating = getIntent().getStringArrayListExtra("rating");
        review = getIntent().getStringArrayListExtra("review");

        listView = findViewById(R.id.reviewListView);

        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        shimmerFrameLayout.startShimmer();

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
        final ViewAdapter viewAdapter = new ViewAdapter(this, name, photo, rating, review);
        listView.setAdapter(viewAdapter);
    }

    class ViewAdapter extends ArrayAdapter<String> {
        Context context;
        List<String> name;
        List<String> photo;
        List<String> rating;
        List<String> review;

        ViewAdapter(@NonNull Context c, List<String> name, List<String> photo, List<String> rating, List<String> review) {
            super(c, R.layout.custom_review_layout, R.id.restaurantTitle, name);
            this.context = c;
            this.name = name;
            this.photo = photo;
            this.rating = rating;
            this.review = review;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            isReview = 0;
            ImageView profilePhoto;

            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.custom_review_layout, parent, false);

            System.out.println("\n-----------Position = " + position);

            TextView authorName = view.findViewById(R.id.authorName);
            TextView Rating = view.findViewById(R.id.authorRatings);
            TextView Review = view.findViewById(R.id.authorReview);
            profilePhoto = view.findViewById(R.id.coverImage);
            TextView ViewMoreReviews = findViewById(R.id.viewMoreReviews);

            if (name.get(position) != "No author name") {
                authorName.setText(name.get(position));
            } else {
                isReview = isReview + 1;
            }

            if (rating.get(position) != "No ratings yet") {
                Rating.setText(rating.get(position));
            } else {
                isReview = isReview + 1;
            }

            if (position <= photo.size() && photo.get(position) != "No photo") {
                String url = photo.get(position);
                Picasso.get().load(url).into(profilePhoto);
            } else {
                isReview = isReview + 1;
                //profilePhoto.setImageResource(R.drawable.no_image);
            }

            if (review.get(position) != "No reviews yet") {
                Review.setText(review.get(position));
            } else {
                isReview = isReview + 1;
                //Review.setText("No reviews yet");
            }

            if (isReview == 4) {
                listView.setVisibility(View.GONE);
                ViewMoreReviews.setText("No reviews yet");
            }

            return view;
        }
    }
}