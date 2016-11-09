package de.gamedots.mindlr.mindlrfrontend.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.gamedots.mindlr.mindlrfrontend.view.fragment.TutorialFragment;

/**
 * Created by dirk on 09.11.2016.
 */

public class TutorialPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGMENT_COUNT = 3;

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialFragment.newInstance(0);
            case 1:
                return TutorialFragment.newInstance(1);
            case 2:
                return TutorialFragment.newInstance(2);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
}
