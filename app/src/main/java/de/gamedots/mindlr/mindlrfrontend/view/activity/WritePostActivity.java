package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.jobs.WritePostTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;
import de.gamedots.mindlr.mindlrfrontend.util.Global;

public class WritePostActivity extends AppCompatActivity implements TextWatcher {

    public static final int PICK_IMAGE_REQUEST = 1;

    private static final int POST_CHAR_LIMIT = 500;

    private EditText _postEditText;
    private Spinner _categorySpinner;
    private TextView _charCounter;
    private ImageView _postImageView;
    private ImageButton _closeImageButton;
    private Uri _imageContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }
        // disable background because the root layout already draw its white background
        getWindow().setBackgroundDrawable(null);

        _categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        //TODO: Change Spinner to represent real category objects and only display the name, cat ID easy
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_item, Global.Categories.CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _categorySpinner.setAdapter(categoryAdapter);

        Button button = (Button) findViewById(R.id.postSubmit);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writePost(v);
                }
            });
        }

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.wp_scrollview);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {

                Log.v(LOG.AUTH, "X : " + scrollX + " Y: " + scrollY);
                ViewCompat.setElevation(toolbar, (scrollY > 0) ? 4f : 0f);
            }
        });

        _postImageView = (ImageView) findViewById(R.id.wp_post_imageview);

        _charCounter = (TextView) findViewById(R.id.wp_char_counter);
        _charCounter.setText(String.valueOf(POST_CHAR_LIMIT));

        _postEditText = (EditText) findViewById(R.id.postWriteArea);
        _postEditText.addTextChangedListener(this);

        ImageButton imageSelect = (ImageButton) findViewById(R.id.wp_imageselect);
        if (imageSelect != null) {
            imageSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                            PICK_IMAGE_REQUEST);
                }
            });
        }

        _closeImageButton = (ImageButton) findViewById(R.id.wp_image_close);
        _closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _postImageView.setImageBitmap(null);
                _postImageView.setVisibility(View.GONE);
                _closeImageButton.setVisibility(View.GONE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                _imageContentUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), _imageContentUri);
                    _postImageView.setImageBitmap(bitmap);
                    _postImageView.setVisibility(View.VISIBLE);
                    _closeImageButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    // error in retrieving Bitmap so assume something went wrong with the Uri
                    // so invalidate it
                    _imageContentUri = null;
                }
            }
        }
    }

    public void writePost(View view) {
        Log.d(LOG.WRITE, "About to create WritePostTask");
        String catString = _categorySpinner.getSelectedItem().toString();
        JSONObject content = new JSONObject();
        try {
            JSONArray categories = new JSONArray();
            categories.put(Category.getCategoryIDForName(catString));
            content.put("content_text", _postEditText.getText().toString());
            content.put("content_url", (_imageContentUri == null) ? "" : _imageContentUri.toString());
            content.put("categories", categories);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new WritePostTask(this, content).execute();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        _charCounter.setText(String.valueOf(POST_CHAR_LIMIT - count));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
}