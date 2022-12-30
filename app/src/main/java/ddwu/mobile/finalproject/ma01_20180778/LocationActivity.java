package ddwu.mobile.finalproject.ma01_20180778;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.finalproject.ma01_20180778.model.json.MovieRoot;
import ddwu.mobile.place.placebasic.OnPlaceBasicResult;
import ddwu.mobile.place.placebasic.PlaceBasicManager;
import ddwu.mobile.place.placebasic.pojo.PlaceBasic;
import retrofit2.Call;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    static final String TAG = "LocationActivity";
    final int REQ_PERMISSION_CODE = 100;

    SearchView searchView;
    Location lastLocation;
    LatLng currentLoc;

    private GoogleMap mGoogleMap;
    FusedLocationProviderClient flpClient;
    private PlaceBasicManager  placeBasicManager;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (checkPermission()) mapLoad();
        searchView = findViewById(R.id.searchView2);

        placeBasicManager = new PlaceBasicManager(getString(R.string.googleMap_key));
        placeBasicManager.setOnPlaceBasicResult(onPlaceBasicResult);

        // Places 초기화 및 클라이언트 생성
        Places.initialize(getApplicationContext(), getString(R.string.googleMap_key));
        placesClient = Places.createClient(this);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 검색 버튼이 눌렸을 때 이벤트 처리
//                검색값으로 geocoding을 해 그 값의 위도 경도를 얻어와 place search
                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(query,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addresses.get(0);
                LatLng queryLoc = new LatLng(address.getLatitude(), address.getLongitude());
                Toast.makeText(LocationActivity.this, query + " 주변 영화관을 검색합니다.", Toast.LENGTH_SHORT).show();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(queryLoc, 17));
                searchStart(queryLoc.latitude, queryLoc.longitude, 5000, PlaceTypes.MOVIE_THEATER);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                Toast.makeText(this, "현재 위치 주변 영화관을 검색합니다.", Toast.LENGTH_SHORT).show();
                searchStart(currentLoc.latitude, currentLoc.longitude, 10000, PlaceTypes.MOVIE_THEATER);
                break;
        }
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                double lat = loc.getLatitude();
                double lng = loc.getLongitude();
                lastLocation = loc;

//                현재 수신 위치로 GoogleMap 위치 설정
                currentLoc = new LatLng(lat, lng);
//                currentLoc = new LatLng(37.612592, 127.030033); // 애뮬레이터로 앱 실행 테스트를 위해 미아역 부근으로 임시 설정
                Toast.makeText(LocationActivity.this, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
                flpClient.removeLocationUpdates(locationCallback);

            }
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (checkPermission()) mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (checkPermission())
                    flpClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.getMainLooper());
                    return false;
            }
        });

        /*마커의 InfoWindow 클릭 시 marker에 Tag 로 보관한 placeID 로
         * Google PlacesAPI 를 이용하여 장소의 상세정보*/
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
//                1. 마커에서 Marker.getTag() 를 사용하여 placeID 확인
//                2. getPlaceDetail() 을 호출하여 Place 검색
//                3. callDetailActivity() 에 Place 정보를 전달하고 DetailActivity 호출 (callDetailActivity() 사용)
                String placeId = marker.getTag().toString();    // 마커의 setTag() 로 저장한 Place ID 확인
                getPlaceDetail(placeId);
            }
        });
    }

    OnPlaceBasicResult onPlaceBasicResult = new OnPlaceBasicResult() {
        @Override
        public void onPlaceBasicResult(List<PlaceBasic> list) {
            for (PlaceBasic place : list) {
                MarkerOptions options = new MarkerOptions()
                        .title(place.getName())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker marker = mGoogleMap.addMarker(options);
                /*현재 장소의 place_id 를 각각의 마커에 보관*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    /*입력된 유형의 주변 정보를 검색
     * PlaceBasicManager 를 사용해 type 의 정보로 PlaceBasic 을 사용하여 현재위치 주변의 관심장소 확인 */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);
    }

    /*Place ID 의 장소에 대한 세부정보 획득하여 반환
     * 마커의 InfoWindow 클릭 시 호출*/
    private Place getPlaceDetail(String placeId) {
        List<Place.Field> placeFields       // 상세정보로 요청할 정보의 유형 지정
                = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // 요청 생성

        // 요청 처리 및 요청 성공/실패 리스너 지정
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override                    // 요청 성공 시 처리 리스너 연결
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // 요청 성공 시
                final Place place = fetchPlaceResponse.getPlace();

//                dialog로 세부 정보 확인
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setTitle(place.getName())
                        .setMessage("주소: " + "\n" + place.getAddress() + "\n\n" +
                                        "전화번호: " + "\n" + place.getPhoneNumber())
                        .setPositiveButton("확인", null)
                        .show();

                Log.i(TAG, "Place found: " + place.getName());  // 장소 명 확인 등
                Log.i(TAG, "Phone: " + place.getPhoneNumber());
                Log.i(TAG, "Address: " + place.getAddress());
                Log.i(TAG, "ID: " + place.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {   // 요청 실패 시 처리 리스너 연결
            @Override
            public void onFailure(@NonNull Exception exception) {   // 요청 실패 시
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();  // 필요 시 확인
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            }
        });
        return null;
    }


//    위치 정보 권한 확인 메소드
    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 경우 수행할 동작
//            Toast.makeText(this, "위치 기반 서비스 권한 확인", Toast.LENGTH_SHORT).show();
        } else {
            // 권한이 없을 경우 요청
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
            return false;
        }
        return true;
    }

//    권한 요청 응답 처리 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults [1] ==  PackageManager.PERMISSION_GRANTED) {
                    mapLoad();
                } else {
                    Toast.makeText(this, "위치권한 미획득", Toast.LENGTH_SHORT).show();
//                    해당 activity 종료하기
                    finish();
                }
        }
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
    protected void onPause() {
        super.onPause();

        flpClient.removeLocationUpdates(locationCallback);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);

    }


}


