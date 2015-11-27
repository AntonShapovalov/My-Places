package ru.org.adons.mplace.db;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;

import ru.org.adons.mplace.MConstants;
import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.edit.ImageUtils;

public class DBUpdateService extends IntentService {

    public DBUpdateService() {
        super("DBUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Place place = (Place) intent.getSerializableExtra(MConstants.EXTRA_PLACE);

        // ADD PLACE
        if (MConstants.ACTION_ADD_PLACE.equals(intent.getAction())) {
            getContentResolver().insert(DBContentProvider.CONTENT_URI, getContentValues(place));

            // EDIT PLACE
        } else if (MConstants.ACTION_EDIT_PLACE.equals(intent.getAction())) {
            String where = "(" + PlaceTable._ID + " = " + place.getID() + ")";
            Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, place.getID());
            getContentResolver().update(uri, getContentValues(place), where, null);
        }

    }

    private ContentValues getContentValues(Place place) {
        final ContentValues values = new ContentValues();
        values.put(PlaceTable.NAME, place.getName());
        values.put(PlaceTable.DATE, place.getDate().getTime());
        values.put(PlaceTable.DESCRIPTION, place.getDescription());
        values.put(PlaceTable.IMAGE_PATH, place.getImagePath());

        // put thumbnail image
        if (!TextUtils.isEmpty(place.getImagePath())) {
            Bitmap thumbnail = null;
            if (!TextUtils.isEmpty(place.getImagePath())) {
                thumbnail = ImageUtils.decodeBitmapFromFile(place.getImagePath(), 96, 96);
            }
            if (thumbnail != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 0, out);
                values.put(PlaceTable.THUMBNAIL, out.toByteArray());
            }
        }

        return values;
    }

}
