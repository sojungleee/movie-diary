package ddwu.mobile.finalproject.ma01_20180778;

import ddwu.mobile.finalproject.ma01_20180778.model.json.MovieRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface INaverAPIService {
    @GET ("/v1/search/movie.{type}")
    Call<MovieRoot> getMovieList (@Path("type") String type,
                                  @Header("X-Naver-Client-Id") String id,
                                  @Header("X-Naver-Client-Secret") String secret,
                                  @Query("query") String query);
}
