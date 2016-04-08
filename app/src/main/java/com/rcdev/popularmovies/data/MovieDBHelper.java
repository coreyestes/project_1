package com.rcdev.popularmovies.data;

/**
 * Created by coreyestes
 * 2016 02 04
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rcdev.popularmovies.objects.MovieItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coreyestes
 * 2016 01 21
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_Favorite_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_YEAR + " TEXT NOT NULL); " +

                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_Favorite_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }



    public boolean hasObject(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
        Cursor cursor = db.rawQuery(selectString, new String[] {id});
        boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;
            int count = 0;
            while(cursor.moveToNext()){
                count++;
            }
        }
        cursor.close();
        db.close();
        return hasObject;
    }



    public ArrayList<MovieItem> getAllMovies() {
        ArrayList<MovieItem> movieList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MovieContract.MovieEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MovieItem movie = new MovieItem(cursor.getString(0), cursor.getString(2),cursor.getString(3), cursor.getString(4),cursor.getString(1), cursor.getString(5));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return movieList;
    }


}