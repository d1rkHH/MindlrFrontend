package de.gamedots.mindlr.mindlrfrontend.util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * Created by Dirk on 05.02.16.
 */
public class DebugUtil {

    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
