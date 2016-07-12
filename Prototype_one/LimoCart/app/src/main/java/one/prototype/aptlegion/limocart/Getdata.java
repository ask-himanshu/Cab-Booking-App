package one.prototype.aptlegion.limocart;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Getdata extends IntentService {
    static String url,getopenbookings,getallbookings,getcompanyinfo,getcompletedbookings,getpromotions;
    static Context mContext;
    HashMap<String,String>user = new HashMap<>();
    String action;
    Openbookingdata bookings= new Openbookingdata();
    ArrayList<Openbookingdata>bookingdata= new ArrayList<>();
    ArrayList<Promotions>Promos = new ArrayList<>();
    SharedPreferences sharedPreferences;
    static Message msg;
    SharedPreferences.Editor editor;
    HashMap<String, ArrayList<Vehicle>>carlist = new HashMap<>();

    public static void startAction(Context context,String action,String token,Handler handler) {
        mContext= context;
        url = "http://demo.olwebapps.com/api/customer?token={"+token+"}";
        getopenbookings= "http://demo.olwebapps.com/api/bookings/open?token="+token+"";
        getallbookings= "http://demo.olwebapps.com/api/bookings/all?token="+token+"";
        getcompletedbookings= "http://demo.olwebapps.com/api/bookings/completed?token="+token+"";
        msg = handler.obtainMessage();
        Intent intent = new Intent(context, Getdata.class);
        intent.setAction(action);
        context.startService(intent);

    }

    public void getcompanyifo(Context context,String action,String name,Handler handler)
    {
        mContext= context;
        sharedPreferences = mContext.getSharedPreferences("MyPrefs", mContext.MODE_PRIVATE);
        getcompanyinfo = "http://demo.olwebapps.com/api/city/"+name;
        msg = handler.obtainMessage();
        Intent intent = new Intent(context, Getdata.class);
        intent.setAction(action);
        context.startService(intent);

    }
    public static void getPromotions(Context context,Handler handler,String token)
    {
        getpromotions ="http://demo.olwebapps.com/api/promotions?token={"+token+"}";
        msg = handler.obtainMessage();
        Intent intent = new Intent(context, Getdata.class);
        intent.setAction("getPromos");
        context.startService(intent);
    }

    public Getdata() {

        super("getdata");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            action = intent.getAction();
            if (action.equals("getopenbookings")) {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getopenbookings,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject( response.toString());
                            JSONArray data = json.optJSONArray("data");
                            bookingdata.clear();
                            for (int i =0;i<data.length();i++)
                            {
                               JSONObject jsonObject = data.getJSONObject(i);
                                bookings.bookingid =jsonObject.getString("id");
                                bookings.driverid=jsonObject.getString("driver_id");
                                bookings.status=jsonObject.getString("status");
                                bookings.sourceAddress=jsonObject.getString("source");
                                bookings.destinationAddress=jsonObject.getString("destination");
                                bookings.bookingtime=jsonObject.getString("bookingtime");
                                bookings.distance=jsonObject.getString("distance");
                                bookings.starttime=jsonObject.getString("starttime");
                                bookings.endtime=jsonObject.getString("endtime");
                                bookings.createdat=jsonObject.getString("created_at");
                                bookings.SourceLatitude = jsonObject.getString("source_lat");
                                bookings.SourceLongitude = jsonObject.getString("source_long");
                                bookings.DestinationLatitude = jsonObject.getString("dest_lat");
                                bookings.DestinationLongitude = jsonObject.getString("dest_long");
                                bookingdata.add(bookings);
                                bookings= new Openbookingdata();
                            }
                            Log.d("all",""+String.valueOf(bookingdata.size()));
                            ((GlobalVaraible)mContext.getApplicationContext()).setBookingdata(bookingdata);
                            if (msg != null)
                            {
                                msg.what=1;
                                msg.sendToTarget();

                            }
                        } catch (JSONException e) {
                            Log.d("bookings",e.toString());
                            if(msg != null)
                            {
                                Bundle b = new Bundle();
                                b.putString("message",e.toString());
                                msg.what=0;
                                msg.setData(b);
                                msg.sendToTarget();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("getopenbookings",error.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }
                });
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
            }
            }

            if(action.equals("getcustomerdata"))
            {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject json = new JSONObject( response.toString());
                            JSONObject data = new JSONObject(String.valueOf(json.getJSONObject("data")));
                            Log.d("cus", data.toString());
                            user.put("id", data.getString("id"));
                            user.put("firstname", data.getString("firstname"));
                            user.put("lastname", data.getString("lastname"));
                            user.put("loginid", data.getString("loginid"));
                            user.put("phone",data.getString("phone"));
                            user.put("city", data.getString("city"));
                            JSONObject c = new JSONObject(data.getString("country"));
                            user.put("country", c.getString("name"));
                            user.put("wallet", data.getString("wallet"));
                            user.put("address",data.getString("address"));
                            user.put("zip",data.getString("zip"));
                            user.put("password","123456");
                            ((GlobalVaraible) mContext.getApplicationContext()).setUserinfo(user);
                             Log.d("cus", ""+((GlobalVaraible) mContext.getApplicationContext()).getUserinfo().toString());
                            if (msg!= null)
                            {
                                msg.what=1;
                                msg.sendToTarget();

                            }
                        } catch (JSONException e) {
                            Log.d("getcutomerdata", e.toString());
                            if(msg != null)
                            {
                                Bundle b = new Bundle();
                                b.putString("message",e.toString());
                                msg.what=0;
                                msg.setData(b);
                                msg.sendToTarget();
                            }
                        }



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("getcustomerdata",error.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }});
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
                }
        if(action.equals("getallbookings"))
        {
            JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getallbookings, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject json = null;
                    try {

                        json = new JSONObject( response.toString());
                        JSONArray data = json.optJSONArray("data");
                        bookingdata.clear();
                        for (int i =0;i<data.length();i++)
                        {
                            JSONObject jsonObject = data.getJSONObject(i);
                            bookings.bookingid =jsonObject.getString("id");
                            bookings.driverid=jsonObject.getString("driver_id");
                            bookings.status=jsonObject.getString("status");
                            bookings.sourceAddress=jsonObject.getString("source");
                            bookings.destinationAddress=jsonObject.getString("destination");
                            bookings.bookingtime=jsonObject.getString("bookingtime");
                            bookings.distance=jsonObject.getString("distance");
                            bookings.starttime=jsonObject.getString("starttime");
                            bookings.endtime=jsonObject.getString("endtime");
                            bookings.createdat=jsonObject.getString("created_at");
                            bookings.SourceLatitude = jsonObject.getString("source_lat");
                            bookings.SourceLongitude = jsonObject.getString("source_long");
                            bookings.DestinationLatitude = jsonObject.getString("dest_lat");
                            bookings.DestinationLongitude = jsonObject.getString("dest_long");
                            bookingdata.add(bookings);
                            bookings= new Openbookingdata();
                        }
                        Log.d("open", "" + String.valueOf(bookingdata.size()));
                        ((GlobalVaraible)mContext.getApplicationContext()).setAllbookings(bookingdata);
                        if (msg!= null)
                        {
                            msg.what=1;
                            msg.sendToTarget();

                        }
                    } catch (JSONException e) {
                        Log.d("bookings",e.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",e.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("getallbookings",error.toString());
                    if(msg != null)
                    {
                        Bundle b = new Bundle();
                        b.putString("message",error.toString());
                        msg.what=0;
                        msg.setData(b);
                        msg.sendToTarget();
                    }
                }
            });
            string.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Globalclass.getInstance(mContext).makerequest(string);
        }

        /*Method To get Data according to booking Id*/




        if (action.equals("getcartypes")) {

            sharedPreferences = mContext.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            editor=sharedPreferences.edit();
            final JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getcompanyinfo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject( response.toString());
                        JSONObject data =  json.getJSONObject("data");
                        Iterator<?> keys =  data.keys();

                        while (keys.hasNext())
                        {
                            String key = (String)keys.next();
                            JSONArray city = data.getJSONArray(key);
                            ArrayList<Vehicle> vehicles = new ArrayList<>();
                            for(int i = 0;i<city.length();i++)
                            {
                                Vehicle vehicle = new Vehicle();
                                vehicle.Vehicletype=city.getString(i);
                                vehicles.add(vehicle);

                            }
                            carlist.put(key,vehicles);

                        }
                        if (msg != null)
                        {
                            msg.what=1;
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("info",carlist);
                            msg.setData(bundle);
                            msg.sendToTarget();

                        }
                    } catch (JSONException e) {
                        Log.d("bookings",e.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",e.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("getopenbookings",error.toString());
                    if(msg != null)
                    {
                        Bundle b = new Bundle();
                        b.putString("message",error.toString());
                        msg.what=0;
                        msg.setData(b);
                        msg.sendToTarget();
                    }else {
                        Toast.makeText(Getdata.this,"Launch The App Again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            string.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Globalclass.getInstance(mContext).makerequest(string);
        }

        if(action.equals("getcompletedbookings"))
        {
            JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getcompletedbookings, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject json = null;
                    try {

                        json = new JSONObject( response.toString());
                        JSONArray data = json.optJSONArray("data");
                        bookingdata.clear();
                        for (int i =0;i<data.length();i++)
                        {
                            JSONObject jsonObject = data.getJSONObject(i);
                            bookings.bookingid =jsonObject.getString("id");
                            bookings.driverid=jsonObject.getString("driver_id");
                            bookings.status=jsonObject.getString("status");
                            bookings.sourceAddress=jsonObject.getString("source");
                            bookings.destinationAddress=jsonObject.getString("destination");
                            bookings.bookingtime=jsonObject.getString("bookingtime");
                            bookings.distance=jsonObject.getString("distance");
                            bookings.starttime=jsonObject.getString("starttime");
                            bookings.endtime=jsonObject.getString("endtime");
                            bookings.createdat=jsonObject.getString("created_at");
                            bookings.SourceLatitude = jsonObject.getString("source_lat");
                            bookings.SourceLongitude = jsonObject.getString("source_long");
                            bookings.DestinationLatitude = jsonObject.getString("dest_lat");
                            bookings.DestinationLongitude = jsonObject.getString("dest_long");
                            bookings.driverfeedback=jsonObject.getString("driver_feedback");
                            bookingdata.add(bookings);
                            bookings= new Openbookingdata();
                        }
                        Log.d("open", "" + String.valueOf(bookingdata.size()));
                        ((GlobalVaraible)mContext.getApplicationContext()).setCompletedbookiings(bookingdata);
                        if (msg!= null)
                        {
                            msg.what=1;
                            msg.sendToTarget();

                        }
                    } catch (JSONException e) {
                        Log.d("bookings",e.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",e.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("getallbookings",error.toString());
                    if(msg != null)
                    {
                        Bundle b = new Bundle();
                        b.putString("message",error.toString());
                        msg.what=0;
                        msg.setData(b);
                        msg.sendToTarget();
                    }
                }
            });
            string.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Globalclass.getInstance(mContext).makerequest(string);
        }

        if(action.equals("getPromos"))
        {
            JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getpromotions, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject json = null;
                    try {

                        json = new JSONObject( response.toString());
                        JSONArray data = json.optJSONArray("data");
                        Promos.clear();

                        for (int i =0;i<data.length();i++)
                        {
                            Promotions promotions = new Promotions();
                            JSONObject jsonObject = data.getJSONObject(i);
                            promotions.Code=jsonObject.getString("code");
                            promotions.Description=jsonObject.getString("description");
                            Promos.add(promotions);
                        }

                        ((GlobalVaraible)mContext.getApplicationContext()).setPromotions(Promos);
                        if (msg!= null)
                        {
                            msg.what=1;
                            msg.sendToTarget();

                        }
                    } catch (JSONException e) {
                        Log.d("bookings",e.toString());
                        if(msg != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",e.toString());
                            msg.what=0;
                            msg.setData(b);
                            msg.sendToTarget();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("getallbookings",error.toString());
                    if(msg != null)
                    {
                        Bundle b = new Bundle();
                        b.putString("message",error.toString());
                        msg.what=0;
                        msg.setData(b);
                        msg.sendToTarget();
                    }
                }
            });
            string.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Globalclass.getInstance(mContext).makerequest(string);
        }


    }


    }








