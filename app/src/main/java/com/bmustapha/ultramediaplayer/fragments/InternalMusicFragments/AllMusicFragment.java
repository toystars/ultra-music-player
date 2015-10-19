package com.bmustapha.ultramediaplayer.fragments.InternalMusicFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmustapha.ultramediaplayer.R;

/**
 * Created by andela on 10/19/15.
 */
public class AllMusicFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        return view;
    }
}
