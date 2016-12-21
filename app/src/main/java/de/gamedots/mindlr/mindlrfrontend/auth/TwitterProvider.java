package de.gamedots.mindlr.mindlrfrontend.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;

/**
 * Created by Dirk on 19.12.16.
 */

public class TwitterProvider extends Callback<TwitterSession> implements IdentityProvider {

    public static final String TAG = "TWITTER_PROVIDER";
    private TwitterAuthClient _twitterAuthClient;
    private final String twitterId;
    private IdpCallback _idpCallback;


    public TwitterProvider(Context appContext) {
        twitterId = appContext.getString(R.string.twitter_provider);
        _twitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public String getProviderId() {
        return twitterId;
    }

    @Override
    public void setAuthenticationCallback(IdpCallback callback) {
        _idpCallback = callback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        _twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public IdpResponse refreshToken() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        return createIDPResponse(session);
    }

    @Override
    public void startLogin(Activity activity) {
       _twitterAuthClient.authorize(activity, this);
    }

    @Override
    public void signOut() {
        Twitter.logOut();
    }

    @Override
    public void success(Result<TwitterSession> result) {
        Log.v(LOG.AUTH, "twitter success");
        _idpCallback.onSuccess(createIDPResponse(result.data));
    }

    @Override
    public void failure(TwitterException exception) {
        _idpCallback.onFailure(new Bundle());
    }

    private IdpResponse createIDPResponse(TwitterSession twitterSession) {
        return new IdpResponse(
                twitterId,
                null,
                twitterSession.getAuthToken().token,
                twitterSession.getAuthToken().secret);
    }
}
