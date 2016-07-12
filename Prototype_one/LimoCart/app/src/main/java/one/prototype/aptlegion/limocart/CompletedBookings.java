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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.CompletedBookingsAdapter;

/**
 * Created by hp on 20-Aug-15.
 */
public class CompletedBookings  extends Fragment {
    ArrayList<String> data = new ArrayList<>();
    Context context;
    CompletedBookingsAdapter adapter;
    ArrayList<Openbookingdata>openbookingdatas= new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout ;
    Message message;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    Htttpcall htttpcall;
    String accept_url;
    int pos;
    JSONObject j;
    TextView nobookings,swipe;
    String lat="",lon="";
    String Bookingid;
    Handler Pay,paymentinfo,feedback;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.open_bookings, container, false);
        context = view.getContext();
        nobookings =(TextView)view.findViewById(R.id.nobookings);
        swipe=(TextView)view.findViewById(R.id.swipe);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        sharedPreferences =context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        openbookingdatas= new ArrayList<>();
        openbookingdatas=((one.prototype.aptlegion.limocart.GlobalVaraible)context.getApplicationContext()).getCompletedbookiings();
        recyclerView = (RecyclerView) view.findViewById(R.id.dummyfrag_scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
        Pay = new Handler(Looper.getMainLooper()){
          @Override
          public void handleMessage(final Message message)
          {
              Bundle b = message.getData();
              pos = Integer.valueOf(b.getString("message"));
              int booking_id= Integer.valueOf(openbookingdatas.get(pos).bookingid);
              Bookingid = openbookingdatas.get(pos).bookingid.toString();
              accept_url = "http://demo.olwebapps.com/api/payment/"+booking_id+"?token="+sharedPreferences.getString("token",null)+"";
              paymentinfo = new Handler(Looper.getMainLooper())
              {
                  @Override
                  public void handleMessage(Message message1)
                  {
                     if(message1.what==1)
                     {
                         Bundle b = message1.getData();
                         try
                         {
                             progressDialog.dismiss();
                             JSONObject jsonObject = new JSONObject(b.getString("message"));
                             JSONObject data = new JSONObject(jsonObject.getString("data"));
                             Intent intent = new Intent(context,Paymentinfo.class);
                             intent.putExtra("amount",data.getString("amount"));
                             intent.putExtra("unitprice",data.getString("unitprice"));
                             intent.putExtra("distance",data.getString("distance"));
                             intent.putExtra("minimum_fare",data.getString("minimum_fare"));
                             intent.putExtra("minimum_dist",data.getString("minimum_dist"));
                             intent.putExtra("Bookingid",Bookingid);
                             context.startActivity(intent);

                         }catch (Exception ex)
                         {
                             Log.d("Error",""+ex.toString());

                         }

                     }
                      if(message1.what==0)
                      {
                          Bundle b = message1.getData();
                          Toast.makeText(context,b.getString("message").toString(),Toast.LENGTH_LONG).show();
                      }
                  }
              };
              progressDialog.setMessage("Opening Payment Info");
              progressDialog.show();
              htttpcall = new Htttpcall(context,paymentinfo,accept_url);
              htttpcall.get();
        }
        };
        feedback = new Handler(Looper.getMainLooper())
        {
         @Override
          public void handleMessage(Message message)
         {
             Bundle b = message.getData();
             pos = Integer.valueOf(b.getString("message"));
             int booking_id= Integer.valueOf(openbookingdatas.get(pos).bookingid);
             Bookingid = openbookingdatas.get(pos).bookingid.toString();
             Intent feed = new Intent(context,Feedback.class);
             feed.putExtra("Bookingid",Bookingid);
             context.startActivity(feed);
         }
        };


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nobookings.setVisibility(View.GONE);
                swipe.setVisibility(View.GONE);
                getCompBookings();

            }
        });
        return  view;
    }


    private void getCompBookings(){
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if(inputMessage.what==1)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    openbookingdatas= new ArrayList<>();
                    openbookingdatas=((one.prototype.aptlegion.limocart.GlobalVaraible)context.getApplicationContext()).getCompletedbookiings();
                    adapter = new CompletedBookingsAdapter(openbookingdatas,context,Pay,feedback);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    if(openbookingdatas.isEmpty())
                    {
                        nobookings.setVisibility(View.VISIBLE);
                        swipe.setVisibility(View.VISIBLE);
                    }
                }
                if(inputMessage.what==0)
                {
                    swipeRefreshLayout.setRefreshing(false);
                    Bundle b = inputMessage.getData();
                    Toast.makeText(context,b.get("message").toString(),Toast.LENGTH_LONG).show();
                }


            }

        };
        if(Globalclass.getInstance(context).isnetworking())
        {
            Getdata.startAction(context, "getcompletedbookings", sharedPreferences.getString("token", null), handler);
        }else
        {
            Toast.makeText(context,"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityCreated(Bundle Savedinstance) {
        super.onActivityCreated(Savedinstance);
        getCompBookings();
        nobookings.setVisibility(View.GONE);
        swipe.setVisibility(View.GONE);
        openbookingdatas=((GlobalVaraible)context.getApplicationContext()).getCompletedbookiings();
        adapter = new CompletedBookingsAdapter(openbookingdatas,context,Pay,feedback);
        recyclerView.setAdapter(adapter);
        if(openbookingdatas.isEmpty())
        {
            nobookings.setVisibility(View.VISIBLE);
            swipe.setVisibility(View.VISIBLE);
        }
    }
    }


