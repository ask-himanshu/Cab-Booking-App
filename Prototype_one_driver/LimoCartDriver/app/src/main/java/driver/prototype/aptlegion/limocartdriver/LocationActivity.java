package driver.prototype.aptlegion.limocartdriver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

public class LocationActivity extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,RoutingListener {

    TextView distance_view;
    ArrayList<openbookingdata> trip_details = new ArrayList<>();
    private static final long INTERVAL = 10000 ;
    private static final long FASTEST_INTERVAL = 10000 ;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mInitialLocation;
    String distance;
    double newResult = 0.0;
    java.util.Date noteTS;
    int pos;
    htttpcall htttpcall;
    Button startWaiting, stopWaiting;
    TextView showTime;
    int count = 0;
    JSONObject j;
    SharedPreferences sharedPreferences;
    PowerManager.WakeLock mWakeLock;
    ProgressDialog progressDialog;
    String endlat,endlon,end,Bookingid;
    String accept_url;
    Intent pay;
    float dis;
    String driverbookig;
    Timer T;
    MapView mapView;
    GoogleMap googleMap;
    LatLng scoodinates,dcoordinates;
    double result;
    String TAG = "LocationActivity";
    Polyline polyline;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        mapView = (MapView)findViewById(R.id.nav);
        startWaiting = (Button) findViewById(R.id.btn_Start);
        stopWaiting = (Button) findViewById(R.id.btn_Stop);
        showTime = (TextView) findViewById(R.id.txtview_time);

        startWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 T=new Timer();
                T.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showTime.setText(" "+count);
                                count++;
                            }
                        });
                    }
                }, 1000, 1000);


            }
        });

        stopWaiting.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                T.cancel();

            }
        });


        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,"myTag");
        mWakeLock.acquire();
        Intent trip =  new Intent();
        trip = getIntent();
        pos =trip.getIntExtra("position", 0);
        trip_details=((GlobalVaraible)getApplicationContext()).getAcceptedbooking();
        if(trip.getIntExtra("position",-2)==-1)
        {
            double sl=Double.valueOf(Double.valueOf(trip.getStringExtra("startlat")));
            double slon = Double.valueOf(Double.valueOf(trip.getStringExtra("startlon")));
            scoodinates = new LatLng(sl,slon);
            trip_details=null;
            endlat = trip.getStringExtra("endlat");
            endlon = trip.getStringExtra("endlong");
            double dl= Double.valueOf(endlat);
            double dlon = Double.valueOf(endlon);
            dcoordinates=  new LatLng(dl,dlon);
            end =trip.getStringExtra("end");
            driverbookig ="true";

        }else
        {
          endlat = trip_details.get(pos).DestinationLatitude;
          endlon = trip_details.get(pos).DestinationLongitude;
          end = trip_details.get(pos). destinationAddress;
          double sl=Double.valueOf(trip_details.get(pos).SourceLatitude);
          double slon = Double.valueOf(trip_details.get(pos).SourceLongitude);
          scoodinates = new LatLng(sl,slon);
          double dl= Double.valueOf(trip_details.get(pos).DestinationLatitude);
          double dlon = Double.valueOf(trip_details.get(pos).DestinationLongitude);
          dcoordinates=  new LatLng(dl,dlon);
          driverbookig="false";
        }
        if(mapView!=null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            googleMap=mapView.getMap();
        }
        if(googleMap != null)
        {
            if(scoodinates!= null)
            {
                googleMap.addMarker( new MarkerOptions().position(scoodinates));
                googleMap.addMarker(new MarkerOptions().position(dcoordinates));
                googleMap.setMyLocationEnabled(true);
                Routing routing = new Routing.Builder()
                        .travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(this)
                        .waypoints(scoodinates, dcoordinates)
                        .build();
                routing.execute();
            }

        }
        if (!isGooglePlayServicesAvailable()) {
           Toast.makeText(this,"Google Play services not Avaliable",Toast.LENGTH_LONG).show();
        }
        sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        pos =trip.getIntExtra("position", 0);
        distance_view=(TextView)findViewById(R.id.distance);
        if(trip_details!=null){

            accept_url = "http://demo.olwebapps.com/api/bookings/"+trip_details.get(pos).bookingid.toString()+"?token="+sharedPreferences.getString("token",null)+"";
            Bookingid=trip_details.get(pos).bookingid.toString();

        }else
        {
            accept_url = "http://demo.olwebapps.com/api/bookings/"+trip.getStringExtra("Bookingid")+"?token="+sharedPreferences.getString("token",null)+"";
            Bookingid=trip.getStringExtra("Bookingid");
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
           Toast.makeText(this, "leave", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    public void endtrip(View v){
        progressDialog.setMessage("Ending Trip");
        progressDialog.show();
        Handler end_trip = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage)
            {
                    if(mWakeLock.isHeld()){
                        mWakeLock.release();

                    if(inputMessage.what==1)
                    {
                        Bundle b = inputMessage.getData();
                        try{
                            JSONObject json = new JSONObject(b.getString("message"));
                            JSONObject data = new JSONObject(json.getString("data"));
                            pay = new Intent(LocationActivity.this,Paymentinfo.class);
                            pay.putExtra("amount",data.getString("amount"));
                            pay.putExtra("unitprice",data.getString("unitprice"));
                            pay.putExtra("distance",data.getString("distance"));
                            pay.putExtra("minimum_fare",data.getString("minimum_fare"));
                            pay.putExtra("minimum_dist",data.getString("minimum_dist"));
                            pay.putExtra("Bookingid",Bookingid);
                            pay.putExtra("driverbookig",driverbookig);


                        }catch (Exception ex)
                        {
                            Log.d("exception_Trip",""+ex.toString());

                        }

                        progressDialog.dismiss();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(LocationActivity.this);
                        dialog.setMessage("Trip Completed");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                      /*  Intent main = new Intent(LocationActivity.this, MainActivity.class);
                                        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(main);
                                        ArrayList<openbookingdata> list,list1,list2 = new ArrayList<openbookingdata>();
                                        list=((GlobalVaraible)getApplicationContext()).getAcceptedbooking();
                                        list1=((GlobalVaraible)getApplicationContext()).getBookingdata();
                                        list2=((GlobalVaraible)getApplicationContext()).getAllbookings();
                                        acceptedbookinglist_adapter a = new acceptedbookinglist_adapter(list);
                                        allbookingsadapter ab = new allbookingsadapter(list2);
                                        mycard m = new mycard(list1);
                                        m.notifyDataSetChanged();
                                        a.notifyDataSetChanged();
                                        ab.notifyDataSetChanged();
                                        finish();*/
                                if(pay!= null){
                                    startActivity(pay);
                                    finish();

                                }


                            }
                        });
                        dialog.show();

                    }
                    if(inputMessage.what==0)
                    {
                        Bundle b = inputMessage.getData();

                        AlertDialog.Builder dialog = new AlertDialog.Builder(LocationActivity.this);
                        dialog.setMessage(b.get("message").toString());
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                    }
                }
                if(inputMessage.what ==0)
                {
                    progressDialog.dismiss();
                    Bundle b = inputMessage.getData();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LocationActivity.this);
                    dialog.setTitle("Error Completing Trip");
                    dialog.setMessage(b.get("message").toString());
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        };
        htttpcall = new htttpcall(LocationActivity.this,end_trip,accept_url);
        HashMap<String,String> a = new HashMap<>();
        a.put("end_lat",endlat);
        a.put("end_long",endlon);
        a.put("completed_at",end);
        Calendar mcurrentDate = Calendar.getInstance();
        int m = mcurrentDate.get(Calendar.MONTH)+1;
        a.put("endtime",""+mcurrentDate.get(Calendar.YEAR)+"-"+m+"-"+mcurrentDate.get(Calendar.DATE)+" "+mcurrentDate.get(Calendar.HOUR_OF_DAY)+":"+mcurrentDate.get(Calendar.MINUTE));
        a.put("distance", String.valueOf(dis));
        Log.e(TAG,"distance");
        a.put("status", "Completed");
        try
        {
            j = new JSONObject(a);
        }catch (Exception ex){};
        htttpcall.put(j);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Please end Trip to go back",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

          }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mInitialLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
       mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(LocationActivity.this,connectionResult.toString(),Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
           }

    @Override
    public void onLocationChanged(Location location) {
        if(mInitialLocation != null) {


             result = CalculationByDistance(mInitialLocation.getLatitude(), mInitialLocation.getLongitude(), location.getLatitude(), location.getLongitude());


            //String latitudeOld = "LatitudeOld: "+mInitialLocation.getLatitude();
         //   String longitudeOld = "LongitudeOld: "+mInitialLocation.getLongitude();
           // Toast.makeText(getApplicationContext(), latitudeOld + "\n"+ longitudeOld + "\n", Toast.LENGTH_LONG).show();
           /** double source_lat = mInitialLocation.getLatitude();
            double source_lon = mInitialLocation.getLongitude();
            double dest_lat = location.getLatitude();
            double dest_lon = location.getLongitude();  */
           // Toast.makeText(getApplicationContext(), "result is:" +result,Toast.LENGTH_SHORT).show();
            //    if(result>0) {

             newResult = result + newResult;
              dis = (float) newResult;

                distance=String.valueOf(dis);

            mInitialLocation = location;
         //   source_lat = dest_lat;
        //    source_lon = dest_lon;
            DecimalFormat twoFForm = new DecimalFormat("#.#");
            dis = Float.valueOf(twoFForm.format(dis));
            distance_view.setText(String.valueOf(dis));

          //  String latitude = "NewLatitude: "+location.getLatitude();
           // String longitude = "NewLongitude: "+location.getLongitude();
            // speed.setText((String.valueOf(location.getSpeed())));
       // }
      // Toast.makeText(getApplicationContext(), latitude + "\n" + longitude + "\nDistance is:" + newResult, Toast.LENGTH_LONG).show();
        }else
        {
            if(mGoogleApiClient.isConnected())
            {
                mInitialLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }else
            {
                mGoogleApiClient.connect();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();

        }
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }


    @Override
    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(android.R.color.holo_red_dark));
        polyOptions.width(10);
        polyOptions.addAll(polylineOptions.getPoints());
        polyline=googleMap.addPolyline(polyOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(scoodinates).zoom(17f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onRoutingCancelled() {

    }
    private double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {

        double EARTH_RADIUS = 6371.00;

        double Radius = EARTH_RADIUS;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(dLon/2)) * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.asin(Math.sqrt(a));
        return Math.abs(Radius * c);


    }


}