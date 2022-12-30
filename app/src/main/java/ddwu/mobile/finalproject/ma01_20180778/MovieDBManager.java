package ddwu.mobile.finalproject.ma01_20180778;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;

public class MovieDBManager {

    MovieDBHelper movieDBHelper = null;
    Cursor cursor = null;

    public MovieDBManager(Context context) {
        movieDBHelper = new MovieDBHelper(context);
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList movieList = new ArrayList();
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM " + MovieDBHelper.TABLE_NAME, null);

        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_TITLE));
            String link = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_LINK));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_IMAGE));
            String pubDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_PUBDATE));
            String director = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_DIRECTOR));
            String actor = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_ACTOR));
            String userRating = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_USERRATING));
            String watchedDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_WATCHEDDATE));
            String memo = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_MEMO));


            movieList.add ( new Movie (id, title, link, image, pubDate, director, actor, userRating, watchedDate, memo) );
        }
        cursor.close();
        movieDBHelper.close();
        return movieList;
    }

    public boolean addNewMovie(Movie newMovie, String watchedDate, String memo) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(MovieDBHelper.COL_TITLE, newMovie.getTitle());
        row.put(MovieDBHelper.COL_LINK, newMovie.getLink());
        row.put(MovieDBHelper.COL_IMAGE, newMovie.getImage());
        row.put(MovieDBHelper.COL_PUBDATE, newMovie.getPubDate());
        row.put(MovieDBHelper.COL_DIRECTOR, newMovie.getDirector());
        row.put(MovieDBHelper.COL_ACTOR, newMovie.getActor());
        row.put(MovieDBHelper.COL_USERRATING, newMovie.getUserRating());
        row.put(MovieDBHelper.COL_WATCHEDDATE, watchedDate);
        row.put(MovieDBHelper.COL_MEMO, memo);

//    insert 메소드를 사용할 경우 데이터 삽입이 정상적으로 이루어질 경우 1 이상, 이상이 있을 경우 0 반환 확인 가능
        long count = db.insert(MovieDBHelper.TABLE_NAME, null, row);
        movieDBHelper.close();
        if (count > 0) return true;
        return false;
    }

//    id 기준으로 기존 data 수정
    public boolean modifyMovie(Movie movie, String watchedDate, String memo) {
        SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(MovieDBHelper.COL_TITLE, movie.getTitle());
        row.put(MovieDBHelper.COL_LINK, movie.getLink());
        row.put(MovieDBHelper.COL_IMAGE, movie.getImage());
        row.put(MovieDBHelper.COL_PUBDATE, movie.getPubDate());
        row.put(MovieDBHelper.COL_DIRECTOR, movie.getDirector());
        row.put(MovieDBHelper.COL_ACTOR, movie.getActor());
        row.put(MovieDBHelper.COL_USERRATING, movie.getUserRating());
        row.put(MovieDBHelper.COL_WATCHEDDATE, watchedDate);
        row.put(MovieDBHelper.COL_MEMO, memo);

        String whereClause = MovieDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(movie.get_id()) };

        int result = sqLiteDatabase.update(MovieDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        movieDBHelper.close();
        if (result > 0) return  true;
        return false;
    }

//    id 기준으로 삭제
    public boolean removeMovie(long id) {
        SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        String whereClause = MovieDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        int result = sqLiteDatabase.delete(MovieDBHelper.TABLE_NAME, whereClause, whereArgs);
        movieDBHelper.close();
        if (result > 0) return true;
        return false;
    }

//    watchedDate 기준으로 영화 검색
    public ArrayList<Movie>  searchMovieByWatchedDate(String date) {
        ArrayList movieList = new ArrayList();
        SQLiteDatabase sqLiteDatabase = movieDBHelper.getReadableDatabase();

        String selection = "watchedDate=?";
        String[] selectArgs = new String[] {date};

        cursor = sqLiteDatabase.query(MovieDBHelper.TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_TITLE));
            String link = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_LINK));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_IMAGE));
            String pubDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_PUBDATE));
            String director = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_DIRECTOR));
            String actor = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_ACTOR));
            String userRating = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_USERRATING));
            String watchedDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_WATCHEDDATE));
            String memo = cursor.getString(cursor.getColumnIndexOrThrow(MovieDBHelper.COL_MEMO));

            movieList.add ( new Movie (id, title, link, image, pubDate, director, actor, userRating, watchedDate, memo) );
        }
        cursor.close();
        movieDBHelper.close();
        return movieList;
    }

    //    close 수행
    public void close() {
        if (movieDBHelper != null) movieDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
