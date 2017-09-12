package com.example.android.devlag;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ANGA KOKO on 9/10/2017.
 */

public class ProfileLoader extends AsyncTaskLoader<ArrayList<Profile>> {

    String mUrl;
    public ProfileLoader(Context context, String url){
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading(){forceLoad();}

    @Override
    public ArrayList<Profile> loadInBackground() {
        if(mUrl != null || !mUrl.isEmpty()) {
            ArrayList<Profile> profiles = QueryUtils.fetchGitHubData(mUrl);
            return profiles;
        }
        return null;
    }
}
