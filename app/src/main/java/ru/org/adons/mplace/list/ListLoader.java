package ru.org.adons.mplace.list;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.org.adons.mplace.Place;
import ru.org.adons.mplace.db.DBContentProvider;
import ru.org.adons.mplace.db.PlaceTable;

public class ListLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private RecyclerAdapter adapter;

    public ListLoader(Context context, RecyclerAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, DBContentProvider.CONTENT_URI, PlaceTable.PLACES_SUMMARY_PROJECTION, null, null, PlaceTable.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean isData = data.moveToFirst();
        List<Place> l = new ArrayList<Place>();
        Place place;
        Bitmap thumbnail;
        byte[] bytes;
        while (isData) {
            place = new Place(context);
            place.setID(data.getInt(PlaceTable.COLUMN_ID_INDEX));
            place.setDate(new Date(data.getLong(PlaceTable.COLUMN_DATE_INDEX)));
            place.setName(data.getString(PlaceTable.COLUMN_NAME_INDEX));
            place.setDescription(data.getString(PlaceTable.COLUMN_DESCRIPTION_INDEX));

            // set thumbnail
            bytes = data.getBlob(PlaceTable.COLUMN_THUMBNAIL_INDEX);
            if (bytes != null && bytes.length > 0) {
                thumbnail = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                place.setThumbnail(thumbnail);
            }

            // set image path
            place.setImagePath(data.getString(PlaceTable.COLUMN_IMAGE_PATH_INDEX));

            l.add(place);
            isData = data.moveToNext();
        }
        adapter.setPlaces(l);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
