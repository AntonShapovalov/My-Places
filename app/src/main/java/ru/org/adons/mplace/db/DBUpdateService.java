package ru.org.adons.mplace.db;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
            ContentValues values = getUpdateContentValues(place);
            final Location location = intent.getParcelableExtra(MConstants.EXTRA_LOCATION);
            if (location != null) {
                // get coordinates
                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                // get address string
                final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                final StringBuilder sb = new StringBuilder();
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    Log.e(MConstants.LOG_TAG, "Unable to get address", e);
                }
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    final int max = address.getMaxAddressLineIndex();
                    for (int i = 0; i < max; i++) {
                        sb.append(address.getAddressLine(i));
                        if (i < max - 1) {
                            sb.append(", ");
                        }
                    }
                }

                // put in ContentValues
                values.put(PlaceTable.LATITUDE, latitude);
                values.put(PlaceTable.LONGITUDE, longitude);
                values.put(PlaceTable.ADDRESS, sb.toString());
            }
            getContentResolver().insert(DBContentProvider.CONTENT_URI, values);

            // EDIT PLACE
        } else if (MConstants.ACTION_EDIT_PLACE.equals(intent.getAction())) {
            String where = "(" + PlaceTable._ID + " = " + place.getID() + ")";
            Uri uri = ContentUris.withAppendedId(DBContentProvider.CONTENT_ID_URI, place.getID());
            getContentResolver().update(uri, getUpdateContentValues(place), where, null);
        }
    }

    private ContentValues getUpdateContentValues(Place place) {
        final ContentValues values = new ContentValues();
        values.put(PlaceTable.NAME, place.getName());
        values.put(PlaceTable.DATE, place.getDate().getTime());
        values.put(PlaceTable.DESCRIPTION, place.getDescription());
        values.put(PlaceTable.IMAGE_PATH, place.getImagePath());

        // put thumbnail image
        if (!TextUtils.isEmpty(place.getImagePath())) {
            Bitmap thumbnail = ImageUtils.decodeBitmapFromFile(place.getImagePath(), 96, 96);
            if (thumbnail != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 0, out);
                values.put(PlaceTable.THUMBNAIL, out.toByteArray());
            }
        }

        return values;
    }

}
