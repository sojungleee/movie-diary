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

        // Places ????????? ??? ??????????????? ??????
        Places.initialize(getApplicationContext(), getString(R.string.googleMap_key));
        placesClient = Places.createClient(this);

        flpClient = LocationServices.getFusedLocationProviderClient(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // ?????? ????????? ????????? ??? ????????? ??????
//                ??????????????? geocoding??? ??? ??? ?????? ?????? ????????? ????????? place search
                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(query,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addresses.get(0);
                LatLng queryLoc = new LatLng(address.getLatitude(), address.getLongitude());
                Toast.makeText(LocationActivity.this, query + " ?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "?????? ?????? ?????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

//                ?????? ?????? ????????? GoogleMap ?????? ??????
                currentLoc = new LatLng(lat, lng);
//                currentLoc = new LatLng(37.612592, 127.030033); // ?????????????????? ??? ?????? ???????????? ?????? ????????? ???????????? ?????? ??????
                Toast.makeText(LocationActivity.this, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

        /*????????? InfoWindow ?????? ??? marker??? Tag ??? ????????? placeID ???
         * Google PlacesAPI ??? ???????????? ????????? ????????????*/
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
//                1. ???????????? Marker.getTag() ??? ???????????? placeID ??????
//                2. getPlaceDetail() ??? ???????????? Place ??????
//                3. callDetailActivity() ??? Place ????????? ???????????? DetailActivity ?????? (callDetailActivity() ??????)
                String placeId = marker.getTag().toString();    // ????????? setTag() ??? ????????? Place ID ??????
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
                /*?????? ????????? place_id ??? ????????? ????????? ??????*/
                marker.setTag(place.getPlaceId());
            }
        }
    };

    /*????????? ????????? ?????? ????????? ??????
     * PlaceBasicManager ??? ????????? type ??? ????????? PlaceBasic ??? ???????????? ???????????? ????????? ???????????? ?????? */
    private void searchStart(double lat, double lng, int radius, String type) {
        placeBasicManager.searchPlaceBasic(lat, lng, radius, type);
    }

    /*Place ID ??? ????????? ?????? ???????????? ???????????? ??????
     * ????????? InfoWindow ?????? ??? ??????*/
    private Place getPlaceDetail(String placeId) {
        List<Place.Field> placeFields       // ??????????????? ????????? ????????? ?????? ??????
                = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // ?????? ??????

        // ?????? ?????? ??? ?????? ??????/?????? ????????? ??????
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override                    // ?????? ?????? ??? ?????? ????????? ??????
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // ?????? ?????? ???
                final Place place = fetchPlaceResponse.getPlace();

//                dialog??? ?????? ?????? ??????
                AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                builder.setTitle(place.getName())
                        .setMessage("??????: " + "\n" + place.getAddress() + "\n\n" +
                                        "????????????: " + "\n" + place.getPhoneNumber())
                        .setPositiveButton("??????", null)
                        .show();

                Log.i(TAG, "Place found: " + place.getName());  // ?????? ??? ?????? ???
                Log.i(TAG, "Phone: " + place.getPhoneNumber());
                Log.i(TAG, "Address: " + place.getAddress());
                Log.i(TAG, "ID: " + place.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {   // ?????? ?????? ??? ?????? ????????? ??????
            @Override
            public void onFailure(@NonNull Exception exception) {   // ?????? ?????? ???
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();  // ?????? ??? ??????
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            }
        });
        return null;
    }


//    ?????? ?????? ?????? ?????? ?????????
    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // ????????? ?????? ?????? ????????? ??????
//            Toast.makeText(this, "?????? ?????? ????????? ?????? ??????", Toast.LENGTH_SHORT).show();
        } else {
            // ????????? ?????? ?????? ??????
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
            return false;
        }
        return true;
    }

//    ?????? ?????? ?????? ?????? ?????????
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
                    Toast.makeText(this, "???????????? ?????????", Toast.LENGTH_SHORT).show();
//                    ?????? activity ????????????
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


