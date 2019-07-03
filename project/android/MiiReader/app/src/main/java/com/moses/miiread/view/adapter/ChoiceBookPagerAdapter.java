package com.moses.miiread.view.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.moses.miiread.view.fragment.ChoiceBookPagerFragment;

import java.util.List;

public class ChoiceBookPagerAdapter extends FragmentStatePagerAdapter {

    private List<ChoiceBookPagerFragment> fragments;

    public ChoiceBookPagerAdapter(@NonNull FragmentManager fm, int behavior, List<ChoiceBookPagerFragment> fragments) {
        super(fm, behavior);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}