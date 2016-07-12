package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.mycard;

/**
 * Created by Elitebook on 19-Jun-15.
 */
public class OpenBookings extends Fragment {
    Context context;
    mycard adapter;
    ArrayList<Openbookingdata>openbookingdatas= new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout ;
    Message message;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    Handler cancelbooking,Track,paymentHandler;
    Htttpcall htttpcall;
    String accept_url;
    int pos;
    JSONObject j;
    TextView nobookings,swipe;
    String lat="",lon="";
    String bid,BookingId;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_bookings, container, false);
        context = view.getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
        nobookings =(TextView)view.findViewById(R.id.nobookings);
        swipe=(TextView)view.findViewById(R.id.swipe);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        sharedPreferences =context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        openbookingdatas = new ArrayList<>();
        openbookingdatas=((one.prototype.aptlegion.limocart.GlobalVaraible)context.getApplicationContext()).getBookingdata();
        recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        cancelbooking = new android.os.Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle b = inputMessage.getData();
                pos = Integer.valueOf(b.getString("message"));
                int booking_id= Integer.valueOf(openbookingdatas.get(pos).bookingid);
                accept_url = "http://demo.olwebapps.com/api/bookings/"+booking_id+"?token="+sharedPreferences.getString("token",null)+"";
                Handler cancel = new Handler(Looper.getMainLooper())
                {
                    @Override
                    public void handleMessage(Message inputMessage)
                    {
                        if(inputMessage.what ==1)
                        {
                            progressDialog.dismiss();
                            openbookingdatas.remove(pos);
                            adapter.updateList(openbookingdatas);
                            Toast.makeText(context,"Cancelled",Toast.LENGTH_SHORT).show();
                        }
                        if(inputMessage.what==0)
                        {
                            progressDialog.dismiss();
                            Bundle b = inputMessage.getData();
                            Toast.makeText(context,b.get("message").toString(),Toast.LENGTH_SHORT).show();

                        }


                    }
                };
                progressDialog.setMessage("Cancelling Booking...");
                progressDialog.show();
                htttpcall = new Htttpcall(context,cancel,accept_url);
                HashMap<String,String> a = new HashMap<>();
                a.put("status", "Cancelled");
                try
                {
                    j = new JSONObject(a);
                }catch (Exception ex){};
                htttpcall.put(j.toString());
            }

        };
        Track = new Handler(Looper.getMainLooper())
        {
          @Override
          public void handleMessage(Message message)
          {
              Bundle b = message.getData();
              pos = Integer.valueOf(b.getString("message"));
              int booking_id= Integer.valueOf(openbookingdatas.get(pos).bookingid);
              bid=openbookingdatas.get(pos).bookingid.toString();
              accept_url = "http://demo.olwebapps.com/api/location/"+booking_id+"?token="+sharedPreferences.getString("token",null)+"";
              Handler handler = new Handler(Looper.getMainLooper())
              {
                @Override
                public void handleMessage(Message message1)
                {
                    if(message1.what==1)
                    {

                        progressDialog.dismiss();
                      Bundle b = message1.getData();
                        try{
                            JSONObject jsonObject = new JSONObject(b.getString("message"));
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
                            lon= data.getString("longitude");
                            lat=data.getString("latitude");
                            if(!(lat.equals(""))&&!(lon.equals("")))
                            {
                                Intent intent = new Intent(context,TrackDriver.class);
                                intent.putExtra("lat",lat);
                                intent.putExtra("lon",lon);
                                intent.putExtra("Bookingid",bid);
                                startActivity(intent);
                            }else
                            {


                                Toast.makeText(context,"Wait for Driver's Approval",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception ex)
                        {
                            Log.d("Exception",ex.toString());
                        }

                    }
                    if(message1.what==0)
                    {
                        progressDialog.dismiss();
                        Toast.makeText(context,"Wait for Driver's Approval",Toast.LENGTH_SHORT).show();
                    }
                }
              };
              progressDialog.setMessage("Fetching Driver Location....");
              progressDialog.show();
              Htttpcall htttpcall = new Htttpcall(context,handler,accept_url);
              htttpcall.get();


          }
        };




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nobookings.setVisibility(View.GONE);
                swipe.setVisibility(View.GONE);
                android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        if (inputMessage.what == 1) {
                            swipeRefreshLayout.setRefreshing(false);
                            openbookingdatas = ((one.prototype.aptlegion.limocart.GlobalVaraible) context.getApplicationContext()).getBookingdata();
                            adapter = new mycard(openbookingdatas, context, cancelbooking, Track);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            if (openbookingdatas.isEmpty()) {
                                nobookings.setVisibility(View.VISIBLE);
                                swipe.setVisibility(View.VISIBLE);
                            }
                        }
                        if (inputMessage.what == 0) {
                            swipeRefreshLayout.setRefreshing(false);
                            Bundle b = inputMessage.getData();
                            Toast.makeText(context, b.get("message").toString(), Toast.LENGTH_LONG).show();
                        }


                    }

                };
                if (Globalclass.getInstance(context).isnetworking()) {
                    Getdata.startAction(context, "getopenbookings", sharedPreferences.getString("token", null), handler);
                } else {
                    Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }


            }
        });
        logHeap(this.getClass());  //function calling
                return view;

    }
            @Override
            public void onActivityCreated(Bundle Savedinstance) {
                super.onActivityCreated(Savedinstance);
                nobookings.setVisibility(View.GONE);
                swipe.setVisibility(View.GONE);
                openbookingdatas = new ArrayList<>();
                openbookingdatas=((GlobalVaraible)context.getApplicationContext()).getBookingdata();
                adapter = new mycard(openbookingdatas,context,cancelbooking,Track);
                recyclerView.setAdapter(adapter);
                if(openbookingdatas.isEmpty())
                {
                    nobookings.setVisibility(View.VISIBLE);
                    swipe.setVisibility(View.VISIBLE);
                }


            }
    //Done by me to get rid of crash from oom
    public static void logHeap(Class clazz){
        // Glide.get(this).clearMemory();
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        System.gc();
        System.gc();


    }


}
