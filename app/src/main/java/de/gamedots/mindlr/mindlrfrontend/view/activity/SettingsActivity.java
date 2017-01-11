package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.gamedots.mindlr.mindlrfrontend.MindlrApplication;
import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.model.ImageUploadResult;
import de.gamedots.mindlr.mindlrfrontend.util.Utility;

/**
 * Created by dirk on 01.11.2016.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            SettingsFragment sf = new SettingsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_container, sf)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUploadResultEvent(ImageUploadResult event) {
        Utility.handleImageResult(event, this);
    }

    public static class SettingsFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener,
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // add pref XML
            addPreferencesFromResource(R.xml.pref_general);

            // bind pref for update on summary
            //findPreference(getString(R.string.pref_notification_key)).setOnPreferenceChangeListener(this);

            // assign listener to signout preference, on click invalidates the authentication SharedPref
            // and launches LoginActivity to sign out the user and show login UI
            final Preference signoutPref = findPreference(getString(R.string.pref_authentication_key));
            signoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    // set auth state to false
                    Utility.addAuthStateToPreference(getActivity(), false);
                    Utility.deactivateCurrentUser(getActivity());

                    MindlrApplication.User.getIdentityProvider().signOut();
                    // Launch LoginActivity and clear back stack
                    Intent signoutIntent = new Intent(getActivity(), TutorialActivity.class);
                    //signoutIntent.putExtra(LoginActivity.SIGNOUT_EXTRA, true);
                    signoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(signoutIntent);

                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        @Override
        public void onPause() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String summaryValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(summaryValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {
                preference.setSummary(summaryValue);
            }

            // updated preference with new value
            return true;
        }

        public void bindPreferenceSummaryToValue(Preference preference) {

            // Set listener to observe changes
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the prefs current values
            onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(
                            preference.getContext()).getString(preference.getKey(), ""));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            if (key.equals(getString(R.string.pref_notification_key))) {
                /*Toast.makeText(getActivity(), "" +
                        prefs.getBoolean(getString(R.string.pref_notification_key),
                                Boolean.parseBoolean(getString(R.string.pref_notification_default))), Toast
                        .LENGTH_SHORT).show();*/
            }
        }
    }
}
