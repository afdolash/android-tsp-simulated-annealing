package com.advinity.afdolash.gisku.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.advinity.afdolash.gisku.fragment.MenuFragment;
import com.advinity.afdolash.gisku.R;
import com.advinity.afdolash.gisku.sa.City;
import com.advinity.afdolash.gisku.sa.Tour;
import com.advinity.afdolash.gisku.sa.TourManager;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.advinity.afdolash.gisku.sa.TourManager.numberOfCities;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {

    private final String SERVER_KEY = "AIzaSyAFpbu-bONGzWJ1V6taipgpxxwIriTjg50";

    private ImageView btn_menu;
    private GoogleMap mMap;
    private ProgressDialog progressDialog;

    private List<LatLng> waypoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Progress dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Creating waypoints...");

        // Initialize button menu event
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        btn_menu.findFocus();
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MenuFragment menuFragment = new MenuFragment();
                fragmentTransaction.replace(R.id.main_container, menuFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Google maps ui settings
        mMap.getUiSettings().setCompassEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng mLatLng = new LatLng(-7.257931, 112.757346);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mLatLng)            // Sets the center of the map to mLatLng
                .zoom(12)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

        // Obtain LatLng on marker by tapping map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.addMarker(new MarkerOptions().position(point));

                TourManager.addCity(new City(point.latitude, point.longitude));
            }
        });
    }

    public void getWaypoints(Tour best) {
        progressDialog.show();

        for (int i = 0; i < best.tourSize(); i++) {
            LatLng latLng = new LatLng(best.getCity(i).getX(), best.getCity(i).getY());
            waypoints.add(latLng);
        }

        LatLng end = new LatLng(best.getCity(0).getX(), best.getCity(0).getY());
        waypoints.add(end);

        progressDialog.hide();

        getDirection();
    }

    public void getDirection() {
        GoogleDirection.withServerKey(SERVER_KEY)
                .from(waypoints.get(0))
                .to(waypoints.get(waypoints.size() - 1))
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Toast.makeText(MainActivity.this, "Direction status : "+ direction.getStatus(), Toast.LENGTH_SHORT).show();
        if (direction.isOK()) {
            mMap.addMarker(new MarkerOptions().position(waypoints.get(0)));
            mMap.addMarker(new MarkerOptions().position(waypoints.get(waypoints.size() - 1)));

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(MainActivity.this, "Direction failed", Toast.LENGTH_SHORT).show();

    }
}
