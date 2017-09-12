package com.example.android.devlag;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ANGA KOKO on 9/10/2017.
 */

public class ProfileAdapter extends ArrayAdapter<Profile> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param profile A List of AndroidFlavor objects to display in a list
     */
    public ProfileAdapter(Activity context, ArrayList<Profile> profile) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, profile);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Profile} object located at this position in the list
        Profile currentProfile = getItem(position);

        //Find the ImageView from list_item.xml
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.profile_pic);
        /**
         * Parse the image link into a string
         * convert the string to a url
         * Get the image from the url using glide
         */
        String imageUrl = currentProfile.getImageLink();
        if((!imageUrl.isEmpty()) || (imageUrl != null)) {
            URL imgUrl = createUrl(imageUrl);
            Glide.with(getContext())
                    .load(imgUrl)
                    .into(imageView);
        }

        //Find the text view for user id from list_item xml
        TextView userIdTextView = (TextView) listItemView.findViewById(R.id.user_id);
        userIdTextView.setText(currentProfile.getUserName());

        return listItemView;
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
