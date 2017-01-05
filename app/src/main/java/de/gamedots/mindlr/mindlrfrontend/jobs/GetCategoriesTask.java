package de.gamedots.mindlr.mindlrfrontend.jobs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.gamedots.mindlr.mindlrfrontend.data.DatabaseIntentService;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

import static de.gamedots.mindlr.mindlrfrontend.util.DebugUtil.toast;

/**
 * Created by Max Wiechmann on 11.12.16.
 */

public class GetCategoriesTask extends APICallTask{

    private Context _context;

    public GetCategoriesTask(Context context, JSONObject content) {
        super(context, content, false);
        _context = context;
        _apiMethod = Global.BACKEND_METHOD_GET_CATEGORIES;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            if (result.has("categories")) {
                Category.allCategories = new ArrayList<>();
                JSONArray categories = result.getJSONArray("categories");
                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);
                    if (category.has("name")) {
                        Category cat = new Category(category.getString("name"));
                        Category.allCategories.add(cat);
                    } else {
                        Log.e(LOG.JSON, "Post JSON invalid");
                    }
                }
                // insert posts into database
                Intent intent = new Intent(_context, DatabaseIntentService.class);
                intent.setAction(DatabaseIntentService.INSERT_CATEGORIES_ACTION);
                _context.startService(intent);
            } else {
                Log.e(LOG.JSON, "Posts JSON invalid");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(JSONObject result) {
        try {
            String text = result.getString("ERROR");
            toast(_context, text);
        } catch (JSONException e) {
            Log.e(LOG.JSON, Log.getStackTraceString(e));
        }
    }

}
