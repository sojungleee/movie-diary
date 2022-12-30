package ddwu.mobile.finalproject.ma01_20180778;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MovieDBHelper extends SQLiteOpenHelper {

    private final String TAG = "MovieDBHelper";

    private final static String DB_NAME = "movie_db";
    public final static String TABLE_NAME = "movie_table";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_LINK = "link";
    public final static String COL_IMAGE = "image";
    public final static String COL_PUBDATE = "pubDate";
    public final static String COL_DIRECTOR = "director";
    public final static String COL_ACTOR = "actor";
    public final static String COL_USERRATING = "userRating";
    public final static String COL_WATCHEDDATE = "watchedDate";
    public final static String COL_MEMO = "memo";


    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "create table " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, "
                + COL_TITLE + " TEXT, " + COL_LINK + " TEXT, " + COL_IMAGE + " TEXT, "
                + COL_PUBDATE + " TEXT, " + COL_DIRECTOR + " TEXT, "
                + COL_ACTOR + " TEXT, " + COL_USERRATING + " TEXT, "
                + COL_WATCHEDDATE + " TEXT, " + COL_MEMO + " TEXT);";

        Log.d(TAG, createSql);
        db.execSQL(createSql);

//        샘플 데이터  추가
//        db.execSQL("INSERT INTO " + TABLE_NAME +
//                " VALUES (null, '영화 제목', '영화 링크', '이미지', '개봉연도', '감독', '출연 배우', '평점');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


}
