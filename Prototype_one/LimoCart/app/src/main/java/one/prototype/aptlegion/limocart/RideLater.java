package one.prototype.aptlegion.limocart;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.GooglePlacesAutocompleteAdapter;


/**
 * Created by Pragadees Waran on 18-05-2015.
 */
public class RideLater extends Fragment {
    TextView date,time;
    Context context;
    RadioGroup radioGroup;
    Fragment fr;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    int mYear,mMonth,mDay;
    int hour, min;
    String AM;
    AutoCompleteTextView from,toplace;
    Geocoder geo;
    Button book,c1,c2;
    private Boolean valdidate=false;
    Address ad,adf;
    Handler handler;
    Htttpcall http;
    String url;
    android.support.v7.app.AlertDialog.Builder dialog ;
    ProgressDialog progressDialog;
    String token;
    HashMap<String,String>params = new HashMap<>();
    String date_booking,time_booking,vehicleid="";
    SharedPreferences sharedPreferences,pref1;
    Date currentdate,selecteddate;
    Spinner spinner;
    List<String>cars = new ArrayList<>();
    Set Cities ;
    String Vehiclename="";
    ArrayList<Vehicle> vehicles1 = new ArrayList<>();
    Address a ;
    @Override
    public View onCreateView(final LayoutInflater inflater,  final ViewGroup container,  Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.ridelater, container, false);
        context=v.getContext();
        sharedPreferences =context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        pref1=context.getApplicationContext().getSharedPreferences("Companyname",context.MODE_PRIVATE);
        Cities =pref1.getStringSet("Cities",null);
        token = sharedPreferences.getString("token",null);
        url ="http://creatg.webserversystems.com/api/bookings?token={"+token+"}";
        Log.d("token", "" + token);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait while we Book your Cab");
        progressDialog.setTitle("Booking");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
        dialog =  new android.support.v7.app.AlertDialog.Builder(context);
        book = (Button)v.findViewById(R.id.button5);
        spinner = (Spinner)v.findViewById(R.id.cartype);
        try
        {
            vehicles1 = new ArrayList<Vehicle>();
            vehicles1 = (ArrayList<Vehicle>) ObjectSerializer.deserialize(pref1.getString("Vehicles",null));
            Log.d("test",""+vehicles1.toString());

        }catch (Exception e)
        {

        }
        if(vehicles1 != null)
        {
            for(Vehicle veh : vehicles1)
            {
                cars.add(veh.Vehicletype);
            }
        }

        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,cars);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);
        geo = new Geocoder(context);
        date= (TextView)v.findViewById(R.id.date);
        time =(TextView)v.findViewById(R.id.time);
        from=(AutoCompleteTextView)v.findViewById(R.id.from);
        toplace=(AutoCompleteTextView)v.findViewById(R.id.to);
        from.setAdapter(new GooglePlacesAutocompleteAdapter(context,R.layout.auto_complete));
        toplace.setAdapter(new GooglePlacesAutocompleteAdapter(context,R.layout.auto_complete));
        Calendar mcurrentDate = Calendar.getInstance();
        mYear=mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        min = mcurrentDate.get(Calendar.MINUTE);
        date.setText("" + mDay + "/" + (mMonth+1) + "/" + mYear);
        time.setText("" + hour + ":" + min);
        context=v.getContext();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Vehiclename = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        date.setText("" + selectedday + "-" + (selectedmonth + 1) + "-" + selectedyear);
                        date_booking = selectedyear + "-" + (selectedmonth + 1) + "-" + selectedday;


                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });
        if(date_booking == null){
            date_booking =  mYear + "-" + (mMonth+1) + "-" + mDay;

        }
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText("" + hourOfDay + ":" + minute + "");
                        time_booking = hourOfDay + ":" + minute;


                    }
                }, hour, min, false);

                timePickerDialog.show();

            }
        });
        if(time_booking == null)
        {
            time_booking= mcurrentDate.get(Calendar.HOUR_OF_DAY)+":"+mcurrentDate.get(Calendar.MINUTE);
        }
        from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(from.getWindowToken(), 0);
                from.setText(str);
                try {

                    if (geo.isPresent()) {
                        List<Address> list = geo.getFromLocationName(str, 1);
                        if (list.size() > 0) {
                            Address address = list.get(0);
                            if (Cities.contains(address.getLocality())) {
                                if (address.hasLatitude() && address.hasLongitude()) {
                                    HashMap<String, ArrayList<Vehicle>> info = (HashMap<String, ArrayList<Vehicle>>) ObjectSerializer.deserialize(pref1.getString("VehicleCity", null));
                                    ArrayList<Vehicle> Vehicles = info.get(address.getLocality());
                                    cars.clear();
                                    if (Vehicles != null) {
                                        for (Vehicle v : Vehicles) {
                                            cars.add(v.Vehicletype);
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cars);
                                    spinner.setAdapter(adapter);
                                    ad = list.get(0);
                                } else {
                                    Toast.makeText(context, "No Coordinates Available for this Location Please select a nearby Location", Toast.LENGTH_LONG).show();
                                    from.setText("");
                                    adf = null;
                                }

                            } else {
                                from.setText("");
                                dialog.setMessage("Service not Available in selected Zone");
                                dialog.setPositiveButton("ok", null);
                                dialog.show();
                            }

                        } else {
                            Toast.makeText(context, "No Coordinates Available for this Location Please select a nearby Location", Toast.LENGTH_LONG).show();
                            from.setText("");
                            adf = null;
                        }

                    }
                } catch (IOException e) {
                    Log.d("Error", e.getMessage());
                }

            }
        });
        toplace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(toplace.getWindowToken(), 0);
                toplace.setText(str);
                try {

                    if (geo.isPresent()) {
                        List<Address> list = geo.getFromLocationName(str, 1);
                        try {
                            if (list.size() > 0) {
                                Address address = list.get(0);
                                if (Cities.contains(address.getLocality())) {
                                    if (address.hasLongitude() && address.hasLatitude()) {
                                        adf = list.get(0);
                                    } else {
                                        toplace.setText("");
                                        Toast.makeText(context, "No Coordinates Available for this Location Please select a nearby Location", Toast.LENGTH_LONG).show();
                                        adf = null;
                                    }
                                } else {
                                    toplace.setText("");
                                    dialog.setMessage("Service not Available in selected Zone");
                                    dialog.setPositiveButton("ok", null);
                                    dialog.show();
                                }

                            } else {
                                toplace.setText("");
                                Toast.makeText(context, "No Coordinates Available for this Location Please select a nearby Location", Toast.LENGTH_LONG).show();
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
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar checktime = Calendar.getInstance();

                SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    int m = checktime.get(Calendar.MONTH) + 1;
                    String c = "" + checktime.get(Calendar.YEAR) + "-" + m + "-" + checktime.get(Calendar.DAY_OF_MONTH);
                    String current = checktime.get(Calendar.HOUR_OF_DAY) + ":" + checktime.get(Calendar.MINUTE);
                    currentdate = format_date.parse(c + " " + current);
                } catch (ParseException e) {
                    Log.d("Date error", "" + e.toString());
                }
                try {
                    selecteddate = format_date.parse(date_booking + " " + time_booking);
                } catch (ParseException e) {
                    Log.d("Date error", "" + e.toString());
                }
                long different = selecteddate.getTime() - currentdate.getTime();
                long hours = different / (1000 * 60 * 60);

                if (from.getText().toString().equals("")) {
                    YoYo.with(Techniques.Shake).duration(700).playOn(from);
                    valdidate = false;
                } else {
                    if (toplace.getText().toString().equals("")) {
                        YoYo.with(Techniques.Shake).duration(700).playOn(toplace);
                        valdidate = false;
                    } else {
                        if (hours >= 1) {
                            if (vehicleid.equals("")) {
                                Vehiclename = spinner.getSelectedItem().toString();
                                valdidate = true;
                            } else {
                                valdidate = true;
                            }

                        } else {
                            valdidate = false;
                            dialog.setMessage("Please Make sure that pickup time is after atleast an hour from now ");
                            dialog.show();

                        }
                    }
                }
                if (valdidate) {
                    params.put("source_lat", String.valueOf(ad.getLatitude()));
                    params.put("source_long", String.valueOf(ad.getLongitude()));
                    params.put("dest_lat", String.valueOf(adf.getLatitude()));
                    params.put("dest_long", String.valueOf(adf.getLongitude()));
                    params.put("country", adf.getCountryName());
                    params.put("source", from.getText().toString());
                    params.put("destination", toplace.getText().toString());
                    params.put("bookingtime", date_booking + " " + time_booking);
                    params.put("vehicletype", Vehiclename);
                    params.put("city", ad.getLocality());
                    JSONObject json = new JSONObject(params);
                    handler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message inputMessage) {
                            if (inputMessage.what == 1) {
                                Handler refresh = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message inputMessage) {
                                        if (inputMessage.what == 1) {
                                            from.setText("");
                                            toplace.setText("");
                                            progressDialog.dismiss();
                                            dialog.setTitle("Success");
                                            dialog.setMessage("Booking Created");
                                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // dialog.dismiss();
                                                    Intent intent = new Intent(getActivity(), MyBookings.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            dialog.show();
                                        }
                                        if (inputMessage.what == 0) {
                                            Bundle b = inputMessage.getData();
                                            progressDialog.dismiss();
                                            dialog.setTitle("Failed");
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
                                Refreshdata refreshdata = new Refreshdata(context, refresh);
                                refreshdata.refreshdata();


                            }

                            if (inputMessage.what == 0) {
                                progressDialog.dismiss();
                                Bundle b = inputMessage.getData();
                                dialog.setTitle("Oops...");
                                dialog.setMessage(b.get("message").toString());
                                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Globalclass.getInstance(context).callactivity("Logout");
                                    }
                                });
                                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        }

                    };
                    http = new Htttpcall(context, handler, url);
                    progressDialog.show();
                    if (Globalclass.getInstance(context).isnetworking()) {
                        http.post(json.toString());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                    }


                }

            }

        });

        from.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                from.requestFocus();
                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (from.getRight() - from.getCompoundDrawables()[2].getBounds().width())) {

                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (from.getRight() - from.getCompoundDrawables()[2].getBounds().width())) {
                            from.setText("");
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            return false;
                        }
                        break;
                }

                return true;

            }
        });
        toplace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                toplace.requestFocus();
                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (toplace.getRight() - toplace.getCompoundDrawables()[2].getBounds().width())) {

                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (toplace.getRight() - toplace.getCompoundDrawables()[2].getBounds().width())) {
                            toplace.setText("");
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            return false;
                        }
                        break;
                }

                return true;

            }
        });


        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButton_rideLater:
                        fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        break;

                    case R.id.radioButton_hourlyRide:
                        fr = new HourlyRide();
                        fm = getFragmentManager();
                        fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_place1, fr).addToBackStack(null);
                        fragmentTransaction.commit();
                        break;

                }
            }
        });

        return v;
    }


}
