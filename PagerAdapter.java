package com.example.registerloginsp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments=new ArrayList<>();

    private List<String> name=new ArrayList<>();

    public PagerAdapter(FragmentManager fm){
        super(fm);
      }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
        return name.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return name.get(position);
    }


    public void addFragment(Fragment f,String nam){
        fragments.add(f);
        name.add(nam);
    }
}
