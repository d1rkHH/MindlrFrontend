package de.gamedots.mindlr.mindlrfrontend.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.gamedots.mindlr.mindlrfrontend.R;
import de.gamedots.mindlr.mindlrfrontend.adapter.TutorialPagerAdapter;

public class TutorialActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private LinearLayout dotsLayout;
    private Button btnSkip;
    private Button btnNext;
    private TextView[] dots;
    ViewPager viewPager;
    private int lastActiveDotPos;

    private int[] colorsActive;
    private int[] colorsInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        colorsActive = getResources().getIntArray(R.array.array_dot_active);
        colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        viewPager = (ViewPager) findViewById(R.id.tutorial_viewpager);
        TutorialPagerAdapter pagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager());
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(this);
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
                    .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.tutorial_btn_skip);
        btnNext = (Button) findViewById(R.id.tutorial_btn_next);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLogin();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int next = viewPager.getCurrentItem() + 1;
                // if not at the end move further, otherwise launch login activity
                if (next < TutorialPagerAdapter.FRAGMENT_COUNT) {
                    viewPager.setCurrentItem(next);
                } else {
                    launchLogin();
                }
            }
        });

        final int firstPage = 0;
        dots = new TextView[TutorialPagerAdapter.FRAGMENT_COUNT];
        for (int i = 0; i < dots.length; ++i) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[firstPage]);
            dotsLayout.addView(dots[i]);
        }
        dots[firstPage].setTextColor(colorsActive[firstPage]);
        lastActiveDotPos = firstPage;
    }

    private void launchLogin() {
        startActivity(new Intent(TutorialActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        dots[lastActiveDotPos].setTextColor(colorsInactive[lastActiveDotPos]);
        dots[position].setTextColor(colorsActive[position]);
        lastActiveDotPos = position;

        // changing the next button text 'NEXT' / 'GOT IT'
        if (position == TutorialPagerAdapter.FRAGMENT_COUNT - 1) {
            // last page. make button text to GOT IT
            btnNext.setText(getString(R.string.start));
            btnSkip.setVisibility(View.GONE);
        } else {
            // still pages are left
            btnNext.setText(getString(R.string.next));
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
