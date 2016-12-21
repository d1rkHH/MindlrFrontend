package de.gamedots.mindlr.mindlrfrontend.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Created by Dirk on 20.12.16.
 */

public class CustomTwitterLoginButton extends Button {

    public CustomTwitterLoginButton(Context context) {
        this(context, null);
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    CustomTwitterLoginButton(Context context, AttributeSet attrs, int defStyle,
                             TwitterAuthClient authClient) {
        super(context, attrs, defStyle);
        setupButton();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupButton() {
        final Resources res = getResources();
        super.setCompoundDrawablesWithIntrinsicBounds(
                res.getDrawable(com.twitter.sdk.android.core.R.drawable.tw__ic_logo_default), null, null,
                null);
        super.setCompoundDrawablePadding(
                res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen
                        .tw__login_btn_drawable_padding));
        super.setText(com.twitter.sdk.android.core.R.string.tw__login_btn_txt);
        super.setTextColor(res.getColor(com.twitter.sdk.android.core.R.color.tw__solid_white));
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_text_size));
        super.setTypeface(Typeface.DEFAULT_BOLD);
        super.setPadding(res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen
                .tw__login_btn_left_padding), 0,
                res.getDimensionPixelSize(com.twitter.sdk.android.core.R.dimen.tw__login_btn_right_padding)
                , 0);
        super.setBackgroundResource(com.twitter.sdk.android.core.R.drawable.tw__login_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setAllCaps(false);
        }
    }
}
