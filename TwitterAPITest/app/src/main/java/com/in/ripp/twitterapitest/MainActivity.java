package com.in.ripp.twitterapitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "key";
    private static final String TWITTER_SECRET = "secret";
    private static final String TAG = "Rip";
    private static final ArrayList<String> twitterNames = new ArrayList<String>();

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                MyTwitterApiClient apiclients=new MyTwitterApiClient(session);

                apiclients.getCustomService().list(result.data.getUserId()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void success(Result<ResponseBody> result) {
                        try {
                            JSONObject json = new JSONObject(result.data.string());
                            JSONArray jsonArray = json.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject ParentObject = jsonArray.getJSONObject(i);
                                twitterNames.add(ParentObject.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "success: asdf");
                    }

                    @Override
                    public void failure(TwitterException arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}