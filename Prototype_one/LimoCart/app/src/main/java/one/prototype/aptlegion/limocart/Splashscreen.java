package one.prototype.aptlegion.limocart;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

public class Splashscreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    String username,password;
    SharedPreferences pref,pref1;
    SharedPreferences.Editor edit,edit1;
    Handler handler;
    Htttpcall http;
    AlertDialog.Builder dialog;
    Animation animation;
    private GoogleApiClient  googleApiClient;
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
        TextView textView = (TextView)findViewById(R.id.stext);
        ImageView imageView = (ImageView)findViewById(R.id.car);
        ImageView imageView2 = (ImageView)findViewById(R.id.car1);
        pref= getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        pref1 = getApplicationContext().getSharedPreferences("Companyname", MODE_PRIVATE);
        edit1 = pref1.edit();
        edit= pref.edit();
        edit1.putString("companyname", "ZipZap");
        edit1.commit();
        username=pref.getString("Username", null);
        password=pref.getString("Password", null);
        ImageView logo =(ImageView)findViewById(R.id.imageView2);
        animation = AnimationUtils.loadAnimation(this,R.anim.left_right);
        logo.startAnimation(animation);
        textView.startAnimation(animation);
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.inderteminate);
        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.inderteminate_right_to_left);
        imageView.startAnimation(animation1);
        imageView2.startAnimation(animation2);

    }
    @Override
    public void onStart()
    {   super.onStart();
        if(Globalclass.getInstance(Splashscreen.this).isgpsenabled())
        {
            if(Globalclass.getInstance(Splashscreen.this).isnetworking())
            {
                int checkgoogleservises = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Splashscreen.this);
                if(checkgoogleservises == ConnectionResult.SUCCESS)
                {
                 googleApiClient.connect();
                }else
                {
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(checkgoogleservises, Splashscreen.this, 10);
                    dialog.show();
                }

            }else
            {
                Intent reload = new Intent(this,ErrorActivity.class);
                reload.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reload);
            }
        }
        else{
            Intent reload = new Intent(this,GPS_falil.class);
            reload.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(reload);
            }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splashscreen, menu);
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
        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(mylocation != null)
        {
            Geocoder geocoder = new Geocoder(Splashscreen.this);
            if(geocoder.isPresent())
            {
                try {
                    List<Address> add = geocoder.getFromLocation(mylocation.getLatitude(),mylocation.getLongitude(),1);
                    Address address = add.get(0);
                   City =address.getLocality();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Handler compnayinfo = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
                if(message.what ==1)
                {
                   Bundle b = message.getData();
                   HashMap<String,ArrayList<Vehicle>> info = (HashMap<String, ArrayList<Vehicle>>) b.getSerializable("info");
                   Set cities = info.keySet();
                       ArrayList<Vehicle> Vehicles = info.get(City);
                       edit1.putString("VehicleCity", ObjectSerializer.serialize(info));
                       edit1.remove("Vehicles");
                       edit.commit();
                       edit1.putString("Vehicles",ObjectSerializer.serialize(Vehicles));
                       edit1.commit();
                       edit1.putStringSet("Cities",cities);
                       edit1.commit();
                       ((GlobalVaraible) getApplicationContext()).setMylocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                       if(!(pref.getString("rememberme","false").equals("true")&&username != null&&password != null)){
                           Globalclass.getInstance(Splashscreen.this).startactivity(Login.class);
                       }else
                       {
                           HashMap<String,String>params = new HashMap<>();
                           params.put("company", pref1.getString("companyname",""));
                           params.put("loginid", username.trim().toLowerCase());
                           params.put("password", password);
                           params.put("type", "Customer");
                           JSONObject json = new JSONObject(params);
                           Handler handler = new Handler(Looper.getMainLooper()) {
                               @Override
                               public void handleMessage(Message inputMessage) {

                                   if (inputMessage.what == 1) {
                                       Bundle b = inputMessage.getData();
                                       try {
                                           String message = b.getString("message");
                                           JSONObject js = new JSONObject(message);
                                           edit.remove("token").commit();
                                           edit.putString("token", js.getString("token"));
                                           edit.commit();
                                           ((GlobalVaraible)getApplicationContext()).setToken(js.getString("token"));
                                           if (Globalclass.getInstance(Splashscreen.this).isgpsenabled() && Globalclass.getInstance(Splashscreen.this).isgpsenabled()) {
                                               Handler start = new Handler(Looper.getMainLooper())
                                               {
                                                   @Override
                                                   public void handleMessage(Message inputMessage)
                                                   {
                                                       if(inputMessage.what==1)
                                                       {
                                                           HashMap<String,String>userinfo= new HashMap<>();
                                                           userinfo=((GlobalVaraible)getApplicationContext()).getUserinfo();
                                                           edit.putString("firstname", userinfo.get("firstname"));
                                                           edit.putString("lastname", userinfo.get("lastname"));
                                                           edit.commit();

                                                           if(LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null)
                                                           {
                                                               Globalclass.getInstance(Splashscreen.this).startactivity(MainActivity.class);
                                                           }else
                                                           {
                                                               Intent reload = new Intent(Splashscreen.this,GPS_falil.class);
                                                               reload.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                               startActivity(reload);
                                                           }

                                                       }
                                                       if (inputMessage.what==0)
                                                       {
                                                           Bundle b = inputMessage.getData();
                                                           dialog = new android.support.v7.app.AlertDialog.Builder(Splashscreen.this);
                                                           dialog.setTitle("Failed ");
                                                           dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   Globalclass.getInstance(Splashscreen.this).startactivity(Login.class);
                                                               }
                                                           });
                                                           dialog.setMessage(b.getString("message"));
                                                           dialog.show();
                                                       }

                                                   }
                                               };
                                               Getdata.startAction(Splashscreen.this, "getcustomerdata", pref.getString("token", ""), start);

                                           } else {
                                               Toast.makeText(Splashscreen.this, "Please Enable your Gps and Internet", Toast.LENGTH_SHORT).show();
                                           }

                                       } catch (JSONException e) {
                                           Log.d("res", e.toString());
                                       }
                                   }
                                   if (inputMessage.what == 0) {
                                       Bundle b = inputMessage.getData();
                                       dialog = new android.support.v7.app.AlertDialog.Builder(Splashscreen.this);
                                       dialog.setTitle("Failed ");
                                       dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               Globalclass.getInstance(Splashscreen.this).startactivity(Login.class);
                                           }
                                       });
                                       dialog.setMessage(b.getString("message"));
                                       dialog.show();

                                   }

                               }
                           };
                           Htttpcall http = new Htttpcall(Splashscreen.this, handler, "http://creatg.webserversystems.com/api/login");
                           http.post(json.toString());
                       }

                }
            }
        };
        Getdata getdata = new Getdata();
        getdata.getcompanyifo(Splashscreen.this, "getcartypes", pref1.getString("companyname", ""), compnayinfo);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
