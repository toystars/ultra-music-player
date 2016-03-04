package com.bmustapha.ultramediaplayer.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.bmustapha.ultramediaplayer.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.InputStream;

/**
 * Created by tunde on 8/30/15.
 */
public class AlbumArtLoader {

    private static Uri defaultUri;

    public static void initializeDefaultArt() {
        try {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.default_art).build();
            defaultUri = imageRequest.getSourceUri();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getDefaultArt() {
        return defaultUri;
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

    public static void setImage(Uri uri, SimpleDraweeView imageView) {
        Uri defaultUri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.default_art))
                .build();
        imageView.setImageURI(defaultUri);

        ContentResolver cr = ContextProvider.getContext().getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cur = cr.query(uri, projection, null, null, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String filePath = cur.getString(0);
                if (!new File(filePath).exists()) {
                    uri = null;
                }
            } else {
                uri = null;
            }
            cur.close();
        } else {
            uri = null;
        }
        if (uri != null) {
            imageView.setImageURI(uri);
        }
    }
}
