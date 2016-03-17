package com.bmustapha.ultramediaplayer.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.AlbumFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.AllMusicFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.FavouriteFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.PlayListFragment;

/**
 * Created by toystars on 10/19/15.
 */
public class MusicPagerAdapter extends FragmentStatePagerAdapter {

    private int[] imageResId = {
            R.drawable.ic_action_music,
            R.drawable.ic_favorite,
            R.drawable.ic_album,
            R.drawable.ic_playlist,
            R.drawable.ic_settings_applications
    };

    private Context context;

    public MusicPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllMusicFragment();
            case 1:
                return new FavouriteFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new PlayListFragment();
            case 4:
                return new PlayListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        ImageView img = (ImageView) view.findViewById(R.id.tab_icon);
        img.setImageResource(imageResId[position]);
        return view;
    }
}
