package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;


public class TutorialFragment extends Fragment {

    public static final String PAGE_ARGUMENT = "page_argument";

    public TutorialFragment() {
    }

    public static TutorialFragment newInstance(int page) {
        TutorialFragment fragmentFirst = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_ARGUMENT, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (getArguments() != null && getArguments().getInt(PAGE_ARGUMENT) == 3) {
            view = inflater.inflate(R.layout.fragment_tutorial_end, container, false);
            List<Item> items = new ArrayList<>();
            Cursor catCursor = getContext().getContentResolver()
                    .query(CategoryEntry.CONTENT_URI, null, null, null, null);

            // get all available categories from db
            // and add theme to collection picker list
            if (catCursor != null) {
                while (catCursor.moveToNext()) {
                    long id = catCursor.getLong(catCursor.getColumnIndex(CategoryEntry._ID));
                    String name = catCursor.getString(catCursor.getColumnIndex(CategoryEntry.COLUMN_NAME));
                    items.add(new Item(Long.toString(id), name));
                }
                catCursor.close();
            }

            CollectionPicker picker = (CollectionPicker) view.findViewById(R.id.collection_item_picker);
            picker.setItems(items);
            picker.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(com.anton46.collectionitempicker.Item item, int position) {
                }
            });

        } else {
            view = inflater.inflate(R.layout.fragment_tutorial, container, false);
            TextView title = (TextView) view.findViewById(R.id.tutorial_title_textview);
            TextView subhead = (TextView) view.findViewById(R.id.tutorial_subhead_textview);
            ImageView slideImage = (ImageView) view.findViewById(R.id.tutorial_imageview);

            int titleRes = R.string.slide_0_title;
            int subheadRes = R.string.slide_0_desc;
            int imageRes = R.drawable.ic_logo;

            if (getArguments() != null) {
                switch (getArguments().getInt(PAGE_ARGUMENT)) {
                    case 1:
                        titleRes = R.string.slide_1_title;
                        subheadRes = R.string.slide_1_desc;
                        break;
                    case 2:
                        titleRes = R.string.slide_2_title;
                        subheadRes = R.string.slide_2_desc;
                        break;
                }
            }

            title.setText(titleRes);
            subhead.setText(subheadRes);
            slideImage.setImageResource(imageRes);
        }

        return view;
    }
}
