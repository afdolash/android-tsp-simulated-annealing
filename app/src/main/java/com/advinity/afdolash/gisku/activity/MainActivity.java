package com.advinity.afdolash.gisku.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.advinity.afdolash.gisku.api.DirectionsJSONParser;
import com.advinity.afdolash.gisku.fragment.MenuFragment;
import com.advinity.afdolash.gisku.R;
import com.advinity.afdolash.gisku.modal.Detail;
import com.advinity.afdolash.gisku.sa.City;
import com.advinity.afdolash.gisku.sa.Tour;
import com.advinity.afdolash.gisku.sa.TourManager;
import com.advinity.afdolash.gisku.sa.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView btn_menu;
    public GoogleMap mMap;
    ProgressDialog progressDialog;

    public List<LatLng> markerPoints = new ArrayList<>();
    public List<LatLng> defaultPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        changeStatusBarColor();

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
        mMap.setBuildingsEnabled(true);

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
                if (TourManager.numberOfCities() >= 9) {
                    Toast.makeText(MainActivity.this, "Cant load more than 9 points", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    TourManager.addCity(new City(point.latitude, point.longitude));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(point);
                    markerOptions.title("City "+ (TourManager.numberOfCities() - 1));

                    if (TourManager.numberOfCities() == 1) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    }

                    mMap.addMarker(markerOptions);
                    defaultPoints.add(point);
                }
            }
        });
    }

    // Event if button DETAIL has been clicked
    public void getDetail() {
        List<Detail> dataDetail = new ArrayList<>();

        for (int i = 0; i < defaultPoints.size(); i++) {
            for (int j = 0; j < markerPoints.size(); j++) {
                if (defaultPoints.get(i).equals(markerPoints.get(j))) {
                    dataDetail.add(new Detail(
                            "City " + i,
                            markerPoints.get(j)
                    ));

                    break;
                }
            }
        }

        for (int i = 0; i < dataDetail.size(); i++) {
            try {
                double distance = Utility.distance(
                        new City(dataDetail.get(i).getLatLng().latitude, dataDetail.get(i).getLatLng().longitude),
                        new City(dataDetail.get(i + 1).getLatLng().latitude, dataDetail.get(i + 1).getLatLng().longitude)
                );

                Toast.makeText(MainActivity.this, dataDetail.get(i).getName() +" to "+ dataDetail.get(i + 1).getName() +" - "+ distance, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                double distance = Utility.distance(
                        new City(dataDetail.get(i).getLatLng().latitude, dataDetail.get(i).getLatLng().longitude),
                        new City(dataDetail.get(0).getLatLng().latitude, dataDetail.get(0).getLatLng().longitude)
                );

                Toast.makeText(MainActivity.this, dataDetail.get(i).getName() +" to "+ dataDetail.get(0).getName() +" - "+ distance, Toast.LENGTH_SHORT).show();
            }
        }

        dataDetail.clear();
    }

    // Event if button RANDOM has been clicked
    public void getRandomLocation(Integer countRandom) {
        // Location default
        double x0 = -7.257931;
        double y0 = 112.757346;

        for (int i = 0; i < countRandom; i++) {
            Random random = new Random();

            // Convert radius from 10 meters to degrees
            double radiusInDegrees = 5000 / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;

            TourManager.addCity(new City(foundLatitude, foundLongitude));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(foundLatitude, foundLongitude));
            markerOptions.title("City "+ (TourManager.numberOfCities() - 1));

            if (TourManager.numberOfCities() == 1) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            mMap.addMarker(markerOptions);
            defaultPoints.add(new LatLng(foundLatitude, foundLongitude));
        }
    }

    // Event if button SOLVED has been clicked
    public void getWaypoints(Tour best) {
        progressDialog.show();

        for (int i = 0; i < best.tourSize(); i++) {
            LatLng latLng = new LatLng(best.getCity(i).getX(), best.getCity(i).getY());
            markerPoints.add(latLng);
        }

        // Get direction
        LatLng origin = (LatLng) markerPoints.get(0);
        LatLng dest = (LatLng) markerPoints.get(0);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        // Start downloading JSon data from Google Directions API
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        progressDialog.hide();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    // A class to parse the Google Places in JSON format
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }

            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0; i<result.size(); i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0; j < path.size(); j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.parseColor("#2196F3"));
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    // Set uri/url direction
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i = 1; i< markerPoints.size(); i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==1)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+ waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    // A method to download json data from url
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
