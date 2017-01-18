package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.DraftEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemCategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.ItemEntry;
import de.gamedots.mindlr.mindlrfrontend.helper.IntentHelper;
import de.gamedots.mindlr.mindlrfrontend.helper.UriHelper;
import de.gamedots.mindlr.mindlrfrontend.jobs.ImgurUploadService;
import de.gamedots.mindlr.mindlrfrontend.jobs.WritePostTask;
import de.gamedots.mindlr.mindlrfrontend.logging.LOG;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;
import de.gamedots.mindlr.mindlrfrontend.view.customview.MultiSelectionSpinner;

import static de.gamedots.mindlr.mindlrfrontend.helper.IntentHelper.PICK_IMAGE_REQUEST;

public class WritePostActivity extends AppCompatActivity implements TextWatcher, LoaderManager
        .LoaderCallbacks<Cursor>, RequestListener<Uri, Bitmap> {

    public static final String DRAFT_EXTRA = "draftextra";
    public static final String JSON_CONTENT_TEXT_KEY = "content_text";
    public static final String JSON_CONTENT_URI_KEY = "content_url";
    public static final String JSONARR_CONTENT_CATEGORIES_KEY = "categories";
    public static final String JSON_CONTENT_SERVER_ID_KEY = "item_id";
    public static final String JSON_CONTENT_USER_CREATE_POST_SUBMIT_DATE = "submit_date";

    /* Unique loader id for this activity */
    public static final int WRITEPOST_DRAFT_LOADER_ID = 3;

    /* Maximum character length of the post content text */
    private static final int POST_CHAR_LIMIT = 500;

    private EditText _postEditText;
    private TextView _charCounter;
    private ImageView _postImageView;
    private ImageButton _closeImageButton;
    private Uri _postContentUri;

    private MultiSelectionSpinner _multiSelectionSpinner;

    /* Uri passed from DraftsActivity to determine which draft to populate */
    private Uri _loadUri;

    // region projection for DraftEntry

    public static final String[] DRAFT_COLUMNS = {
            DraftEntry.TABLE_NAME + "." + DraftEntry._ID,
            ItemEntry.TABLE_NAME + "." + ItemEntry._ID,
            ItemEntry.COLUMN_CONTENT_TEXT,
            ItemEntry.COLUMN_CONTENT_URI,
    };

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ITEM_ID = 1;
    public static final int COLUMN_CONTENT_TEXT = 2;
    public static final int COLUMN_CONTENT_URI = 3;

    // endregion

    // region lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG.AUTH, "writepost recreated");
        setContentView(R.layout.activity_write_post);

        // cant handle writing for non authenticated user
        if (!Utility.getAuthStateFromPreference(this)){
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }

        // disable background because the root layout already draw its white background
        getWindow().setBackgroundDrawable(null);

        // Fetch all categories from db and store display names for selection
        Cursor categoriesCursor = getContentResolver().query(CategoryEntry.CONTENT_URI, null,null,null,null);
        ArrayList<String> catArray = new ArrayList<>();
        if (categoriesCursor != null){
            while (categoriesCursor.moveToNext()){
                String name = categoriesCursor.getString(categoriesCursor.getColumnIndex(CategoryEntry
                        .COLUMN_DISPLAY_NAME));
                catArray.add(name);
            }
            categoriesCursor.close();
        }
        _multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.cat_spinner);
        _multiSelectionSpinner.setItems(catArray.toArray(new String[catArray.size()]));

        // write post button to kick off posting
        Button button = (Button) findViewById(R.id.postSubmit);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPostSending(v);
                }
            });
        }

        // scroll handling if post content grow in size
        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.wp_scrollview);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {
                ViewCompat.setElevation(toolbar, (scrollY > 0) ? 4f : 0f);
            }
        });

        // optional image for a post, its initially hidden (gone)
        _postImageView = (ImageView) findViewById(R.id.wp_post_imageview);

        _charCounter = (TextView) findViewById(R.id.wp_char_counter);
        _charCounter.setText(String.valueOf(POST_CHAR_LIMIT));

        _postEditText = (EditText) findViewById(R.id.postWriteArea);
        _postEditText.addTextChangedListener(this);

        // setup image select action; we need to start different intents here because
        // the image picker action has changed from KITKAT(API 19) onwards, so check for sdk version
        // and set appropriate intent
        ImageButton imageSelect = (ImageButton) findViewById(R.id.wp_imageselect);
        if (imageSelect != null) {
            imageSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentHelper.imageChooseIntent(WritePostActivity.this);
                }
            });
        }

        // close button appearing at the top right corner of the select image to dismiss
        _closeImageButton = (ImageButton) findViewById(R.id.wp_image_close);
        _closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _postContentUri = null;
                _postImageView.setImageBitmap(null);
                _postImageView.setVisibility(View.GONE);
                _closeImageButton.setVisibility(View.GONE);
            }
        });

        // check if launched from drafts, if so load up content
        if (getIntent() != null && getIntent().hasExtra(DRAFT_EXTRA)) {
            _loadUri = getIntent().getParcelableExtra(DRAFT_EXTRA);
            if (_loadUri != null) {
                getSupportLoaderManager().initLoader(WRITEPOST_DRAFT_LOADER_ID, null, this);
            }
        }

        checkForActionSend(getIntent());
    }

    private void checkForActionSend(Intent intent){
        // check if launched from share event
        // at this point we do not differ between content types and just append the text
        final String action = intent.getAction();
        if(action != null && action.equals(Intent.ACTION_SEND)) {
            String shareText = intent.getStringExtra(Intent.EXTRA_TEXT);
            _postEditText.append(" ");
            _postEditText.append(shareText);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(LOG.AUTH, "writepost onNewIntent called");
        checkForActionSend(intent);
    }

    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                _postContentUri = data.getData();
                if (_postContentUri != null) {
                    Toast.makeText(this, "URI: " + _postContentUri.toString(), Toast.LENGTH_LONG).show();
                    _postImageView.setVisibility(View.INVISIBLE);

                    Glide.with(this)
                            .loadFromMediaStore(_postContentUri)
                            .asBitmap()
                            .fitCenter()
                            .listener(this)
                            .into(_postImageView);
                }
            }
        }
    }

    // region start send written post to server
    public void startPostSending(View view) {
        if (_postEditText.getText().toString().trim().isEmpty() &&
                (_postContentUri == null || _postContentUri.toString().isEmpty())){
            Toast.makeText(this, R.string.post_empty_not_allowed, Toast.LENGTH_SHORT).show();
        } else {
            // user added image to post content, upload it to imgur to get global URL
            if (_postContentUri != null && !_postContentUri.toString().isEmpty()
                    && !UriHelper.isYoutube(_postContentUri)) {

                ImgurUploadService service = new ImgurUploadService(this, _loadUri, composeContent());
                service.start(_postContentUri);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                // no image select we can start sending right away
                composeContentAndSendToServer();
            }
        }
    }

    private JSONObject composeContent(){
        JSONArray categories = new JSONArray();
        for (String displayName : _multiSelectionSpinner.getSelectedStrings()) {
            Cursor cat = getContentResolver().query(CategoryEntry.CONTENT_URI, null,
                    CategoryEntry.COLUMN_DISPLAY_NAME + " = ? ",
                    new String[]{displayName}, null);
            if (cat != null && cat.moveToNext()) {
                String uniqueName = cat.getString(cat.getColumnIndex(CategoryEntry.COLUMN_NAME));
                categories.put(uniqueName);
                cat.close();
            }
        }

        String contentUri = UriHelper.isYoutube(_postContentUri) ? _postContentUri.toString() : "";
        String postText = _postEditText.getText().toString();

        if (UriHelper.isYoutube(_postContentUri)){
            postText = postText.replace(_postContentUri.toString(), "");
        }

        JSONObject content = new JSONObject();
        try {
            content.put(JSON_CONTENT_TEXT_KEY, postText);
            content.put(JSON_CONTENT_URI_KEY, contentUri);
            content.put(JSONARR_CONTENT_CATEGORIES_KEY, categories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return content;
    }

    private void composeContentAndSendToServer(){
        // send post in background thread and go back to MainActivity
        new WritePostTask(this, composeContent(), _loadUri).execute();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // endregion

    // region textcounter handling
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable postText) {
        if(UriHelper.isYoutube(_postContentUri)
                && !postText.toString().contains(_postContentUri.toString())){
            _postContentUri = null;
        }
        _charCounter.setText(String.valueOf(POST_CHAR_LIMIT - postText.length()));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    // endregion

    // region navigation and store draft
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

        if (!text.trim().isEmpty() || (_postContentUri != null && !_postContentUri.toString().isEmpty())
                || _loadUri != null) {

            final String postText;
            if (UriHelper.isYoutube(_postContentUri)){
                // remove youtube uri from content text for later use
                // because it is stored separately inside an uri field
                postText = text.replace(_postContentUri.toString(), "");
            } else{
                postText = text;
            }

            final Dialog dialog = new Dialog(WritePostActivity.this);
            dialog.setContentView(R.layout.dialog_store_drafts);

            TextView info = (TextView) dialog.findViewById(R.id.dialog_draft_textview);
            info.setText("Save Draft?");

            TextView delete = (TextView) dialog.findViewById(R.id.dialog_draft_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // we come from draftsactivity
                    if (_loadUri != null && getIntent().hasExtra(DRAFT_EXTRA)) {
                        getContentResolver().delete(DraftEntry.CONTENT_URI,
                                DraftEntry._ID + " = ? ",
                                new String[]{DraftEntry.getIdPathFromUri(_loadUri)});
                    }
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

                    // 1. create and insert item
                    ContentValues cv = new ContentValues();
                    cv.put(ItemEntry.COLUMN_CONTENT_URI, (_postContentUri == null) ? "" :
                            _postContentUri.toString());
                    cv.put(ItemEntry.COLUMN_CONTENT_TEXT, postText);

                    long itemId = -1;
                    long draftItemId = -1;
                    if (_loadUri == null) { // insert if new draft
                        itemId = Long.valueOf(ItemEntry.getIdPathFromUri(getContentResolver()
                                .insert(ItemEntry.CONTENT_URI, cv)));
                    } else { // update item entry if already exist
                        Cursor c = getContentResolver().query(DraftEntry.CONTENT_URI,
                                new String[]{DraftEntry.COLUMN_ITEM_KEY},
                                DraftEntry.TABLE_NAME + "." + DraftEntry._ID + " = ?",
                                new String[]{_loadUri.getPathSegments().get(1)}, null);
                        if (c != null && c.moveToFirst()) {
                            draftItemId = c.getLong(c.getColumnIndex(DraftEntry.COLUMN_ITEM_KEY));
                            c.close();
                        }
                        if (draftItemId > 0) {
                            getContentResolver().update(ItemEntry.CONTENT_URI, cv,
                                    ItemEntry.TABLE_NAME + "." + ItemEntry._ID + " = ? ",
                                    new String[]{String.valueOf(draftItemId)});
                            itemId = draftItemId;
                        }
                    }

                    // 2. store item category reations
                    // TODO: multi categories + identify categories by unique name instead of id
                    /*
                    Cursor c = _categoryAdapter.getCursor();
                    c.moveToPosition(_categorySpinner.getSelectedItemPosition());
                    long catID = c.getLong(c.getColumnIndex(CategoryEntry._ID));*/
                    if (itemId >= 0){
                        // delete old category relations
                        getContentResolver().delete(ItemCategoryEntry.CONTENT_URI,
                                ItemCategoryEntry.COLUMN_ITEM_KEY + " = ? ",
                                new String[]{String.valueOf(itemId)});

                        // insert current category selections
                        for (String catDN : _multiSelectionSpinner.getSelectedStrings()){
                            Cursor cat = getContentResolver().query(CategoryEntry.CONTENT_URI, null,
                                    CategoryEntry.COLUMN_DISPLAY_NAME + " = ? ",
                                    new String[]{catDN}, null);
                            if (cat != null && cat.moveToNext()) {
                                long catId = cat.getLong(cat.getColumnIndex(CategoryEntry._ID));
                                if (catId >= 0){
                                    cv = new ContentValues();
                                    cv.put(ItemCategoryEntry.COLUMN_ITEM_KEY, itemId);
                                    cv.put(ItemCategoryEntry.COLUMN_CATEGORY_KEY, catId);
                                    getContentResolver().insert(
                                            ItemCategoryEntry.CONTENT_URI, cv);
                                }
                                cat.close();
                            }
                        }
                    }
                    if (_loadUri == null) {
                        // 3. store draft
                        cv = new ContentValues();
                        cv.put(DraftEntry.COLUMN_USER_KEY, MindlrApplication.User.getId());
                        cv.put(DraftEntry.COLUMN_ITEM_KEY, itemId);
                        cv.put(DraftEntry.COLUMN_SUBMIT_DATE, System.currentTimeMillis());

                        getContentResolver().insert(DraftEntry.CONTENT_URI, cv);
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

    // endregion

    // region loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                _loadUri,
                DRAFT_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        _postEditText.setText(cursor.getString(COLUMN_CONTENT_TEXT));
        _postContentUri = Uri.parse(cursor.getString(COLUMN_CONTENT_URI));
        Log.v(LOG.AUTH, "GOT URI: " + _postContentUri.toString());

        /* Load selection querying the db item x category table using the item id from the draft entry */
        Cursor catIds = getContentResolver().query(
                ItemCategoryEntry.CONTENT_URI,
                new String[]{ItemCategoryEntry.COLUMN_CATEGORY_KEY},
                ItemCategoryEntry.COLUMN_ITEM_KEY + " = ? ",
                new String[]{String.valueOf(cursor.getLong(COLUMN_ITEM_ID))},
                null
        );
        LinkedList<String> selections = new LinkedList<>();
        if (catIds != null){
            while (catIds.moveToNext()){
                Cursor category = getContentResolver().query(CategoryEntry.CONTENT_URI,
                        null, CategoryEntry.TABLE_NAME + "." + CategoryEntry._ID + " = ?",
                        new String[]{
                                String.valueOf(catIds.getLong(
                                        catIds.getColumnIndex(ItemCategoryEntry.COLUMN_CATEGORY_KEY)))
                        },
                        null);
                if (category != null && category.moveToNext()){
                    selections.add(category.getString(category.getColumnIndex(CategoryEntry.COLUMN_DISPLAY_NAME)));
                    category.close();
                }
            }
            catIds.close();
        }
        // set selected categories from draft
        if (selections.size() > 0) {
            _multiSelectionSpinner.setSelection(selections);
        }

        if (_postContentUri.getScheme().equals("content")) {
            _postImageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .loadFromMediaStore(_postContentUri)
                    .asBitmap()
                    .fitCenter()
                    .listener(this)
                    .into(_postImageView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<String> selections = new ArrayList<>(_multiSelectionSpinner.getSelectedStrings());
        outState.putStringArrayList("keys", selections);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        _multiSelectionSpinner.setSelection(savedInstanceState.getStringArrayList("keys"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    // endregion

    @Override
    public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
        // error in retrieving Bitmap so assume something went wrong with the Uri
        // so invalidate it
        Log.v(LOG.AUTH, "ERROR draft image");
        _postContentUri = null;
        _postImageView.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean
            isFromMemoryCache, boolean isFirstResource) {
        Log.v(LOG.AUTH, "SUCCESS draft image");
        _postImageView.setVisibility(View.VISIBLE);
        _closeImageButton.setVisibility(View.VISIBLE);
        return false;
    }
}