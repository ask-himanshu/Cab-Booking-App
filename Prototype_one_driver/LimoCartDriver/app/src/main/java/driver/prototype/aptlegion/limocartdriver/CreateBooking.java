package driver.prototype.aptlegion.limocartdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class CreateBooking extends AppCompatActivity {

    EditText email,phoneno;
    AutoCompleteTextView from,to_place;
    String url;
    SharedPreferences sharedPreferences,pref;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    Geocoder geo;
    Address ad,adf;
    Set<String> Cities = new HashSet<>()  ;
    String Bookingid,From;
    HashMap<String,String>user = new HashMap<>();
    Location location = null;
    Button button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        button = (Button)findViewById(R.id.button4);
        user = ((GlobalVaraible)getApplicationContext()).getUserinfo();
        location = ((GlobalVaraible)getApplicationContext()).getLocation();
        geo = new Geocoder(this);
        progressDialog = new ProgressDialog(this);
        dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        progressDialog.setMessage("Booking Cab...");
        progressDialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        pref = getApplicationContext().getSharedPreferences("MyPrefs_driver1", MODE_PRIVATE);
        Cities = pref.getStringSet("Cities",null);
        url ="http://demo.olwebapps.com/api/bookings?token={"+sharedPreferences.getString("token","")+"}";
        email = (EditText)findViewById(R.id.email);
        phoneno = (EditText)findViewById(R.id.phoneno);
        from = (AutoCompleteTextView)findViewById(R.id.from);
        to_place = (AutoCompleteTextView)findViewById(R.id.to);
        from.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.auto_complete));
        to_place.setAdapter(new GooglePlacesAutocompleteAdapter(this,R.layout.auto_complete));
        from.setEnabled(false);
        if(location !=null)
        {
            if (geo.isPresent())
            {
                try
                {
                    List<Address> list = geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if (list.size() > 0) {
                        Address address = list.get(0);
                        if(Cities.contains(address.getLocality()))
                        {
                            ad = list.get(0);
                            ArrayList<String> addressFragments = new ArrayList<String>();
                            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                addressFragments.add(address.getAddressLine(i));
                            }
                            From =  TextUtils.join(System.getProperty("line.separator"), addressFragments);
                            from.setText(From);
                        }else
                        {
                            from.setText("Service Not Avaliable in City");
                            to_place.setText("Service Not Avaliable in City");
                            button.setEnabled(false);
                        }
                    }
                }catch (Exception ex)
                {
                    Log.d("Error", ex.getMessage());
                }
            }
        }
        from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(from.getWindowToken(), 0);
                from.setText(str);
                try {

                    if (geo.isPresent()) {
                        List<Address> list = geo.getFromLocationName(str, 1);
                        if (list.size() > 0) {
                            Address address = list.get(0);
                            if(Cities.contains(address.getLocality()))
                            {
                                if(address.hasLatitude()&&address.hasLongitude())
                                {
                                    ad = list.get(0);
                                }else {
                                    Toast.makeText(CreateBooking.this, "No Coordinated avaliable for this location Please select a nearby Location", Toast.LENGTH_LONG).show();
                                    from.setText("");
                                    adf = null;
                                }

                            }else
                            {
                                from.setText("");
                                dialog.setMessage("Service Not Available in selected Zone");
                                dialog.setPositiveButton("ok",null);
                                dialog.show();
                            }

                        } else {
                            Toast.makeText(CreateBooking.this,"No Coordinated avaliable for this location Please select a nearby Location",Toast.LENGTH_LONG).show();
                            from.setText("");
                            adf = null;
                        }

                    }
                } catch (IOException e) {
                    Log.d("Error", e.getMessage());
                }

            }
        });
        to_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(to_place.getWindowToken(), 0);
                to_place.setText(str);
                try {

                    if (geo.isPresent()) {
                        List<Address> list = geo.getFromLocationName(str, 1);
                        try {
                            if (list.size() > 0) {
                                Address address = list.get(0);
                                if(Cities.contains(address.getLocality()))
                                {
                                    if(address.hasLongitude()&&address.hasLatitude())
                                    {
                                        adf = list.get(0);
                                    }else
                                    {
                                        to_place.setText("");
                                        Toast.makeText(CreateBooking.this, "No Coordinated avaliable for this location Please select a nearby Location", Toast.LENGTH_LONG).show();
                                        adf = null;
                                    }
                                }else
                                {
                                    to_place.setText("");
                                    dialog.setMessage("Service Not Available in selected Zone");
                                    dialog.setPositiveButton("ok",null);
                                    dialog.show();
                                }

                            } else {
                                to_place.setText("");
                                Toast.makeText(CreateBooking.this, "No Coordinated avaliable for this location Please select a nearby Location", Toast.LENGTH_LONG).show();
                                adf = null;
                            }
                        } catch (Exception ex) {
                            Log.d("error", "" + ex.toString());
                        }


                    }
                } catch (IOException e) {
                    Log.d("Error", e.getMessage());
                }
            }
        });
    }

    public void bookcab(View view)
    {
        if(!(email.getText().toString().equals("")))
        {
          if(!(phoneno.getText().toString().equals("")))
          {
            /*  if(!(from.getText().toString().equals("")))
              {*/
                  if(!(to_place.getText().toString().equals(""))) {
                      HashMap<String, String> params = new HashMap<>();
                      params.put("city", ad.getLocality());
                      params.put("country", ad.getCountryName());
                      params.put("vehicletype", user.get("vehicletype"));
                      params.put("customer_email", email.getText().toString().trim().toLowerCase());
                      params.put("customer_phone", phoneno.getText().toString().trim());
                      params.put("source_lat", String.valueOf(ad.getLatitude()));
                      params.put("source_long", String.valueOf(adf.getLongitude()));
                      params.put("dest_lat", String.valueOf(adf.getLatitude()));
                      params.put("dest_long", String.valueOf(adf.getLongitude()));
                      params.put("source", from.getText().toString().trim());
                      params.put("destination", to_place.getText().toString().trim());
                      JSONObject jsonObject = new JSONObject(params);
                      Handler bookcab = new Handler(Looper.getMainLooper()) {
                          @Override
                          public void handleMessage(Message message) {
                              if (message.what == 1) {
                                  progressDialog.dismiss();
                                  progressDialog.setMessage("Starting Trip...");
                                  progressDialog.show();
                                  Bundle b = message.getData();
                                  try {
                                      JSONObject json = new JSONObject(b.getString("message", "").toString());
                                      String t = json.getString("data");
                                      JSONObject id = new JSONObject(t);
                                      Bookingid = id.getString("bookingid");

                                  } catch (Exception ex) {
                                      Log.d("Createbooking exception", "" + ex.toString());
                                  }


                                  Intent starttrip = new Intent(CreateBooking.this, LocationActivity.class);
                                  Calendar mcurrentDate = Calendar.getInstance();
                                  starttrip.putExtra("Bookingid", Bookingid);
                                  starttrip.putExtra("position", -1);
                                  starttrip.putExtra("start", from.getText().toString());
                                  starttrip.putExtra("endlat", String.valueOf(adf.getLatitude()));
                                  starttrip.putExtra("endlong", String.valueOf(adf.getLongitude()));
                                  starttrip.putExtra("startlat", String.valueOf(ad.getLatitude()));
                                  starttrip.putExtra("startlon", String.valueOf(ad.getLongitude()));

                                  starttrip.putExtra("end", to_place.getText().toString());
                                  starttrip.putExtra("starttime", "" + mcurrentDate.get(Calendar.YEAR) + "-" + ((mcurrentDate.get(Calendar.MONTH)) + 1) + "-" + mcurrentDate.get(Calendar.DATE) + " " + mcurrentDate.get(Calendar.HOUR_OF_DAY) + ":" + mcurrentDate.get(Calendar.MINUTE));
                                  startActivity(starttrip);


                              }
                              if (message.what == 0) {
                                  progressDialog.dismiss();
                                  Bundle b = message.getData();
                                  dialog.setMessage(b.get("message").toString());
                                  dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                        email.setText("");
                                        phoneno.setText("");
                                        to_place.setText("");
                                        email.requestFocus();

                                      }
                                  });
                                  dialog.show();

                              }
                          }
                      };
                      progressDialog.show();
                      htttpcall htttpcall = new htttpcall(context,bookcab,url);
                      htttpcall.post(jsonObject.toString());

                  }
             /* }else
              {
                  YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.from));
              }*/
          }else
          {
              YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.phoneno));
          }
        }else
        {
            YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.email));

        }
    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this,MainActivity.class);
       // main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_booking, menu);
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
}
