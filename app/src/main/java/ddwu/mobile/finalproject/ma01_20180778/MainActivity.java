package ddwu.mobile.finalproject.ma01_20180778;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    CalendarView calendarView;
    ListView listView;
    TextView tvInfo;

    MovieArrayAdapter adapter;
    ArrayList<Movie> movieArrayList;
    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.myMovieList);
        tvInfo = findViewById(R.id.tvInfo);

        movieArrayList = new ArrayList<Movie>();
        movieDBManager = new MovieDBManager(this);

        adapter = new MovieArrayAdapter(this, R.layout.listview_layout, movieArrayList);
        listView.setAdapter(adapter);

//        click 시 상세 확인 (저장할 때 적은 시청 날짜, 메모 보이게 구현)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Movie movie = movieArrayList.get(position);
                Intent intent = new Intent(MainActivity.this, MyMovieInfoActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
//                해당 날짜를 눌렀을 때 db 검색해서 같은 날짜가 있는지 확인, 있으면 가져오기.
                String date = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth);

                movieArrayList.clear();
                movieArrayList.addAll(movieDBManager.searchMovieByWatchedDate(date));

                if (movieArrayList.isEmpty())
                    tvInfo.setVisibility(View.VISIBLE);
                else
                    tvInfo.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();
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

}