package de.gamedots.mindlr.mindlrfrontend.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dirk on 18.04.2016.
 * Simple class constructing a share intent with the current post text and starting it
 * showing an chooser dialog for the app to share to.
 */
public class ShareUtil {

    public static void showShareIntent(String shareText, Context context) {
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mindlr Post");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(intent, "Share"));*/
        Intent shareIntent = ShareCompat.IntentBuilder.from((AppCompatActivity)context)
                .setType("text/plain")
                .setText(shareText)
                .createChooserIntent();
        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(shareIntent);
        }
    }
}
