package com.bmustapha.ultramediaplayer.modals;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;

/**
 * Created by tunde on 9/11/15.
 */
public class PlayListModal extends DialogFragment {


    private TextView playListDescription;
    private TextView playListName;

    private String recievedName;
    private String recievedDescription;
    private int recievedPlayListId;
    private boolean flag = false;

    public static PlayListModal newInstance(String name, String description, int playListId, boolean flag) {
        PlayListModal playListModal = new PlayListModal();

        // supply arguments
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("description", description);
        args.putInt("playListId", playListId);
        args.putBoolean("edit", flag);
        playListModal.setArguments(args);

        return playListModal;
    }

    // default constructor
    public PlayListModal() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get arguments
        try {
            recievedName = getArguments().getString("name");
            recievedDescription = getArguments().getString("description");
            recievedPlayListId = getArguments().getInt("playListId");
            flag = getArguments().getBoolean("edit");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_playlist, container, false);

        final Dialog dialog = getDialog();
        String title = (flag) ? "Edit Playlist" : "Add New Playlist";
        dialog.setTitle(title);

        playListName = (TextView) view.findViewById(R.id.play_list_name);
        playListDescription = (TextView) view.findViewById(R.id.play_list_description);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_add_playlist_button);
        Button saveButton = (Button) view.findViewById(R.id.save_playlist_button);

        title = (flag) ? "Update" : "Save";
        saveButton.setText(title);

        // set EditText value to incoming value in case of edit
        setData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    update(dialog);
                } else {
                    save(dialog);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        return view;
    }

    private void update(Dialog dialog) {
        if (!validate()) {
            String message = "";
            // check if user actually modified playlist details
            String name = playListName.getText().toString();
            String description = playListDescription.getText().toString().isEmpty() ? "" : playListDescription.getText().toString();
            if (name.equals(recievedName) && description.equals(recievedDescription)) {
                // user did not modify playlist, just dismiss dialog
                message = "Playlist not modified.";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            } else {
                // user modified playlist, update record
                try {
                    PlayListSync.getDataBaseHandler().updatePlayList(recievedPlayListId, name, description);
                    message = "Playlist updated.";
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Error updating playlist.";
                } finally {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    // refresh the list
                    PlayListSync.refreshPlayLists();
                    // cancel dialog
                    dialog.cancel();
                }
            }
        } else {
            playListName.setError("Name is required");
        }
    }

    private void setData() {
        playListName.setText(recievedName);
        playListDescription.setText(recievedDescription);
    }

    private void save(Dialog dialog) {
        if (!validate()) {
            // get user input
            String message = "";
            String name = playListName.getText().toString();
            String description = playListDescription.getText().toString().isEmpty() ? "" : playListDescription.getText().toString();
            // create the playlist object
            try {
                PlayListSync.getDataBaseHandler().insertPlayList(name, description);
                message = "Playlist Created.";
            } catch (Exception e) {
                e.printStackTrace();
                message = "Unable to create playlist.";
            } finally {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                // refresh the list
                if (PlayListSync.getPlayListAdapter() != null) {
                    PlayListSync.refreshPlayLists();
                }
                // cancel dialog
                dialog.cancel();
            }
        } else {
            playListName.setError("Name is required");
        }
    }

    private boolean validate() {
            return playListName.getText().toString().isEmpty();
    }
}
