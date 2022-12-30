package ddwu.mobile.finalproject.ma01_20180778;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;

public class MyPageActivity extends AppCompatActivity {

    final static  String TAG = "MyPageActivity";

    ListView listView;

    MovieArrayAdapter adapter;
    ArrayList<Movie> movieArrayList;
    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        listView = findViewById(R.id.myMovieList);

        movieArrayList = new ArrayList<Movie>();
        movieDBManager = new MovieDBManager(this);

        adapter = new MovieArrayAdapter(this, R.layout.listview_layout, movieArrayList);
        listView.setAdapter(adapter);

//        click 시 상세 확인 (저장할 때 적은 시청 날짜, 메모 보이게 구현)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie = movieArrayList.get(position);
                Intent intent = new Intent(MyPageActivity.this, MyMovieInfoActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

//        long click 시 db에서 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                final int pos = position;
                String title = String.valueOf(Html.fromHtml(movieArrayList.get(pos).getTitle()));

                AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                builder.setTitle("영화 삭제")
                        .setMessage(title + " 기록을 삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (movieDBManager.removeMovie(movieArrayList.get(pos).get_id())) {
                                    Toast.makeText(MyPageActivity.this, "선택한 영화가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    movieArrayList.clear();
                                    movieArrayList.addAll(movieDBManager.getAllMovies());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MyPageActivity.this, "선택한 영화 삭제에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_calender:
                intent = new Intent(this, MainActivity.class);
                break;
            case R.id.menu_search:
                intent = new Intent(this, SearchActivity.class);
                break;
            case R.id.menu_location:
                intent = new Intent(this, LocationActivity.class);
                break;
            case R.id.menu_mypage:
                intent = new Intent(this, MyPageActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        movieArrayList.clear();
        movieArrayList.addAll(movieDBManager.getAllMovies());
        if (movieArrayList.isEmpty())
            Toast.makeText(this, "아직 기록된 영화가 없어요. \n 영화를 추가해 보세요!", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }


}
