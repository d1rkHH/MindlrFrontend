package de.gamedots.mindlr.mindlrfrontend.auth;

import android.content.Context;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * Created by Dirk on 20.12.16.
 */

public class ProviderFactory {

    public static IdentityProvider getInstance(String type, Context context){
        if(type.equals(context.getString(R.string.google_provider))){
            return new GoogleProvider(context);
        } else if (type.equals(context.getString(R.string.twitter_provider))){
            return new TwitterProvider(context);
        } else {
            throw new IllegalArgumentException("Type must match the available provider");
        }
    }
}
