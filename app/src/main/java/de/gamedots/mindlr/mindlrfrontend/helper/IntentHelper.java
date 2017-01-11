package de.gamedots.mindlr.mindlrfrontend.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class IntentHelper {
    public static final int PICK_IMAGE_REQUEST = 1;


    public static void imageChooseIntent(Activity activity){
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");

            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    public static Intent buildNewClearTask(Context ctxPackage, Class cls){
        return new Intent(ctxPackage,cls)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }
}
