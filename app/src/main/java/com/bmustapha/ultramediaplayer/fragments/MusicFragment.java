package com.bmustapha.ultramediaplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.MusicPagerAdapter;

/**
 * Created by andela on 10/19/15.
 */
public class MusicFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);

        MusicPagerAdapter adapter = new MusicPagerAdapter(getChildFragmentManager());
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return view;
    }
}
