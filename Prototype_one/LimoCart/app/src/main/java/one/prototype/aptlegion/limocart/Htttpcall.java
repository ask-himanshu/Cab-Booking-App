package one.prototype.aptlegion.limocart;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Elitebook on 11-Jun-15.
 */
public class Htttpcall {
    Handler mhandler;
    Message msg;
    Bundle b= new Bundle();
    Context mcontext;
    String postmessage;
    ArrayList<String>errormessage = new ArrayList<>();
    String url;
    JSONObject oj;
    public Htttpcall(Context context)
    {
        this.mcontext = context;
    }

    public Htttpcall(Context context, Handler handler, String url)
    {
        this.mhandler = handler;

        this.mcontext = context;
        this.url=url;
    }
    public void put(String message)
    {
        try {
            oj = new JSONObject(message);
        }catch (Exception ex)
        {
            Log.d("rese",ex.toString());
        }

        Runnable put= new Runnable() {
            @Override
            public void run() {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.PUT, url, oj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        msg = mhandler.obtainMessage();
                        msg.what = 1;
                        Bundle b1 = new Bundle();
                        try {
                            JSONObject js = new JSONObject(response.toString());
                            b1.putString("message", js.toString());
                            msg.setData(b1);
                            msg.sendToTarget();
                        } catch (JSONException e) {
                            Log.d("res", e.toString());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("puterror",error.toString());
                        errormessage = Globalclass.getInstance(mcontext).processerrorjson(error);
                        StringBuilder builder = new StringBuilder();
                        for (String value : errormessage) {
                            builder.append(value);
                            builder.append(System.getProperty("line.separator"));
                        }
                        b.putString("message", builder.toString());
                        msg = mhandler.obtainMessage();
                        msg.setData(b);
                        msg.what = 0;
                        if(msg != null){ msg.sendToTarget();}


                    }
                }
                );
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if (Globalclass.getInstance(mcontext).isnetworking()) {

                    Globalclass.getInstance(mcontext).makerequest(string);
                } else {
                    Toast.makeText(mcontext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        };
        put.run();
    }
    public void post(String message){
        try {
            oj = new JSONObject(message);
        }catch (Exception ex)
        {
            Log.d("rese",ex.toString());
        }

        this.postmessage = message;
    Runnable post=    new Runnable()
        {

            @Override
            public void run() {
                JsonObjectRequest string = new JsonObjectRequest(com.android.volley.Request.Method.POST,url,oj, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(mhandler != null)
                        {
                            msg=mhandler.obtainMessage();
                            msg.what=1;
                        }
                        Bundle b = new Bundle();
                        try {
                            JSONObject js = new JSONObject(response.toString());
                            b.putString("message", js.toString());
                            if(msg != null)
                            {
                                msg.setData(b);
                                msg.sendToTarget();
                            }

                        } catch (JSONException e) {
                            Log.d("jsonexe",e.toString());
                        }



                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("posterror",""+error.toString());
                        errormessage=Globalclass.getInstance(mcontext).processerrorjson(error);
                        StringBuilder builder = new StringBuilder();
                        for (String value : errormessage) {
                            builder.append(value);
                            builder.append(System.getProperty("line.separator"));
                        }

                        b.putString("message", builder.toString());
                        if(mhandler != null)
                        {
                            msg = mhandler.obtainMessage();
                        }
                        if(msg != null)
                        {   msg.setData(b);
                            msg.what=0;
                            msg.sendToTarget();
                        }


                    }}
                );
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if(Globalclass.getInstance(mcontext).isnetworking())
                {

                    Globalclass.getInstance(mcontext).makerequest(string);
                }else {
                    Toast.makeText(mcontext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        };
        post.run();
    }
    public void get(){
        Runnable get=    new Runnable()
        {

            @Override
            public void run() {
                JsonObjectRequest string = new JsonObjectRequest(Request.Method.GET,url, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(mhandler != null)
                        {
                            msg=mhandler.obtainMessage();
                            msg.what=1;
                        }
                        Bundle b = new Bundle();
                        try {
                            JSONObject js = new JSONObject(response.toString());
                            b.putString("message", js.toString());
                            if(msg != null)
                            {
                                msg.setData(b);
                                msg.sendToTarget();
                            }

                        } catch (JSONException e) {
                            Log.d("jsonexe",e.toString());
                        }
                   }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("posterror",""+error.toString());
                        errormessage=Globalclass.getInstance(mcontext).processerrorjson(error);
                        StringBuilder builder = new StringBuilder();
                        for (String value : errormessage) {
                            builder.append(value);
                            builder.append(System.getProperty("line.separator"));
                        }

                        b.putString("message", builder.toString());
                        if(mhandler != null)
                        {
                            msg = mhandler.obtainMessage();
                        }
                        if(msg != null)
                        {   msg.setData(b);
                            msg.what=0;
                            msg.sendToTarget();
                        }


                    }}
                );
                string.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if(Globalclass.getInstance(mcontext).isnetworking())
                {

                    Globalclass.getInstance(mcontext).makerequest(string);
                }else {
                    Toast.makeText(mcontext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        };
        get.run();
    }

}
