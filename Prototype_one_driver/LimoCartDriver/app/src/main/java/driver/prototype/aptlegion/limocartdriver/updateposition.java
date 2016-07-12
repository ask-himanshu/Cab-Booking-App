package driver.prototype.aptlegion.limocartdriver;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.sql.StatementEvent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class updateposition extends IntentService implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
    static Context mContext;
    static  MainActivity mainActivity;
    Handler handler;
    Runnable update;
    GoogleApiClient mGoogle;
    Location mlocation;
    String url;
    SharedPreferences sharedPreferences;
    Message msg;
    RequestQueue requestQueue;
    Handler mhandler;
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    public static void updatepostion(Context context) {
        mContext =context;
        Intent intent = new Intent(context, updateposition.class);
        intent.setAction("updateposition");
        context.startService(intent);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public updateposition() {
        super("updateposition");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            mGoogle = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogle.connect();

         }
    }


    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogle, mLocationRequest,this);

       // Toast.makeText(mContext,"Location Connected",Toast.LENGTH_SHORT).show();
        mlocation = LocationServices.FusedLocationApi.getLastLocation(mGoogle);
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        mhandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
                if(message.what==1) {
                   // Toast.makeText(mContext, "Location Updated" + String.valueOf(mlocation.getLatitude()), Toast.LENGTH_SHORT).show();
                }

            }
        };
        url = "http://demo.olwebapps.com/api/location?token="+sharedPreferences.getString("token",null)+"";
        HashMap<String,String> params = new HashMap<>();
        params.put("latitude", String.valueOf(mlocation.getLatitude()));
        params.put("longitude", String.valueOf(mlocation.getLongitude()));
        JSONObject jsonObject = new JSONObject(params);
        htttpcall htttpcall = new htttpcall(mContext,mhandler,url);
        htttpcall.post(jsonObject.toString());


    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(mContext,"Location Changed",Toast.LENGTH_SHORT).show();
        mlocation=location;
        url = "http://demo.olwebapps.com/api/location?token="+sharedPreferences.getString("token",null)+"";
        HashMap<String,String> params = new HashMap<>();
        params.put("latitude", String.valueOf(mlocation.getLatitude()));
        params.put("longitude", String.valueOf(mlocation.getLongitude()));
        JSONObject jsonObject = new JSONObject(params);
        htttpcall htttpcall = new htttpcall(mContext,mhandler,url);
        htttpcall.post(jsonObject.toString());

    }
}
