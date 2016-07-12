package driver.prototype.aptlegion.limocartdriver;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class splashscreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    ImageView logo;
    String username,password;
    SharedPreferences pref,pref1 ;
    HashMap<String,String>params = new HashMap<>();
    SharedPreferences.Editor editor,editor1;
    getdata getdata;
    ProgressBar progressBar;
    TextView internet,gps;
    AlertDialog.Builder dialog;
    GoogleApiClient googleApiClient;
    Location mylocation;
    String City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        pref=getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        pref1= getApplicationContext().getSharedPreferences("MyPrefs_driver1", MODE_PRIVATE);
        editor1 = pref1.edit();
        editor1.putString("Company","ZipZap");
        editor1.commit();
        editor= pref.edit();
        editor.putString("Company","ZipZap");
        editor.commit();
        username=pref.getString("Username", null);
        password=pref.getString("Password", null);
        logo =(ImageView)findViewById(R.id.logo);
        internet=(TextView)findViewById(R.id.inernet);
        gps=(TextView)findViewById(R.id.GPS);
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.left_right);
        logo.startAnimation(ani);

    }
    @Override
    public  void onStart(){
        super.onStart();
        int Check = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(Check == ConnectionResult.SUCCESS)
        {
            if(Globalclass.getInstance(this).isnetworking())
            {
                if(Globalclass.getInstance(this).isgpsenabled()){
                    googleApiClient.connect();
                }
                else{
                    Toast.makeText(this,"Please Set the GPS to High Power Mode",Toast.LENGTH_LONG).show();
                    Intent nointernet  = new Intent(this,GPS_falil.class);
                    startActivity(nointernet);

                }
            } else {
                Intent nointernet  = new Intent(this,ErrorActivity.class);
                startActivity(nointernet);
            }
        }else{
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(Check, splashscreen.this, 10);
            dialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
       Handler compnayinfo = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    Bundle b = message.getData();
                    HashMap<String, ArrayList<Vehicle>> info = (HashMap<String, ArrayList<Vehicle>>) b.getSerializable("info");
                    editor1.putString("VehicleCity", ObjectSerializer.serialize(info));
                    editor1.commit();
                    Set cities = info.keySet();
                    editor1.putStringSet("Cities", cities);
                    editor1.commit();
                        ((GlobalVaraible)getApplicationContext()).setLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                        int checkplayservices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(splashscreen.this);
                        if(checkplayservices == ConnectionResult.SUCCESS)
                        {
                            if(!(pref.getString("rememberme","false").equals("true")&& username != null || password != null)){
                                Intent login = new Intent(splashscreen.this,Login.class);
                                startActivity(login);
                            }
                            else
                            {
                                params.put("company",pref1.getString("Company",null));
                                params.put("loginid",username.trim().toLowerCase());
                                params.put("password",password);
                                params.put("type","Driver");
                                JSONObject json = new JSONObject(params);
                                Handler handler = new Handler(Looper.getMainLooper()){
                                    @Override
                                    public void handleMessage(Message inputMessage)
                                    {
                                        if(inputMessage.what ==1){
                                            editor.remove("token").commit();
                                            Bundle b = inputMessage.getData();
                                            try {
                                                String message = b.getString("message");
                                                JSONObject js = new JSONObject(message);
                                                editor.putString("token", js.getString("token"));
                                                editor.commit();
                                                ((GlobalVaraible)getApplicationContext()).setToken(js.getString("token"));
                                                Handler handler1 = new Handler(Looper.getMainLooper())
                                                {
                                                    @Override
                                                    public void handleMessage(Message inputMessage)
                                                    {
                                                        if(inputMessage.what ==1){
                                                            HashMap<String,String>user ;
                                                            user=((GlobalVaraible)getApplicationContext()).getUserinfo();
                                                            editor.putString("firstname",user.get("firstname"));
                                                            editor.putString("lastname", user.get("lastname"));
                                                            editor.commit();
                                                            updateposition.updatepostion(splashscreen.this);
                                                            Intent main = new Intent(splashscreen.this,MainActivity.class);
                                                            startActivity(main);
                                                        }
                                                        if(inputMessage.what ==0)
                                                        {
                                                            Bundle b = inputMessage.getData();
                                                            dialog = new AlertDialog.Builder(splashscreen.this);
                                                            dialog.setMessage(b.getString("message").toString());
                                                            dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Globalclass.getInstance(splashscreen.this).callactivity("Logout");
                                                                }
                                                            });
                                                        }

                                                    }
                                                };
                                                Refreshdata r = new Refreshdata(splashscreen.this,handler1);
                                                r.refreshdata();

                                            } catch (JSONException e) {
                                                Log.d("res", e.toString());
                                            }
                                        }
                                        if(inputMessage.what ==0)
                                        {
                                            Bundle b = inputMessage.getData();
                                            dialog = new AlertDialog.Builder(splashscreen.this);
                                            dialog.setMessage(b.getString("message").toString());
                                            dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Globalclass.getInstance(splashscreen.this).startactivity(Login.class);
                                                }
                                            });

                                        }

                                    }
                                };
                                htttpcall http = new htttpcall(splashscreen.this,handler,"http://demo.olwebapps.com/api/login");
                                http.post(json.toString());
                            }
                        }else
                        {
                            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(checkplayservices, splashscreen.this, 10);
                            dialog.show();
                        }


                }
            }
        };
        Message message = compnayinfo.obtainMessage();
        getdata.getcompanyinfo(this,"",message,pref1.getString("Company",""));




    }






    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
