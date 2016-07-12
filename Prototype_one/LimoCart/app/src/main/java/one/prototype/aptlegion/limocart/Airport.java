package one.prototype.aptlegion.limocart;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


/**
 * Created by hp on 03-Nov-15.
 */
public class Airport extends Fragment{


    Fragment fr;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    Spinner spinner;
    TextView date,time;
    int mYear,mMonth,mDay;
    int hour, min;
    AutoCompleteTextView from,toplace;
    Button book;
    android.support.v7.app.AlertDialog.Builder dialog ;
    String date_booking,time_booking,vehicleid="";
    Date currentdate,selecteddate;
    ArrayList<Vehicle> vehicles1 = new ArrayList<>();
    Context ctx;
    Set Cities ;
    String token;
    ProgressDialog progressDialog;
    List<String> cars = new ArrayList<>();
    String c;
    String Vehiclename="";
    SharedPreferences sharedPreferences,pref1;
    private RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.airport, container, false);


        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButton_toAirport:
                        fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        break;

                    case R.id.radioButton_frmAirport:
                        fr = new FrmAirport();
                        fm = getFragmentManager();
                        fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_place, fr).addToBackStack(null);
                        fragmentTransaction.commit();


                        break;

                }
            }
        });
        ctx=v.getContext();
        sharedPreferences =ctx.getSharedPreferences("MyPrefs", ctx.MODE_PRIVATE);
        pref1=ctx.getApplicationContext().getSharedPreferences("Companyname",ctx.MODE_PRIVATE);
        Cities =pref1.getStringSet("Cities",null);
        token = sharedPreferences.getString("token",null);

        spinner = (Spinner)v.findViewById(R.id.cartype);
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Please wait while we Book your Cab");
        progressDialog.setTitle("Booking");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(ctx));
        dialog =  new android.support.v7.app.AlertDialog.Builder(ctx);
        book = (Button)v.findViewById(R.id.button5);
        try
        {
            vehicles1 = new ArrayList<Vehicle>();
            vehicles1 = (ArrayList<Vehicle>) ObjectSerializer.deserialize(pref1.getString("Vehicles",null));
            Log.d("test", "" + vehicles1.toString());

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
        ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,cars);
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);

        date= (TextView)v.findViewById(R.id.date);
        time =(TextView)v.findViewById(R.id.time);
        from=(AutoCompleteTextView)v.findViewById(R.id.from);
        toplace=(AutoCompleteTextView)v.findViewById(R.id.to);
        from.setAdapter(new one.prototype.aptlegion.limocart.adapter.GooglePlacesAutocompleteAdapter(ctx,R.layout.auto_complete));
        toplace.setAdapter(new one.prototype.aptlegion.limocart.adapter.GooglePlacesAutocompleteAdapter(ctx,R.layout.auto_complete));
        Calendar mcurrentDate = Calendar.getInstance();
        /*toAirport = (Button) v.findViewById(R.id.btn_toAirport);
        fromAirport = (Button) v.findViewById(R.id.btn_frmAirport);*/
        mYear=mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
        min = mcurrentDate.get(Calendar.MINUTE);
        date.setText("" + mDay + "/" + (mMonth+1) + "/" + mYear);
        time.setText("" + hour + ":" + min);
        ctx=v.getContext();
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
                mDatePicker = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                        date.setText("" + selectedday + "-" + (selectedmonth+1) + "-" + selectedyear);
                        date_booking = selectedyear + "-" + (selectedmonth+1) + "-" + selectedday;


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
                timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
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



        return v;
    }




}
