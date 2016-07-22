package de.gamedots.mindlr.mindlrfrontend.util;

import android.os.Build;

import java.util.HashMap;

/**
 * Created by max on 08.11.15.
 */
public class ServerComUtil {

    public static HashMap<String, String> getMetaDataHashMap(){
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("BRAND", android.os.Build.BRAND);
        parameter.put("MODEL", android.os.Build.MODEL);
        parameter.put("PRODUCT", Build.PRODUCT);
        parameter.put("SDK", "" + Build.VERSION.SDK_INT);
        return parameter;
    }
}
