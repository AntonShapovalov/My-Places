package ru.org.adons.mplace.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.org.adons.mplace.MainActivity;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mplace.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PlaceTable.TABLE_NAME + " ("
                + PlaceTable._ID + " INTEGER PRIMARY KEY,"
                + PlaceTable.NAME + " TEXT,"
                + PlaceTable.DATE + " INTEGER,"
                + PlaceTable.DESCRIPTION + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDB(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDB(db, oldVersion, newVersion);
    }

    private void recreateDB(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MainActivity.LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + PlaceTable.TABLE_NAME);
        onCreate(db);
    }

}
