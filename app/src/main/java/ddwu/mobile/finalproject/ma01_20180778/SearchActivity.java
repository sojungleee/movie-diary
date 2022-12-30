package ddwu.mobile.finalproject.ma01_20180778;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;
import ddwu.mobile.finalproject.ma01_20180778.model.json.MovieRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    final static String TAG = "SearchActivity";

    SearchView searchView;
    ListView listView;

    MovieArrayAdapter adapter;
    ArrayList<Movie> movieArrayList;

    String naverId;
    String naverSecret;
    String apiUrl;

    private Retrofit retrofit;
    private INaverAPIService naverAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        listView = findViewById(R.id.listViewMovies);

        movieArrayList = new ArrayList<Movie>();
        adapter = new MovieArrayAdapter(this, R.layout.listview_layout, movieArrayList);

        naverId = getResources().getString(R.string.client_id);
        naverSecret = getResources().getString(R.string.client_secret);
        apiUrl = getResources().getString(R.string.api_url);

        // 검색 쿼리 값으로 레트로핏 실행
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼이 눌렸을 때 이벤트 처리
//                Toast.makeText(SearchActivity.this, "검색 처리됨: " + query, Toast.LENGTH_SHORT).show();

                adapter.clear();
                Call<MovieRoot> apiCall =
                        naverAPIService.getMovieList("json", naverId, naverSecret, query);
                apiCall.enqueue(apiCallback);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // 검색어가 변경되었을 때 이벤트 처리
                return false;
            }
        });

        //    listView item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
//                현재 영화 정보를 intent에 담아서 addActivity로 이동
                Movie selectedMovie = new Movie();
                selectedMovie.setTitle(movieArrayList.get(pos).getTitle());
                selectedMovie.setImage(movieArrayList.get(pos).getImage());
                selectedMovie.setPubDate(movieArrayList.get(pos).getPubDate());
                selectedMovie.setDirector(movieArrayList.get(pos).getDirector());
                selectedMovie.setActor(movieArrayList.get(pos).getActor());
                selectedMovie.setUserRating(movieArrayList.get(pos).getUserRating());

                Intent intent = new Intent(SearchActivity.this, AddActivity.class);

                intent.putExtra("selectedMovie", selectedMovie);
                startActivity(intent);
            }
        });

        // retrofit 생성
        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        naverAPIService = retrofit.create(INaverAPIService.class);
    }

    Callback<MovieRoot> apiCallback = new Callback<MovieRoot>() {

        @Override
        public void onResponse(Call<MovieRoot> call, Response<MovieRoot> response) {
            Log.d(TAG, "api 연결 성공");
            MovieRoot movieRoot = response.body();
            List<Movie> items = movieRoot.getItems();

//            값이 없는 경우
            if (response.body() == null)
                Toast.makeText(SearchActivity.this, "검색 결과 없음.", Toast.LENGTH_SHORT).show();

//            로그 확인용
//            for (Movie movie : items) {
//                Log.d(TAG, movie.toString());
//            }

            adapter.addAll(items);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }

        @Override
        public void onFailure(Call<MovieRoot> call, Throwable t) {
            Log.d(TAG, "api 연결 실패: " + t);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
