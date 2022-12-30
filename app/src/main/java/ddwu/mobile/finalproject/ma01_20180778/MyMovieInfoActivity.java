package ddwu.mobile.finalproject.ma01_20180778;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;

public class MyMovieInfoActivity extends AppCompatActivity {
    final static String TAG = "MovieInfoActivity";

    TextView tvTitle;
    TextView tvPubDate;
    TextView tvUserRating;
    TextView tvDirector;
    TextView tvActor;
    ImageView imageView;
    EditText etWatchedDate;
    EditText etMemo;

    Movie movie;
    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymovieinfo);

        //        intent 가져오기
        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra("movie");
//        Log.d(TAG, "intent data: " + movie.toString());
        setViews(movie);
        movieDBManager = new MovieDBManager(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnOk:
//                메모, 본 날짜 수정 가능
//                db에 저장
                String watchedDate = etWatchedDate.getText().toString();
                String memo = etMemo.getText().toString();
                boolean result = movieDBManager.modifyMovie(movie, watchedDate, memo);

                if (result) {
                    Toast.makeText(this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    public void setViews(Movie movie) {
        tvTitle = findViewById(R.id.tvTitle2);
        tvPubDate = findViewById(R.id.tvPubDate2);
        tvUserRating = findViewById(R.id.tvUserRating2);
        tvDirector = findViewById(R.id.tvDirector2);
        tvActor = findViewById(R.id.tvActor2);
        imageView = findViewById(R.id.imageView2);
        etMemo = findViewById(R.id.etMemo);
        etWatchedDate = findViewById(R.id.etWatchedDate);

        String result = String.valueOf(Html.fromHtml(movie.getTitle()));
        tvTitle.setText(result);
        tvPubDate.setText(movie.getPubDate());
        tvUserRating.setText(movie.getUserRating());
        tvDirector.setText(movie.getDirector());
        tvActor.setText(movie.getActor());
        // 이미지뷰 글라이드
        Glide.with(imageView.getContext())
                .load(movie.getImage())
                .into(imageView);

        etMemo.setText(movie.getMemo());
        etWatchedDate.setText(movie.getWatchedDate());
//        memo에 값이 있을 경우 가져오기
//        if ( etMemo.getText().toString().length() != 0 ) {
//
//        }
    }
}
