package ru.org.adons.mplace.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
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
        if (!isData) return;
        List<Place> l = new ArrayList<Place>();
        int ID;
        String name;
        while (isData) {
            ID = data.getInt(PlaceTable.COLUMN_ID_INDEX);
            name = data.getString(PlaceTable.COLUMN_NAME_INDEX);
            l.add(new Place(ID, name));
            isData = data.moveToNext();
        }
        adapter.setPlaces(l);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
