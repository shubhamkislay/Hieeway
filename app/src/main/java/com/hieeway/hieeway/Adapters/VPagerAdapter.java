package com.hieeway.hieeway.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class VPagerAdapter extends FragmentPagerAdapter {


    private List<Fragment> fragmentList;

    public VPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);

        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
