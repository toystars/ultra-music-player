package com.bmustapha.ultramediaplayer.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.AllMusicFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.FavouriteFragment;
import com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments.PlayListFragment;
import com.bmustapha.ultramediaplayer.utilities.ContextProvider;

/**
 * Created by andela on 10/19/15.
 */
public class MusicPagerAdapter extends FragmentStatePagerAdapter {

    private int[] imageResId = {
            R.drawable.ic_action_music,
            R.drawable.ic_favorite,
            R.drawable.ic_playlist
    };

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
                return new PlayListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return getTitle(position, "All Songs");
            case 1:
                return getTitle(position, "Favourites");
            case 2:
                return getTitle(position, "Playlists");
            default:
                return null;
        }
    }

    private SpannableString getTitle(int position, String title) {
        SpannableString spannableString = new SpannableString("  " + title);
        try {
            Drawable image = ContextProvider.getContext().getResources().getDrawable(imageResId[position]);
            assert image != null;
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }
}
