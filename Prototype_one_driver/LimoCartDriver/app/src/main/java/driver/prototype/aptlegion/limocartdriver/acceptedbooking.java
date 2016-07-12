package driver.prototype.aptlegion.limocartdriver;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Elitebook on 24-Jun-15.
 */
public class acceptedbooking extends android.support.v4.app.Fragment
{
    RecyclerView recyclerView;
    JSONObject j;
    Message message_id,message_accept;
    htttpcall htttpcall;
    SwipeRefreshLayout swipeRefreshLayout;
    acceptedbookinglist_adapter adapter;
    ArrayList<openbookingdata> bookingdatas_accepted = new ArrayList<>();
    Context context;
    SharedPreferences sharedPreferences;
    LocationManager locationManager;
    TextView nobookings,text;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.acceptedbooking, container, false);
        context = view.getContext();
        nobookings = (TextView)view.findViewById(R.id.nobookings);
        text = (TextView)view.findViewById(R.id.text);
        sharedPreferences=context.getSharedPreferences("MyPrefs_driver",context.MODE_PRIVATE);
        recyclerView = (RecyclerView)view.findViewById(R.id.accpetedbookinglist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLACK, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBookings();

            }

        });



        return view;
    }
       private void refreshBookings(){
        android.os.Handler handler_refresh = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if(inputMessage.what ==1) {
                    swipeRefreshLayout.setRefreshing(false);

                    bookingdatas_accepted = ((GlobalVaraible) context.getApplicationContext()).getAcceptedbooking();
                    adapter = new acceptedbookinglist_adapter(bookingdatas_accepted, context);
                    adapter.updatelist(bookingdatas_accepted);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    nobookings.setVisibility(RelativeLayout.GONE);
                    text.setVisibility(RelativeLayout.GONE);
                    if (!(bookingdatas_accepted.size() > 0)) {
                        nobookings.setVisibility(RelativeLayout.VISIBLE);
                        text.setVisibility(RelativeLayout.VISIBLE);
                    }
                }
                if(inputMessage.what==0)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    nobookings.setText("Something Went Wrong....");
                    nobookings.setVisibility(View.VISIBLE);
                }
            }
        };
        message_accept = handler_refresh.obtainMessage();
        getdata.getacceptedbookings(context, sharedPreferences.getString("token", null), message_accept);
    }
    @Override
    public void onResume() {

        super.onResume();
        refreshBookings();
    }

    public String criteria(){
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        return locationManager.getBestProvider(criteria,true);
    }

    @Override
    public void onActivityCreated( Bundle Savedinstance){
        super.onActivityCreated(Savedinstance);
        locationManager= (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        String provider = criteria();
        Handler getid = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle b = inputMessage.getData();
                final int pos = Integer.valueOf(b.getString("message"));
                final int booking_id= Integer.valueOf(bookingdatas_accepted.get(pos).bookingid);
                String accept_url = "http://demo.olwebapps.com/api/bookings/"+booking_id+"?token="+sharedPreferences.getString("token",null)+"";
                Handler Start_trip = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message inputMessage)
                    {
                        if(inputMessage.what ==1)
                        {
                            Intent starttrip = new Intent(context,LocationActivity.class);
                            Calendar mcurrentDate = Calendar.getInstance();
                            starttrip.putExtra("position", pos);
                            int m = mcurrentDate.get(Calendar.MONTH)+1;
                            starttrip.putExtra("starttime", "" + mcurrentDate.get(Calendar.YEAR) + "-" + m + "-" + mcurrentDate.get(Calendar.DATE) + " " + mcurrentDate.get(Calendar.HOUR_OF_DAY) + ":" + mcurrentDate.get(Calendar.MINUTE));
                            context.startActivity(starttrip);

                    }
                        if(inputMessage.what ==0)
                        {
                            Bundle b= inputMessage.getData();
                            Toast.makeText(context,b.getString("message",""),Toast.LENGTH_SHORT).show();
                        }

                    }

                };
                htttpcall = new htttpcall(context,Start_trip,accept_url);
                HashMap<String,String> a = new HashMap<>();
                a.put("start_lat",bookingdatas_accepted.get(pos).SourceLatitude);
                a.put("start_long",bookingdatas_accepted.get(pos).SourceLongitude);
                a.put("started_at",String.valueOf(bookingdatas_accepted.get(pos).sourceAddress));
                Calendar mcurrentDate = Calendar.getInstance();
                int m = mcurrentDate.get(Calendar.MONTH)+1;
                a.put("starttime",""+mcurrentDate.get(Calendar.YEAR)+"-"+m+"-"+mcurrentDate.get(Calendar.DATE)+" "+mcurrentDate.get(Calendar.HOUR_OF_DAY)+":"+mcurrentDate.get(Calendar.MINUTE));
                a.put("status","Started");
                try
                {
                    j = new JSONObject(a);
                }catch (Exception ex){};
                if(Globalclass.getInstance(context).isnetworking()){
                    if(Globalclass.getInstance(context).isgpsenabled())
                    {
                        htttpcall.put(j);
                    }else{
                        Intent nointernet  = new Intent(context,GPS_falil.class);
                        startActivity(nointernet);
                    }
                }else{
                    Intent nointernet  = new Intent(context,ErrorActivity.class);
                    startActivity(nointernet);
                }
            }
        };
       // refreshBookings();
        bookingdatas_accepted = ((GlobalVaraible)context.getApplicationContext()).getAcceptedbooking();
        adapter = new acceptedbookinglist_adapter(bookingdatas_accepted,context,message_id,getid);
        recyclerView.setAdapter(adapter);

        nobookings.setVisibility(RelativeLayout.GONE);
        text.setVisibility(RelativeLayout.GONE);
        if(bookingdatas_accepted.isEmpty())
        {
            nobookings.setVisibility(RelativeLayout.VISIBLE);
            text.setVisibility(RelativeLayout.VISIBLE);

        }
    }



}
