package com.rcdev.popularmovies.data;

/**
 * Created by coreyestes
 * 2016 02 04
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by coreyestes
 * 2016 01 21
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY ="com.rcdev.popularmovies";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)

    public static final String PATH_FAVORITE = "Favorite";


    //Inner class that defines the table contents of the Favorite table
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final  String TABLE_NAME = "Favorite";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_FULL_POSTER = "full_poster";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_DESCRIPTION = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_YEAR = "release_date";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_VOTE_CNT = "vote_count";


        public static Uri buildFavoriteUri (Long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getFavoriteIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}
