package ru.org.adons.mplace.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.org.adons.mplace.MainActivity;

public class DBContentProvider extends ContentProvider {

    public static final String AUTHORITY = "ru.org.adons.mplace.db";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PlaceTable.TABLE_NAME);
    public static final Uri CONTENT_ID_URI = Uri.parse("content://" + AUTHORITY + "/" + PlaceTable.TABLE_NAME + "/");

    public static final int PLACES = 1;
    public static final int PLACE_ID = 2;
    private UriMatcher uriMatcher;
    private DBHelper dbHelper;

    public DBContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PlaceTable.TABLE_NAME, PLACES);
        uriMatcher.addURI(AUTHORITY, PlaceTable.TABLE_NAME + "/#", PLACE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == PLACES) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(PlaceTable.TABLE_NAME, projection, selection,
                    selectionArgs, null /*no group*/, null /*no filter*/, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } else {
            Log.e(MainActivity.LOG_TAG, "Unknown URI " + uri);
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) == PLACES) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long rowId = db.insert(PlaceTable.TABLE_NAME, null, values);
            if (rowId > 0) {
                Uri noteUri = ContentUris.withAppendedId(CONTENT_ID_URI, rowId);
                // TODO: to prevent reload all items via Loader - delete notifyChange when addPlace will be implemented
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            } else {
                Log.e(MainActivity.LOG_TAG, "Failed to insert row into " + uri);
                return null;
            }
        } else {
            Log.e(MainActivity.LOG_TAG, "Unknown URI " + uri);
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == PLACE_ID) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int rowId = db.delete(PlaceTable.TABLE_NAME, selection, selectionArgs);
            if (rowId > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
                return rowId;
            } else {
                Log.e(MainActivity.LOG_TAG, "Failed to delete row " + uri);
                return 0;
            }
        } else {
            Log.e(MainActivity.LOG_TAG, "Unknown URI " + uri);
            return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
