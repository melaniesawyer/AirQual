package mhs.team.googlemapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class glass_maps extends FragmentActivity {

    public static double latitude = 0.0;
    public static double longitude = 0.0;
    ParseObject spots = new ParseObject("spots");
    public static double latitudeArray[] = new double[1000];
    public static double longitudeArray[] = new double[1000];
    public static int x = 0;

    Location location;
    Location myLocation;

    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void showSimplePopUp() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Location Services Disabled");
        helpBuilder.setMessage("Please go Settings to enable Location Services. \nNote: You may have to wait a few seconds for your location to be found.");
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //getActionBar().setTitle("Glass Bins");
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.airqual_map);
        setUpMapIfNeeded();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);

        location = locationManager.getLastKnownLocation(provider);
        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        // Check location
        Log.e("Location: ", new LatLng(myLocation.getLatitude(), myLocation.getLongitude()).toString());

        for(int i = 0; i < longitudeArray.length; i++) {
            longitudeArray[i] = 20.0;
            latitudeArray[i] = 20.0;
        }

        // Get all of the glass data points from parse

        ParseQuery
                .getQuery("spots")
                .whereEqualTo("type", "AirQual")
                .findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> spots, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < spots.size(); i++) {
                                longitudeArray[i] = spots.get(i).getDouble("longitude");
                                latitudeArray[i] = spots.get(i).getDouble("latitude");
                                x++;
                            }
                            makeMarkers();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });



    }


    public void makeMarkers() {
        for (int i = 0; i < longitudeArray.length; i++) {
            // Set markers
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudeArray[i], longitudeArray[i]))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pinit_resized))
                    .title("Glass recycling bin")
                    .draggable(false));
        }
        //Toast.makeText(getApplicationContext(), String.valueOf(x), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */




    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16.0f));
        if (drawToMap) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .radius(1) // In meters
                    .color(MainActivity.color);
            Circle circle = mMap.addCircle(circleOptions);
            drawToMap = false;
        }
    }
}