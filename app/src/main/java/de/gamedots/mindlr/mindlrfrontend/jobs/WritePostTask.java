package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.activity.MainActivity;

/**
 * Created by Max Wiechmannn on 23.07.16.
 */
public class WritePostTask extends APICallTask {

    private Uri _draftUri;

    public WritePostTask(Context context, JSONObject content, Uri uri) {
        super(context, content);
        _apiMethod = Global.BACKEND_METHOD_WRITE_POST;
        _draftUri = uri;
    }

    @Override
    public void onSuccess(JSONObject result) {
        Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.POSTS, "Successfull posted.");
        Toast.makeText(_context, "Erfolgreich gepostet", Toast.LENGTH_SHORT).show();

        // send post was no draft so store it to db
        if (_draftUri == null || _draftUri.toString().isEmpty()) {
            ContentValues cv = new ContentValues();
            try {
                Utility.buildUserCreatePostValuesFromJSON(cv, false, _content);
                _context.getContentResolver().insert(UserCreatePostEntry.CONTENT_URI, cv);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // post was loaded from draft and is now send successfully, so update draft flag -> false
            ContentValues cv = new ContentValues();
            cv.put(UserCreatePostEntry.COLUMN_SUBMIT_DATE, System.currentTimeMillis());
            cv.put(UserCreatePostEntry.COLUMN_IS_DRAFT, 0);
            _context.getContentResolver().update(UserCreatePostEntry.CONTENT_URI, cv,
                    UserCreatePostEntry._ID + " = ? ",
                    new String[]{UserCreatePostEntry.getIdPathFromUri(_draftUri)});
        }
        _context.startActivity(new Intent(_context, MainActivity.class));
    }

    @Override
    public void onFailure(JSONObject result) {
        ContentValues cv = new ContentValues();
        try {
            // save post if its not a draft
            if (_draftUri == null || _draftUri.toString().isEmpty()) {
                Utility.buildUserCreatePostValuesFromJSON(cv, true, _content);
                _context.getContentResolver().insert(UserCreatePostEntry.CONTENT_URI, cv);
            }

            String text = result.getString("ERROR");
            Toast.makeText(_context, text, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e(de.gamedots.mindlr.mindlrfrontend.logging.LOG.JSON, Log.getStackTraceString(e));
        }
    }
}