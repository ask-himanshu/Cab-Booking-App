package driver.prototype.aptlegion.limocartdriver;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elitebook on 24-Jun-15.
 */
public class pendingbookings extends android.support.v4.app.Fragment {
    RecyclerView recyclerView;
    String accept_url;
    JSONObject j;
    Message message_id,message_pending;
    htttpcall htttpcall;
    SwipeRefreshLayout swipeRefreshLayout;
    mycard adapter;
    ArrayList<openbookingdata> openbookingdatas = new ArrayList<>();
    Context context;
    Handler Accept_trip;
    SharedPreferences sharedPreferences;
    TextView nobookings,text;
    int pos;
    View view,main;
    Location location;
    ProgressBar lodaing;
    private OnAcceptClickListner aceeptClickListner;
    private static final long INTERVAL = 10000 ;
    private static final long FASTEST_INTERVAL = 10000 ;
    LocationRequest mLocationRequest;
    GoogleApiClient googleApiClient;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pendingbookings, container, false);
        context = view.getContext();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        main = inflater.inflate(R.layout.activity_main_activity2,container,false);
        lodaing = (ProgressBar)main.findViewById(R.id.loading);
        nobookings = (TextView)view.findViewById(R.id.nobookings);
        text = (TextView)view.findViewById(R.id.text);
            recyclerView = (RecyclerView)view.findViewById(R.id.pendingbookinglist);
            recyclerView.setHasFixedSize(true);
            sharedPreferences=context.getSharedPreferences("MyPrefs_driver",context.MODE_PRIVATE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);



            swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.activity_main_swipe_refresh_layout_pending);
            swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLACK, Color.RED, Color.YELLOW);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message inputMessage) {
                            if(inputMessage.what==1){
                            nobookings.setVisibility(RelativeLayout.GONE);
                            text.setVisibility(RelativeLayout.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            openbookingdatas = ((GlobalVaraible) context.getApplicationContext()).getBookingdata();
                            adapter = new mycard(openbookingdatas, context);
                            recyclerView.setAdapter(adapter);
                            if(!(openbookingdatas.size()>0))
                            {
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
                    message_pending = handler.obtainMessage();
                    getdata.getpendingbookings(context, sharedPreferences.getString("token", null), message_pending);
                }
            });

        return view;
    }

    @Override
    public void onActivityCreated( Bundle Savedinstance){
        super.onActivityCreated(Savedinstance);
        final Handler getid = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle b = inputMessage.getData();
                pos = Integer.valueOf(b.getString("message"));
                int booking_id= Integer.valueOf(openbookingdatas.get(pos).bookingid);
                accept_url = "http://demo.olwebapps.com/api/bookings/"+booking_id+"?token="+sharedPreferences.getString("token",null)+"";
                Accept_trip = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message inputMessage)
                    {
                        if(inputMessage.what ==1)
                        {
                         Handler refresh = new Handler(Looper.getMainLooper())
                         {
                             @Override
                             public void handleMessage(Message inputMessage)
                             {
                                 if(inputMessage.what ==1)
                                 {
                                     lodaing.setVisibility(View.GONE);
                                  Handler handler = ((GlobalVaraible)context.getApplicationContext()).getHandler();

                                  ArrayList<openbookingdata>list=new ArrayList<>();
                                  list=((GlobalVaraible)context.getApplicationContext()).getAcceptedbooking();
                                  acceptedbookinglist_adapter a =new acceptedbookinglist_adapter(list);
                                  a.updatelist(list);
                                  openbookingdatas =((GlobalVaraible)context.getApplicationContext()).getBookingdata();
                                  adapter.updateList(openbookingdatas);
                                  Snackbar.make(view,"Accepted",Snackbar.LENGTH_LONG).show();
                                     aceeptClickListner.onAcceptiClicked();
                                     if(handler != null)
                                     {
                                         Message m = handler.obtainMessage();
                                         m.what =1;
                                         m.sendToTarget();
                                     }
                                  if(openbookingdatas.isEmpty())
                                  {
                                      nobookings.setVisibility(RelativeLayout.VISIBLE);
                                      text.setVisibility(RelativeLayout.VISIBLE);
                                  }
                                 }
                             }

                         };
                            Refreshdata r = new Refreshdata(context,refresh);
                            r.refreshdata();
                        }
                        if(inputMessage.what ==0)
                        {
                            lodaing.setVisibility(View.GONE);
                            Bundle b= inputMessage.getData();
                            Toast.makeText(context,b.getString("message",""),Toast.LENGTH_SHORT).show();
                        }

                    }

                };
                htttpcall = new htttpcall(context,Accept_trip,accept_url);
                HashMap<String,String> a = new HashMap<>();
                a.put("status","Accepted");
                try
                {
                    j = new JSONObject(a);
                }catch (Exception ex){};
                lodaing.setVisibility(View.VISIBLE);
                htttpcall.put(j);

            }
        };
        openbookingdatas = ((GlobalVaraible)context.getApplicationContext()).getBookingdata();
        adapter = new mycard(openbookingdatas,context,message_id,getid);
        recyclerView.setAdapter(adapter);
        nobookings.setVisibility(RelativeLayout.GONE);
        text.setVisibility(RelativeLayout.GONE);
        if(!(openbookingdatas.size()>0))
        {
            nobookings.setVisibility(RelativeLayout.VISIBLE);
            text.setVisibility(RelativeLayout.VISIBLE);
        }
    }


    public void setOnAcceptClickListner(OnAcceptClickListner aceeptClickListner){

        if(aceeptClickListner !=null){
            this.aceeptClickListner =aceeptClickListner;
        }

    }



    public interface OnAcceptClickListner{
        public void onAcceptiClicked();
    }
}
