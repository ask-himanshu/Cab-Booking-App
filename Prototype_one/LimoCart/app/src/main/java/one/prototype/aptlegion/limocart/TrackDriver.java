package one.prototype.aptlegion.limocart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapView;

import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.PolylineOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.DecimalFormat;


public class TrackDriver extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    Double lat,lon,lat1,lon1;
    Context context;
    String BookingID;
    GoogleMap gMap;
    MapView mapView;
    LatLng latLng = null;
    Handler handler;
    String accept_url;
    SharedPreferences sharedPreferences,pref2;
    Handler updatedriver;
    double test = 0;
    Runnable runnable;
    Message m ;
    MarkerOptions markerOptions ;
    Marker marker;
    PolylineOptions polylineOptions;
    LocationManager locationManager;
    GoogleApiClient googleApiClient;
    Location location;
    float distance;
    int time,ETA;
    float speed = 30;
    LatLng mylocation;
    TextView timedis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerOptions= new MarkerOptions();
        setContentView(R.layout.activity_track_driver);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        handler = new Handler();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        mapView = (MapView)findViewById(R.id.map_view_booking);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            gMap=mapView.getExtendedMap();
        }
        gMap.setClustering(new ClusteringSettings().addMarkersDynamically(true));
        gMap.setMyLocationEnabled(true);
        Intent intent = new Intent();
        intent= getIntent();
        lat = Double.valueOf(intent.getStringExtra("lat"));
        lon = Double.valueOf(intent.getStringExtra("lon"));
        latLng = new LatLng(lat,lon);
        BookingID= intent.getStringExtra("Bookingid");
        timedis = (TextView) findViewById(R.id.displayTime);

    }
    @Override
    public void onStart()
    {
        super.onStart();
        if(!(googleApiClient.isConnected()))
        {
            googleApiClient.connect();
        }else
        {
            location= LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }





    }
    @Override
    public void onResume()
    {
        super.onResume();
        updatedriver = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
                m= message;
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(m.what==1){
                            //  Toast.makeText(TrackDriver.this,"tracking..."+String.valueOf(lat), Toast.LENGTH_SHORT).show();
                        }
                        Bundle b = m.getData();
                        try {
                            JSONObject jsonObject = new JSONObject(b.getString("message"));
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            lon = Double.valueOf(data.getString("longitude"));
                            lat = Double.valueOf(data.getString("latitude"));
                            latLng = new LatLng(lat,lon);
                            distance = (float) CalculationByDistance(lat1,lon1,lat,lon);
                            time = (int) (distance/speed);
                            ETA = time*60;
                            DecimalFormat twoFForm = new DecimalFormat("#.#");

                            distance = Float.valueOf(twoFForm.format(distance));
                           timedis.setText("Distance is " + distance + " km" + "\nTime is " + ETA + " min");
                          //  Toast.makeText(TrackDriver.this,"ETA is "+ETA+" min"+"\n distance is "+distance+" km",Toast.LENGTH_SHORT).show();
                            // test++;
                            //  LatLng testl = new LatLng(test,lon);
                            // gMap.clear();
                            //gMap.setClustering(new ClusteringSettings().addMarkersDynamically(true));
                            //gMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(latLng));
                            marker.setPosition(latLng);
                            polylineOptions.add(latLng);
                            gMap.addPolyline(polylineOptions);
                            //  Toast.makeText(TrackDriver.this,"tracking..."+String.valueOf(testl), Toast.LENGTH_SHORT).show();
                            //  CameraPosition cameraPosition = new CameraPosition.Builder().target(testl).build();
                            //  gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            accept_url = "http://demo.olwebapps.com/api/location/"+BookingID+"?token="+sharedPreferences.getString("token",null)+"";
                            Htttpcall htttpcall = new Htttpcall(TrackDriver.this,updatedriver,accept_url);
                            htttpcall.get();
                        }catch (Exception ex) {
                            Log.d("MapError", "" + ex);
                        }
                    }
                };
                runnable.run();

            }
        };

        accept_url = "http://demo.olwebapps.com/api/location/"+BookingID+"?token="+sharedPreferences.getString("token",null)+"";
        Htttpcall htttpcall = new Htttpcall(TrackDriver.this,updatedriver,accept_url);
        htttpcall.get();




    }

    private double CalculationByDistance(Double lat2, Double lon2, Double lat3, Double lon3) {


        double EARTH_RADIUS = 6371.00;

        double Radius = EARTH_RADIUS;

        double dLat = Math.toRadians(lat3 - lat2);
        double dLon = Math.toRadians(lon3 - lon2);

        double a = Math.sin(dLat/3) * Math.sin(dLat/3) + Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(dLon/3)) * Math.sin(dLon/3) * Math.sin(dLon/3);

        double c = 2 * Math.asin(Math.sqrt(a));
        return  Math.abs(Radius * c);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        updatedriver.removeCallbacksAndMessages(runnable);
        Intent intent = new Intent(TrackDriver.this, MyBookings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
    @Override
    public void onPause()
    {
        super.onPause();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location= LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location == null)
        {
            location =((GlobalVaraible) context.getApplicationContext()).getMylocation();
        }
         mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        lat1 = location.getLatitude();
        lon1 = location.getLongitude();
       if(mylocation !=null)
       {
           markerOptions.position(mylocation);
           marker = gMap.addMarker(markerOptions);
           marker.setVisible(true);
           CameraPosition cameraPosition = new CameraPosition.Builder().target(mylocation).zoom(17f).build();
           marker.setPosition(mylocation);
           gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

       }else{
           Toast.makeText(TrackDriver.this,"Location is null",Toast.LENGTH_SHORT).show();
       }
        if(latLng!=null)
        {
            markerOptions.position(latLng);
            marker= gMap.addMarker(markerOptions);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(17f).build();
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            test = lat;
            polylineOptions = new PolylineOptions();
            polylineOptions.width(6);
            polylineOptions.add(latLng);
            gMap.addPolyline(polylineOptions);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public void locatecmaera(LatLng pos)
    {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos).zoom(17f).build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
