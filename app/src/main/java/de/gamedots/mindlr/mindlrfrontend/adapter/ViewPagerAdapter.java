package de.gamedots.mindlr.mindlrfrontend.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 14.04.2016.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> _fragmentList = new ArrayList<>();
    private final List<String> _fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return _fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return _fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return _fragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        _fragmentList.add(fragment);
        _fragmentTitleList.add(title);
    }
}
