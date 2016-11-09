package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.UserCreatePostEntry;
import de.gamedots.mindlr.mindlrfrontend.jobs.WritePostTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.model.Category;

public class WritePostActivity extends AppCompatActivity implements TextWatcher, LoaderManager
        .LoaderCallbacks<Cursor> {

    public static final int WRITEPOST_DRAFT_LOADER_ID = 3;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final String DRAFT_EXTRA = "draftextra";
    private static final int POST_CHAR_LIMIT = 500;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 42;

    private EditText _postEditText;
    private Spinner _categorySpinner;
    private TextView _charCounter;
    private ImageView _postImageView;
    private ImageButton _closeImageButton;
    private Uri _imageContentUri;
    private SimpleCursorAdapter _categoryAdapter;
    private Uri _loadUri;


    public static final String[] CREATE_POST_COLUMNS = {
            UserCreatePostEntry.TABLE_NAME + "." + UserCreatePostEntry._ID,
            UserCreatePostEntry.COLUMN_CONTENT_TEXT,
            UserCreatePostEntry.COLUMN_CONTENT_URI,
            UserCreatePostEntry.COLUMN_CATEGORY_KEY,
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_CONTENT_TEXT = 1;
    public static final int COLUMN_CONTENT_URI = 2;
    public static final int COLUMN_CATEGORY_KEY = 3;

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

        _categoryAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                getContentResolver().query(CategoryEntry.CONTENT_URI, null, null,
                        null, null), new String[]{CategoryEntry.COLUMN_NAME},
                new int[]{android.R.id.text1},
                0);
        _categoryAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        _categorySpinner.setAdapter(_categoryAdapter);

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
                    if (Build.VERSION.SDK_INT < 19) {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    } else {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                    }
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

        // check if launched from drafts
        if (getIntent() != null && getIntent().hasExtra(DRAFT_EXTRA)) {
            _loadUri = getIntent().getParcelableExtra(DRAFT_EXTRA);
            if (_loadUri != null) {
                getSupportLoaderManager().initLoader(WRITEPOST_DRAFT_LOADER_ID, null, this);
            }
        }

    }

    @Override
    protected void onResume() {
        _categoryAdapter.swapCursor(getContentResolver()
                .query(CategoryEntry.CONTENT_URI, null, null, null, null));
        _categoryAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        _categoryAdapter.swapCursor(null);
        super.onPause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                _imageContentUri = data.getData();
                if (_imageContentUri != null)
                    Toast.makeText(this, "URI: " + _imageContentUri.toString(), Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        return handleNavigationDraftStorage(true);
    }

    @Override
    public void onBackPressed() {
        handleNavigationDraftStorage(false);
    }

    private boolean handleNavigationDraftStorage(final boolean upNavigation) {
        String text = _postEditText.getText().toString();
        if (!text.trim().isEmpty() || (_imageContentUri != null && !_imageContentUri.toString().isEmpty())
                || _loadUri != null) {

            final Dialog dialog = new Dialog(WritePostActivity.this);
            dialog.setContentView(R.layout.dialog_store_drafts);

            TextView info = (TextView) dialog.findViewById(R.id.dialog_draft_textview);
            info.setText("Save Draft?");

            TextView delete = (TextView) dialog.findViewById(R.id.dialog_draft_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do nothing
                    dialog.dismiss();
                    finishNavigation(upNavigation);

                }
            });

            TextView save = (TextView) dialog.findViewById(R.id.dialog_draft_save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // avoid saving draft another time.
                    // If launched from draftsactivity this uri is not null.
                    if (_loadUri == null) {
                        //save draft
                        ContentValues cv = new ContentValues();
                        cv.put(UserCreatePostEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                        cv.put(UserCreatePostEntry.COLUMN_CONTENT_URI, (_imageContentUri == null) ? "" :
                                _imageContentUri.toString());
                        cv.put(UserCreatePostEntry.COLUMN_CONTENT_TEXT, _postEditText.getText().toString());
                        cv.put(UserCreatePostEntry.COLUMN_IS_DRAFT, 1);
                        cv.put(UserCreatePostEntry.COLUMN_SUBMIT_DATE, System.currentTimeMillis());
                        Cursor c = _categoryAdapter.getCursor();
                        c.moveToPosition(_categorySpinner.getSelectedItemPosition());
                        long catID = c.getLong(c.getColumnIndex(CategoryEntry._ID));
                        cv.put(UserCreatePostEntry.COLUMN_CATEGORY_KEY, catID);

                        getContentResolver().insert(UserCreatePostEntry.CONTENT_URI, cv);
                    }
                    dialog.dismiss();
                    // if we come from drafts activity return to it otherwise process normal
                    // up or back navigation
                    finishNavigation(upNavigation);
                }
            });

            dialog.show();
        } else {
            WritePostActivity.super.onBackPressed();
        }
        return true;
    }

    private void finishNavigation(boolean upNavigation) {
        if (upNavigation) {
            if (getIntent() != null && getIntent().hasExtra(DRAFT_EXTRA)) {
                startActivity(new Intent(WritePostActivity.this, DraftsActivity.class).setFlags
                        (Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                NavUtils.navigateUpFromSameTask(WritePostActivity.this);
            }
        } else {
            WritePostActivity.super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                UserCreatePostEntry.CONTENT_URI,
                CREATE_POST_COLUMNS,
                UserCreatePostEntry._ID + " = ? ",
                new String[]{UserCreatePostEntry.getIdPathFromUri(_loadUri)},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        _postEditText.setText(cursor.getString(COLUMN_CONTENT_TEXT));
        // because the spinner is based on db the id is equal to the spinner row selection id
        Log.v(LOG.AUTH, "categorie loaded " + (int) cursor.getLong(COLUMN_CATEGORY_KEY));
        _categorySpinner.setSelection((int) cursor.getLong(COLUMN_CATEGORY_KEY), true);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                    Uri.parse(cursor.getString(COLUMN_CONTENT_URI)));
            _postImageView.setImageBitmap(bitmap);
            _postImageView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
            _postImageView.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}