package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.allbookingsadapter;

/**
 * Created by Elitebook on 19-Jun-15.
 */
public class Allbookings extends Fragment {


    Context context;
    allbookingsadapter adapter;
    ArrayList<Openbookingdata>openbookingdatas= new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout ;
    Message message;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    Htttpcall htttpcall;
    JSONObject j;
    TextView nobookings,swipe;
    Getdata getdata;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,  Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.all_bookings, container, false);
        nobookings =(TextView)view.findViewById(R.id.nobookings);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        swipe=(TextView)view.findViewById(R.id.swipe);
        context = view.getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.openbookings_swipe);
        sharedPreferences =context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        openbookingdatas= new ArrayList<>();
        openbookingdatas=((GlobalVaraible)context.getApplicationContext()).getAllbookings();
        recyclerView = (RecyclerView) view.findViewById(R.id.openbookings_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
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
                            openbookingdatas = ((GlobalVaraible) context.getApplicationContext()).getAllbookings();
                            adapter = new allbookingsadapter(openbookingdatas, context, null);
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
                    Getdata.startAction(context, "getallbookings", sharedPreferences.getString("token", null), handler);
                } else {
                    Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;



    }

    @Override
    public void onActivityCreated(Bundle Savedinstance) {
        super.onActivityCreated(Savedinstance);
        openbookingdatas=((GlobalVaraible)context.getApplicationContext()).getAllbookings();
        adapter = new allbookingsadapter(openbookingdatas,context,null);
        adapter.setOnClickListner(new allbookingsadapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Openbookingdata selectedBooking = openbookingdatas.get(position);
                String bookingId = selectedBooking.getBookingid();
                String statusId = selectedBooking.getStatus();
                String bookingTime = selectedBooking.getBookingtime();
                String source = selectedBooking.getSourceAddress();
                String destination = selectedBooking.getDestinationAddress();
                String sourceLat = selectedBooking.getSourceLatitude();
                String sourceLon = selectedBooking.getSourceLongitude();
                String destLat = selectedBooking.getDestinationLatitude();
                String destLon = selectedBooking.getDestinationLongitude();


                Intent bookingDetails = new Intent(getActivity(),DetailsBooking.class);
                bookingDetails.putExtra(Constants.KEY_BOOKINGID,bookingId);
                bookingDetails.putExtra(Constants.KEY_STATUS, statusId);
                bookingDetails.putExtra(Constants.KEY_BOOKING_TIME,bookingTime);
                bookingDetails.putExtra(Constants.KEY_SOURCE,source);
                bookingDetails.putExtra(Constants.KEY_DESTINATION,destination);
                bookingDetails.putExtra(Constants.KEY_SOURCE_LAT,sourceLat);
                bookingDetails.putExtra(Constants.KEY_SOURCE_LON,sourceLon);
                bookingDetails.putExtra(Constants.KEY_DEST_LAT,destLat);
                bookingDetails.putExtra(Constants.KEY_DEST_LON,destLon);

                startActivity(bookingDetails);
            }
        });
        recyclerView.setAdapter(adapter);
        if(openbookingdatas.isEmpty())
        {
            nobookings.setVisibility(View.VISIBLE);
            swipe.setVisibility(View.VISIBLE);
        }
    }
}



