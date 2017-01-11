package de.gamedots.mindlr.mindlrfrontend.view.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;
import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.auth.GoogleProvider;
import de.gamedots.mindlr.mindlrfrontend.auth.TwitterProvider;
import de.gamedots.mindlr.mindlrfrontend.data.MindlrContract.CategoryEntry;
import de.gamedots.mindlr.mindlrfrontend.helper.CategoryHelper;
import de.gamedots.mindlr.mindlrfrontend.view.customview.CustomTwitterLoginButton;


public class TutorialFragment extends Fragment {

    public static final String PAGE_ARGUMENT = "page_argument";
    public static final int MIN_SELECT_CATEGORIES = 3;

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
        final View view;
        if (getArguments() != null && getArguments().getInt(PAGE_ARGUMENT) == 3) {
            view = inflater.inflate(R.layout.fragment_tutorial_end, container, false);
            List<Item> items = new ArrayList<>();
            Cursor catCursor = getContext().getContentResolver()
                    .query(CategoryEntry.CONTENT_URI, null, null, null, null);

            final Set<Long> selectedCategories = CategoryHelper.getCategories();

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

            /* add the authentication fragment to the activity and configure listener. */
            final AuthFragment authFragment = AuthFragment.getInstance();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.add(R.id.auth_button_container, authFragment, AuthFragment.TAG);
            ft.commit();

            SignInButton googleLoginButton =
                    (SignInButton) view.findViewById(R.id.google_signIn_button);
            CustomTwitterLoginButton twitterLoginButton =
                    (CustomTwitterLoginButton) view.findViewById(R.id.twitter_login_button);

            /* Provider Button handling, set provider according to pressed button and start auth flow */
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof CustomTwitterLoginButton) {
                        authFragment.setIdentityProvider(new TwitterProvider(getContext()));
                    } else if (v instanceof SignInButton) {
                        authFragment.setIdentityProvider(new GoogleProvider(getActivity()));
                    }
                    authFragment.startLogin();
                }
            };
            twitterLoginButton.setOnClickListener(listener);
            googleLoginButton.setOnClickListener(listener);

            CollectionPicker picker = (CollectionPicker) view.findViewById(R.id.collection_item_picker);
            picker.setItems(items);
            picker.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onClick(com.anton46.collectionitempicker.Item item, int position) {
                    long id = Long.parseLong(item.id);
                    if (selectedCategories.contains(id)) {
                        selectedCategories.remove(id);
                    } else {
                        selectedCategories.add(id);
                    }

                    view.findViewById(R.id.auth_button_container).setVisibility(
                            (selectedCategories.size() >= MIN_SELECT_CATEGORIES) ?
                                    View.VISIBLE
                                    : View.INVISIBLE);
                }
            });
            // end if page == 3
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
