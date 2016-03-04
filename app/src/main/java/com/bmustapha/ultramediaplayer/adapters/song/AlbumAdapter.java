package com.bmustapha.ultramediaplayer.adapters.song;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.adapters.playlist.PlayListAddSongAdapter;
import com.bmustapha.ultramediaplayer.models.Album;
import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.models.Song;
import com.bmustapha.ultramediaplayer.services.MusicService;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;
import com.bmustapha.ultramediaplayer.utilities.AlbumArtLoader;
import com.bmustapha.ultramediaplayer.utilities.MediaQuery;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by tunde on 10/25/15.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    private ArrayList<Album> albums;
    private Activity activity;
    private Typeface face;
    private MusicService musicService;
    private int albumPosition;

    public AlbumAdapter(ArrayList<Album> albums, Activity activity, Typeface face) {
        super();
        this.albums = albums;
        this.activity = activity;
        this.face = face;
        musicService = MusicService.musicService;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder holder, final int position) {
        Album album = albums.get(position);
        holder.albumName.setText(album.getName());
        holder.artistName.setText(album.getArtistName());
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumPosition = position;
                showMenu(view);
            }
        });
        AlbumArtLoader.setImage(album.getArt(), holder.albumArt);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView albumName;
        public TextView artistName;
        public ImageView moreButton;
        public SimpleDraweeView albumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.playlist_name);
            albumName.setTypeface(face);
            artistName = (TextView) itemView.findViewById(R.id.playlist_description);
            artistName.setTypeface(face);
            moreButton = (ImageView) itemView.findViewById(R.id.playlist_more_button);
            albumArt = (SimpleDraweeView) itemView.findViewById(R.id.playlist_art);
        }
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.album_popoup_menu);
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

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shuffle_album_songs:
                shuffleAlbumSongs();
                return true;
            case R.id.add_album_to_playlist:
                addAlbumToPlaylist();
                return true;
            default:
                return false;
        }
    }

    private void shuffleAlbumSongs() {
        musicService.setSongsFromAlbum(MediaQuery.getAlbumSongs(activity, albums.get(albumPosition).getName()));
    }

    private void addAlbumToPlaylist() {
        // get a list of playLists available on the device
        final ArrayList<PlayList> playLists = PlayListSync.getDataBaseHandler().getAllPlayLists();
        // get the view
        final LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.playlist_fragment, null);

        // create dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity)
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
                ArrayList<Song> albumSongs = MediaQuery.getAlbumSongs(activity, albums.get(albumPosition).getName());
                String message = "";
                try {
                    message = processPlayListAdd(albumSongs, playLists.get(position));
                    // message = playList.addSong(currentSong);
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

    private String processPlayListAdd(ArrayList<Song> songs, PlayList playList) {
        boolean status = true;
        for (int count = 0; count < songs.size(); count++) {
            String message = playList.addSong(songs.get(count));
            if (message.equals("Song already in playlist")) {
                status = false;
            }
        }
        return (status) ? "Album added to " + playList.getName() : "Album added to " + playList.getName();
    }
}
