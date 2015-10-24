package com.bmustapha.ultramediaplayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SongAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Song> songs;
    private Song currentSong;
    private LayoutInflater songInflater;
    private Typeface face;
    private Context context;
    private Activity activity;
    private boolean isMainSongList;
    private boolean isFav;

    public SongAdapter(Context context, ArrayList<Song> theSongs, Typeface face, boolean isMainSongList, boolean isFav) {
        this.context = context;
        this.activity = (Activity) context;
        this.songs = theSongs;
        this.songInflater = LayoutInflater.from(context);
        this.face = face;
        this.isMainSongList = isMainSongList;
        this.isFav = isFav;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Song getItem(int index) {
        return songs.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    public class ViewHolder {
        TextView songTitle;
        TextView songArtist;
        TextView songDuration;
        ImageView moreButton;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            if (isMainSongList) {
                convertView = songInflater.inflate(R.layout.song, parent, false);
            } else {
                convertView = songInflater.inflate(R.layout.no_more_button_song, parent, false);
            }
            holder = new ViewHolder();
            holder.songTitle = (TextView) convertView.findViewById(R.id.song_title);
            holder.songTitle.setTypeface(face);
            holder.songArtist = (TextView) convertView.findViewById(R.id.song_artist);
            holder.songArtist.setTypeface(face);
            holder.songDuration = (TextView) convertView.findViewById(R.id.song_duration);
            holder.songDuration.setTypeface(face);
            if (isMainSongList) {
                holder.moreButton = (ImageView) convertView.findViewById(R.id.more_button);

            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        holder.songTitle.setText(currSong.getTitle());
        holder.songArtist.setText(currSong.getArtist());
        holder.songDuration.setText(currSong.getFormattedTime());
        if (isMainSongList) {
            holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentSong = songs.get(position);
                    showMenu(v);
                }
            });
        }

        return convertView;
    }

    public void setSongs(ArrayList<Song> newSongList) {
        songs = newSongList;
    }

    private void addToPlayList() {
        // get a list of playLists available on the device
        final ArrayList<PlayList> playLists = PlayListSync.getDataBaseHandler().getAllPlayLists();
        // get the view
        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.playlist_fragment, null);

        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setMessage("Add song to Playlist")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        ListView listView = (ListView) view.findViewById(R.id.play_list_view);
        PlayListAddSongAdapter playListAdapter2 = new PlayListAddSongAdapter(activity, playLists, face);
        listView.setAdapter(playListAdapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // add song to playList if not in playlist before...
                String message = "";
                try {
                    PlayList playList = playLists.get(position);
                    message = playList.addSong(currentSong);
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Unable to add song.";
                } finally {
                    alertDialog.dismiss();
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRingTone() {
        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle("Set as ringtone?")
                .setIcon(R.drawable.ic_phone_android)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, currentSong.getTrackUri());
                            Toast.makeText(context, "Ringtone set successfully.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error setting ringtone!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void addToFavourites() {
        String message = "";
        try {
            if (PlayListSync.getDataBaseHandler().isInFavourites(currentSong.getID())) {
                message = "Song already in Favourites";
            } else {
                long songId = currentSong.getID();
                String artist = currentSong.getArtist();
                String title = currentSong.getTitle();
                long duration = currentSong.getDuration();
                String album = currentSong.getAlbum();
                String albumURI = currentSong.getAlbumArtUri().toString();
                String trackURI = currentSong.getTrackUri().toString();
                PlayListSync.getDataBaseHandler().addToFavourites(songId, artist, title, duration, album, albumURI, trackURI);
                message = "Song added to Favourites";
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error adding song to Favourites";
        } finally {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            PlayListSync.refreshFavouritesSongs();
        }

    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_playlist:
                addToPlayList();
                return true;
            case R.id.add_to_favourite:
                addToFavourites();
                return true;
            case R.id.set_ringtone:
                setRingTone();
                return true;
            default:
                return false;
        }
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener(this);
        if (isFav) {
            popup.inflate(R.menu.menu_fav_song_list_pop);
        } else {
            popup.inflate(R.menu.menu_songlist_pop);
        }
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            Log.w("TAG", "error forcing menu icons to show", e);
            popup.show();
            return;
        }
        popup.show();
    }
}
