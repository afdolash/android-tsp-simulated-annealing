package com.advinity.afdolash.gisku.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.advinity.afdolash.gisku.fragment.MenuFragment;
import com.advinity.afdolash.gisku.R;
import com.advinity.afdolash.gisku.sa.City;
import com.advinity.afdolash.gisku.sa.TourManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView btn_menu;
    GoogleMap mMap;

    // Set simulated annealing variables
    double temp = 0.1;
    double coolingRate = 0.99;
    double absoluteZero = 0.01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
}
