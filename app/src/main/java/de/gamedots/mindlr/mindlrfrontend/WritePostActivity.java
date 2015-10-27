package de.gamedots.mindlr.mindlrfrontend;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.gamedots.mindlr.mindlrfrontend.models.Category;
import de.gamedots.mindlr.mindlrfrontend.models.ViewPost;

import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.Global.BACKEND_METHOD_WRITE_POST;
import static de.gamedots.mindlr.mindlrfrontend.Global.SERVER_URL;
import static de.gamedots.mindlr.mindlrfrontend.Global.METHOD_POST;
import static de.gamedots.mindlr.mindlrfrontend.Global.DEFAULT_USER_ID;

public class WritePostActivity extends ToolbarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getToolbar().setNavigationIcon(R.drawable.prev24);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);

       // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
       // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spinner.setAdapter(adapter);

        //TODO: Change Spinner to represent real category objects and only display the name, so you can get the categoryID easyly
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Global.Categories.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(categoryAdapter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_write_post;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_post, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void writePost(View view){
        Log.d(LOG.WRITE, "About to create WritePostTask");
        EditText editText = (EditText) findViewById(R.id.postWriteArea);
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        String catString = spinner.getSelectedItem().toString();
        new WritePostTask(this, editText.getText().toString(), Category.getCategoryIDForName(catString)).execute();
    }

    private class WritePostTask extends AsyncTask<Void, Void, JSONObject> {

        Context context;
        String text;
        int categoryID;

        public WritePostTask(Context context, String text, int categoryID) {
            this.context = context;
            this.text = text;
            this.categoryID = categoryID;
        }

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            //Needed parameters: UserID, Text, Category, User Information, Method
            HashMap<String, String> parameter = new HashMap<>();
            //LOGGING PARAMETERS
            parameter.put("BRAND", android.os.Build.BRAND);
            parameter.put("MODEL", android.os.Build.MODEL);
            parameter.put("PRODUCT", Build.PRODUCT);
            parameter.put("SDK", "" + Build.VERSION.SDK_INT);
            parameter.put("TIME", "" + new Date());
            //METHOD SPECIFIC PARAMETERS
            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_WRITE_POST);
            parameter.put("USER_ID", Integer.toString(DEFAULT_USER_ID));
            parameter.put("CONTENT_TEXT", text);
            parameter.put("CATEGORY_ID", Integer.toString(categoryID));

            Log.d(LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject result){
            if(result != null){
                try {
                    boolean success = result.getBoolean("SUCCESS");
                    if(success){
                        Log.d(LOG.POSTS, "Successfull posted.");
                        Toast.makeText(context, "Erfolgreich gepostet", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, MainActivity.class));
                    } else {
                        Log.d(LOG.POSTS, "Could not post.");
                        Toast.makeText(context, "Post konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Log.d(LOG.JSON, "Error parsing data into objects");
                    e.printStackTrace();
                }
            } else{
                Log.d(LOG.JSON, "JSONObject was null");
            }
        }
    }
}
