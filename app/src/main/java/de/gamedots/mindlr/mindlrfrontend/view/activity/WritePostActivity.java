package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Context;
import android.content.Intent;
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
import de.gamedots.mindlr.mindlrfrontend.util.BackendTask;
import de.gamedots.mindlr.mindlrfrontend.util.Global;
import de.gamedots.mindlr.mindlrfrontend.util.ServerComUtil;

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
        HashMap<String, String> content = new HashMap<>();
        content.put("content_text", editText.getText().toString());
        content.put("category_id", Integer.toString(Category.getCategoryIDForName(catString)));
        new WritePostTask(this, content, ServerComUtil.getMetaDataHashMap()).execute();
    }

    private class WritePostTask extends BackendTask {

        public WritePostTask(Context context, HashMap<String, String> content, HashMap<String,
                String> metadata){
            super(context, content, metadata);
            _apiMethod = Global.BACKEND_METHOD_WRITE_POST;
        }

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
    }
}
