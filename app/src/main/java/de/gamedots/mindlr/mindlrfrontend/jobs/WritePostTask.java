package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.activity.MainActivity;

/**
 * Created by Max Wiechmannn on 23.07.16.
 */
public class WritePostTask extends APICallTask {

    private Uri _draftUri;

    public WritePostTask(Context context, JSONObject content, Uri uri) {
        super(context, content, true);
        _apiMethod = Global.BACKEND_METHOD_WRITE_POST;
        _draftUri = uri;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.POSTS, "Successfull posted.");
        Toast.makeText(_context, "Erfolgreich gepostet", Toast.LENGTH_SHORT).show();

        try {
            // false: send post was no draft so store it to db
            // true: post was loaded from draft and is now send successfully
            //        delete draft and insert usercreated post
            boolean isDraft = !(_draftUri == null || _draftUri.toString().isEmpty());
            Utility.storeUserCreatePostFromJSON(_draftUri, isDraft, result, _context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _context.startActivity(new Intent(_context, MainActivity.class));
    }

    @Override
    public void onFailure(JSONObject result) {
        try {
            // failed so save or update draft
            Utility.updateOrCreateDraft(_content,_draftUri, _context);

            String text = result.getString("ERROR");
            Toast.makeText(_context, text, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e(de.gamedots.mindlr.mindlrfrontend.logging.LOG.JSON, Log.getStackTraceString(e));
        }
    }
}