package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.jobs.WritePostTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

public class WritePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //appbar_toolbar.setNavigationIcon(R.drawable.prev24);
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


    public void writePost(View view){
        Log.d(LOG.WRITE, "About to create WritePostTask");
        EditText editText = (EditText) findViewById(R.id.postWriteArea);
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        String catString = spinner.getSelectedItem().toString();
        JSONObject content = new JSONObject();
        try {
            JSONArray categories = new JSONArray();
            categories.put(Category.getCategoryIDForName(catString));
            content.put("content_text", editText.getText().toString());
            content.put("content_url", ""); // TODO
            content.put("categories", categories);
        } catch (JSONException e){
            e.printStackTrace();
        }
        new WritePostTask(this, content).execute();
    }
}