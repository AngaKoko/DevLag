package com.example.android.devlag;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.net.MalformedURLException;
import java.net.URL;


public class ProfileActivity extends AppCompatActivity {

    /**
     * Create Static variables to hold the values from the MainActivity
     */
    private static String mUrl = "";
    private static String mUserId = "";
    private static String mImageLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Find a reference to the ImageView in the layout
        ImageView imageView = (ImageView) findViewById(R.id.profile_pic);

        //Find reference to the user_id in the layout
        TextView idTextView = (TextView) findViewById(R.id.user_id);

        //Find reference to the html_url in the layout
        TextView urlTextView = (TextView) findViewById(R.id.html_url);

        //Find reference to the share_button in the layout
        ImageButton shareButton = (ImageButton) findViewById(R.id.share_button);

        /**
         * Get the values from the MainActivity and parse them into declared variables
         */
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null){
            mUserId = (String) bd.get("USER_ID");
            mImageLink = (String) bd.get("IMAGE_LINK");
            mUrl = (String) bd.get("URL");

            //Set the text of the user_id TextView in the layout
            idTextView.setText(mUserId);
            //Set the text of the html_url TextView in the layout
            urlTextView.setText(mUrl);

            /**
             * Parse the image link into a string
             * convert the string to a url
             * Get the image from the url using glide
             */
            String imageUrl = mImageLink;
            if((!imageUrl.isEmpty()) || (imageUrl != null)) {
                URL imgUrl = createUrl(imageUrl);
                Glide.with(this)
                        .load(imgUrl)
                        .into(imageView);
            }
        }

        //Set an onClickListener for the urlTextView, which sends an intent to a web browser
        //to open a website with more information about the selected profile.
        urlTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri httpUrl = Uri.parse(mUrl);

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, httpUrl);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //Set an OnClickListener for the share button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out this awesome developer @"+mUserId+" "+mUrl;
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lagos Developers");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        }
        return url;
    }
}
