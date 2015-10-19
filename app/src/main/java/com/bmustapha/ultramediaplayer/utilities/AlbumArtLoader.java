package com.bmustapha.ultramediaplayer.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by tunde on 8/30/15.
 */
public class AlbumArtLoader {

    private static Drawable defaultArt;

    public static void setDefaultArt(Drawable drawable) {
        defaultArt = drawable;
    }

    public static Drawable getDefaultArt() {
        return defaultArt;
    }

    public static Bitmap getTrackCoverArt(Context context, Uri uri){
        Bitmap songCoverArt = null;
            ContentResolver res = context.getContentResolver();
            try {
                InputStream in = res.openInputStream(uri);
                songCoverArt = BitmapFactory.decodeStream(in);
            } catch (Exception e){
                e.printStackTrace();
            }

        return songCoverArt;
    }
}
