package com.bmustapha.ultramediaplayer.modals;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bmustapha.ultramediaplayer.R;
import com.bmustapha.ultramediaplayer.models.PlayList;
import com.bmustapha.ultramediaplayer.shared.PlayListSync;

/**
 * Created by tunde on 9/11/15.
 */
public class PlayListModal {

    PlayList playlist;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private Context context;
    private boolean edit = false;

    public PlayListModal(Context context) {
        this.context = context;
    }

    public PlayListModal(Context context, PlayList playList, boolean edit) {
        this.context = context;
        this.playlist = playList;
        this.edit  = edit;
    }

    public void showDialog(View view) {

        nameEditText = (EditText) view.findViewById(R.id.play_list_name);
        descriptionEditText = (EditText) view.findViewById(R.id.play_list_description);

        String title = (edit) ? "Edit Playlist" : "Add Playlist";
        String positiveButtonText = (edit) ? "Update" : "Save";

        if (edit) {
            nameEditText.setText(playlist.getName());
            descriptionEditText.setText(playlist.getDescription());
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel dialog
                    }
                })
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit) {
                    update(alertDialog);
                } else {
                    save(alertDialog);
                }
            }
        });
    }

    private void update(AlertDialog dialog) {
        if (!validate()) {
            String message = "";
            String playListName = nameEditText.getText().toString();
            String playListDescription = descriptionEditText.getText().toString().isEmpty() ? "" : descriptionEditText.getText().toString();
            if (playlist.getName().equals(playListName) && playlist.getDescription().equals(playListDescription)) {
                message = "Playlist not modified.";
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    PlayListSync.getDataBaseHandler().updatePlayList(playlist.getDbId(), playListName, playListDescription);
                    message = "Playlist updated.";
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Error updating playlist.";
                } finally {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    PlayListSync.refreshPlayLists();
                    dialog.dismiss();
                }
            }
        } else {
            nameEditText.setError("Name is required!");
        }
    }

    private void save(AlertDialog dialog) {
        if (!validate()) {
            String message = "";
            String playListName = nameEditText.getText().toString();
            String playListDescription = descriptionEditText.getText().toString().isEmpty() ? "" : descriptionEditText.getText().toString();
            try {
                PlayListSync.getDataBaseHandler().insertPlayList(playListName, playListDescription);
                message = "Playlist Created";
            } catch (Exception e) {
                e.printStackTrace();
                message = "Unable to create playlist";
            } finally {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if (PlayListSync.getPlayListAdapter() != null) {
                    PlayListSync.refreshPlayLists();
                }
                dialog.dismiss();
            }
        } else {
            nameEditText.setError("Name is required!");
        }
    }

    private boolean validate() {
        return nameEditText.getText().toString().isEmpty();
    }
}
