package driver.prototype.aptlegion.limocartdriver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Handler;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class getdata extends IntentService {
    static String getopenbookings,getallbookings,getdriverdata,getcompanyinfo;
    static Context mContext;
    HashMap<String,String>user = new HashMap<>();
    String action;
    openbookingdata bookings= new openbookingdata();
    ArrayList<openbookingdata>bookingdata,allbooking= new ArrayList<>();
    static Handler mhandler = null;
    static Message msg_pending,msg_accept,msg_driverdata,msg_all,msg_Company, msg_bookingbyid;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    HashMap<String, ArrayList<Vehicle>>carlist = new HashMap<>();


    public static void getpendingbookings(Context context,String token,Message message_pending)
    {
        msg_pending= message_pending;
        mContext= context;
        getopenbookings= "http://demo.olwebapps.com/api/bookings/pending?token="+token+"";
        Intent intent = new Intent(context, getdata.class);
        intent.setAction("getopenbookings");
        context.startService(intent);
    }
    public static void getacceptedbookings(Context context,String token,Message message_accpet)
    {
        msg_accept= message_accpet;
        mContext= context;
        getopenbookings= "http://demo.olwebapps.com/api/bookings/open?token="+token+"";
        Intent intent = new Intent(context, getdata.class);
        intent.setAction("getaccptedbookings");
        context.startService(intent);
    }
    public static void getallbookings(Context context,String token,Message message_all)
    {
        msg_all= message_all;
        mContext= context;
        getallbookings= "http://demo.olwebapps.com/api/bookings/all?token="+token+"";
        Intent intent = new Intent(context, getdata.class);
        intent.setAction("getallbookings");
        context.startService(intent);
    }
    public static void getdriverdata(Context context,String token,Message message){
        msg_driverdata=  message;
        mContext= context;
        getdriverdata= "http://demo.olwebapps.com/api/driver?token="+token+"";
        Intent intent = new Intent(context, getdata.class);
        intent.setAction("getdriverdata");
        context.startService(intent);
    }
    public static void getcompanyinfo(Context context,String token,Message message,String name){
        msg_Company=  message;
        mContext= context;
        getcompanyinfo= "http://demo.olwebapps.com/api/city/"+name;;
        Intent intent = new Intent(context, getdata.class);
        intent.setAction("getcompanyinfo");
        context.startService(intent);
    }


    public getdata() {

        super("getdata");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(Globalclass.getInstance(mContext).isnetworking()){
        if (intent != null) {
            action = intent.getAction();
            if(action.equals("getaccptedbookings"))
            {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getopenbookings, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject json = null;
                        try {
                            Log.d("response",response.toString());
                            json = new JSONObject( response.toString());
                            JSONArray data = json.optJSONArray("data");
                            bookingdata= new ArrayList<openbookingdata>();
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
                                bookings= new openbookingdata();
                            }
                            ((GlobalVaraible)mContext.getApplicationContext()).setAcceptedbooking(bookingdata);
                            if (msg_accept != null)
                            {
                                msg_accept.what=1;
                                msg_accept.sendToTarget();


                            }
                        } catch (JSONException e) {
                            Log.d("bookings",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (msg_accept != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg_accept.setData(b);
                            msg_accept.what=0;
                            msg_accept.sendToTarget();

                        }
                        Log.d("cus",error.toString());
                    }
                });
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
            }
            if (action.equals("getopenbookings")) {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getopenbookings, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject json = null;
                        try {
                            Log.d("response",response.toString());
                            json = new JSONObject( response.toString());
                            JSONArray data = json.optJSONArray("data");
                            bookingdata= new ArrayList<openbookingdata>();
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
                                bookings= new openbookingdata();
                            }
                            ((GlobalVaraible)mContext.getApplicationContext()).setBookingdata(bookingdata);
                            String t = msg_pending.getTarget().toString();
                            if (msg_pending != null)
                            {
                                msg_pending.what=1;
                                msg_pending.sendToTarget();

                            }
                        } catch (JSONException e) {
                            Log.d("bookings",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (msg_pending != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg_pending.setData(b);
                            msg_pending.what=0;
                            msg_pending.sendToTarget();

                        }
                        Log.d("cus",error.toString());
                    }
                });
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
            }
        }
            if(action.equals("getdriverdata"))
            {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getdriverdata, new Response.Listener<JSONObject>() {
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
                            user.put("address",data.getString("address"));
                            user.put("zip",data.getString("zip"));
                            JSONObject j = new JSONObject(data.getString("vehicletype").toString());
                            user.put("vehicletype",j.getString("name"));
                            ((GlobalVaraible) mContext.getApplicationContext()).setUserinfo(user);
                            Log.d("cus", ""+((GlobalVaraible) mContext.getApplicationContext()).getUserinfo().toString());
                        } catch (JSONException e) {
                            Log.d("cus", e.toString());
                        }
                        if (msg_driverdata != null)
                        {

                            msg_driverdata.what=1;
                            msg_driverdata.sendToTarget();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (msg_driverdata != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg_driverdata.setData(b);
                            msg_driverdata.what=0;
                            msg_driverdata.sendToTarget();
                        }
                        Log.d("cus",error.toString());
                    }});
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
            }
            if (action.equals("getallbookings")) {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET, getallbookings, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject json = null;
                        try {
                            Log.d("response", response.toString());
                            json = new JSONObject( response.toString());
                            JSONArray data = json.optJSONArray("data");
                            allbooking= new ArrayList<openbookingdata>();
                            allbooking.clear();
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
                                allbooking.add(bookings);
                                bookings= new openbookingdata();
                            }
                            ((GlobalVaraible)mContext.getApplicationContext()).setAllbookings(allbooking);
                            if (msg_all != null)
                            {
                                msg_all.what=1;
                                msg_all.sendToTarget();

                            }
                        } catch (JSONException e) {
                            Log.d("bookings",e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("cus",error.toString());
                        if (msg_all != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg_all.setData(b);
                            msg_all.what=0;
                            msg_all.sendToTarget();

                        }
                    }
                });
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Globalclass.getInstance(mContext).makerequest(string);
            }
            if (action.equals("getcompanyinfo")) {

                sharedPreferences = mContext.getApplicationContext().getSharedPreferences("MyPrefs_driver1", MODE_PRIVATE);;
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
                            if (msg_Company != null)
                            {
                                msg_Company.what=1;
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("info",carlist);
                                msg_Company.setData(bundle);
                                msg_Company.sendToTarget();

                            }
                            else {
                                Toast.makeText(getdata.this, "Launch The App Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("bookings",e.toString());
                            if(msg_Company != null)
                            {
                                Bundle b = new Bundle();
                                b.putString("message",e.toString());
                                msg_Company.what=0;
                                msg_Company.setData(b);
                                msg_Company.sendToTarget();
                            } else {
                                Toast.makeText(getdata.this, "Launch The App Again", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("getopenbookings",error.toString());
                        if(msg_Company != null)
                        {
                            Bundle b = new Bundle();
                            b.putString("message",error.toString());
                            msg_Company.what=0;
                            msg_Company.setData(b);
                            msg_Company.sendToTarget();
                        } else {
                            Toast.makeText(getdata.this, "Launch The App Again", Toast.LENGTH_SHORT).show();
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
}






