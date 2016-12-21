package de.gamedots.mindlr.mindlrfrontend.view.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import de.gamedots.mindlr.mindlrfrontend.R;

/**
 * Created by dirk on 01.11.2016.
 */

public class ConfirmCheckBoxPreference extends CheckBoxPreference {

    /* default constructors to implement */

    public ConfirmCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public ConfirmCheckBoxPreference(Context context) {
        super(context);
    }
    public ConfirmCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        final boolean isChecked = isChecked();
        if (isChecked) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getContext().getString(R.string.alert_warning))
                    .setNegativeButton(getContext().getString(R.string.alert_cancel), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // user canceled so do nothing
                        }
                    })
                    .setPositiveButton(getContext().getString(R.string.alert_ok), new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // set state to false and store it to SharedPreferences
                            setChecked(false);
                        }
                    })
                    .setMessage(R.string.alert_message)
                    .create().show();

            // if the user does not confirm we do not want the pref to get updated
            return;
        }
        super.onClick();
    }
}
