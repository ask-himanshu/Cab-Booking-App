package driver.prototype.aptlegion.limocartdriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
 * Created by Elitebook on 30-Jun-15.
 */
public class allbookings extends android.support.v4.app.Fragment {

    RecyclerView recyclerView;
    Message message_id,message_accept;
    SwipeRefreshLayout swipeRefreshLayout;
    allbookingsadapter adapter;
    ArrayList<openbookingdata> bookingdatas_all = new ArrayList<>();
    Context context;
    Handler getid;
    HashMap<String,String> params = new HashMap<>();
    SharedPreferences sharedPreferences;
    TextView nobookings,text;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.allbookings, container, false);
        context = view.getContext();
        bookingdatas_all=((GlobalVaraible)context.getApplicationContext()).getAllbookings();
        nobookings = (TextView)view.findViewById(R.id.nobookings);
        text = (TextView)view.findViewById(R.id.text);
        sharedPreferences=context.getSharedPreferences("MyPrefs_driver",context.MODE_PRIVATE);
        recyclerView = (RecyclerView)view.findViewById(R.id.allbookinglist);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.allbokings_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.BLACK, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                android.os.Handler handler_refresh = new android.os.Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        if(inputMessage.what==1){
                        swipeRefreshLayout.setRefreshing(false);
                        bookingdatas_all = ((GlobalVaraible) context.getApplicationContext()).getAllbookings();
                        adapter = new allbookingsadapter(bookingdatas_all, context);
                        recyclerView.setAdapter(adapter);
                        nobookings.setVisibility(RelativeLayout.GONE);
                        text.setVisibility(RelativeLayout.GONE);
                        if (!(bookingdatas_all.size() > 0)) {
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
                getdata.getallbookings(context, sharedPreferences.getString("token", null), message_accept);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated( Bundle Savedinstance){
        super.onActivityCreated(Savedinstance);
        bookingdatas_all = ((GlobalVaraible)context.getApplicationContext()).getAllbookings();
        adapter = new allbookingsadapter(bookingdatas_all,context,message_id,getid);
        adapter.setOnClickListner(new allbookingsadapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openbookingdata selectedBooking = bookingdatas_all.get(position);
                String bookingId = selectedBooking.getBookingid();
                String statusId = selectedBooking.getStatus();
                String bookingTime = selectedBooking.getBookingtime();
                String source = selectedBooking.getSourceAddress();
                String destination = selectedBooking.getDestinationAddress();
                String sourceLat = selectedBooking.getSourceLatitude();
                String sourceLon = selectedBooking.getSourceLongitude();
                String destLat = selectedBooking.getDestinationLatitude();
                String destLon = selectedBooking.getDestinationLongitude();


                Intent bookingDetails = new Intent(getActivity(),BookingDetails.class);
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
        nobookings.setVisibility(RelativeLayout.GONE);
        text.setVisibility(RelativeLayout.GONE);
        if(!(bookingdatas_all.size()>0))
        {
            nobookings.setVisibility(RelativeLayout.VISIBLE);
            text.setVisibility(RelativeLayout.VISIBLE);
        }
    }
}
