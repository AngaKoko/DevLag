package com.example.android.devlag;

/**
 * Created by ANGA KOKO on 9/9/2017.
 */

public class Profile {

    private String mUserName;
    private String mImageLink;
    private String mUrl;

    //Constructor for class Profile
    public Profile(String userName, String imageLink, String url){
        mUserName = userName;
        mImageLink = imageLink;
        mUrl = url;
    }

    //Returns the User Name
    public String getUserName(){return mUserName;}

    //Returns the Link to user's profile pic
    public String getImageLink(){return mImageLink;}

    //Returns url to users profile
    public String getUrl(){return mUrl;}
}
