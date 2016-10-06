package com.bmustapha.ultramediaplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.AlbumFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.AllMusicFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.FavouriteFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.PlayListFragment;

/**
 * Created by toystars on 10/19/15.
 *
 */
public class MusicPagerAdapter extends FragmentStatePagerAdapter {

    public MusicPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
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
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Songs";
            case 1:
                return "Favourites";
            case 2:
                return "Albums";
            case 3:
                return "Playlists";
        }
        return null;
    }
}
