package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.helper.JSONParser;
import de.gamedots.mindlr.mindlrfrontend.util.PostExecuteTemplate;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;

import static de.gamedots.mindlr.mindlrfrontend.util.Global.BACKEND_METHOD_KEY;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.BACKEND_METHOD_WRITE_POST;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.SERVER_URL;
import static de.gamedots.mindlr.mindlrfrontend.util.Global.METHOD_POST;

public class WritePostActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.prev24);
        if(toolbar != null)
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        return id == android.R.id.home || super.onOptionsItemSelected(item);

    }

    public void writePost(View view){
        Log.d(LOG.WRITE, "About to create WritePostTask");
        EditText editText = (EditText) findViewById(R.id.postWriteArea);
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        String catString = spinner.getSelectedItem().toString();
        new WritePostTask(editText.getText().toString(), Category.getCategoryIDForName(catString)).execute();
    }

    private class WritePostTask extends AsyncTask<Void, Void, JSONObject> {

        String text;
        int categoryID;

        public WritePostTask(String text, int categoryID) {
            this.text = text;
            this.categoryID = categoryID;
        }

        protected void onPreExecute(){
            //Keine Funktionalität
        }

        protected JSONObject doInBackground(Void... params){
            //Needed parameters: UserID, Text, Category, User Information, Method
            //Generate default HashMap with logging values
            HashMap<String, String> parameter = ServerComUtil.newDefaultParameterHashMap();
            //METHOD SPECIFIC PARAMETERS
            parameter.put(BACKEND_METHOD_KEY,BACKEND_METHOD_WRITE_POST);
            parameter.put("USER_ID", "1"); //TODO: Test "TRIAL"
            parameter.put("CONTENT_TEXT", text);
            parameter.put("CATEGORY_ID", Integer.toString(categoryID));

            Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.JSON, "About to create JSONParser");
            JSONParser parser = new JSONParser();
            Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.CONNECTION, "About to make HTTPRequest");
            return parser.makeHttpRequest(SERVER_URL, METHOD_POST, parameter);
        }

        protected void onPostExecute(JSONObject result){
            new PostExecuteTemplate() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d(de.gamedots.mindlr.mindlrfrontend.logging.LOG.POSTS, "Successfull posted.");
                    Toast.makeText(getApplicationContext(), "Erfolgreich gepostet", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                @Override
                public void onFailure(JSONObject result) {
                    try {
                        String text = result.getString("ERROR");
                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e(de.gamedots.mindlr.mindlrfrontend.logging.LOG.JSON, Log.getStackTraceString(e));
                    }
                }
            }.onPostExec(result);
        }
    }
}
