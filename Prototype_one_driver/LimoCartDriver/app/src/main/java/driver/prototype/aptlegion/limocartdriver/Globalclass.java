package driver.prototype.aptlegion.limocartdriver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Elitebook on 6/3/2015.
 */
public class Globalclass {
    private static Globalclass mInstance;
    private static Context mCtx;
    SharedPreferences.Editor Edit;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    ArrayList<String>message=new ArrayList<>();
    AlertDialog.Builder dialog;
    String token;
    public void setToken(String token)
    {
        this.token = token;
    }
    public String getToken ()
    {
        return this.token;
    }

    public Globalclass(Context c){
        mCtx=c;
    }
    public static synchronized Globalclass getInstance(Context context){

        if(mInstance==null)
        {
            mInstance =new Globalclass(context);
            return mInstance;
        }
        return mInstance;
    }
    public void startactivity(Class myClass)
    {
        Intent start = new Intent(mCtx, myClass);
        mCtx.startActivity(start);
    }
    public boolean isgpsenabled()
    {
        LocationManager loc =(LocationManager)mCtx.getSystemService(Context.LOCATION_SERVICE);
        return loc.isProviderEnabled(LocationManager.GPS_PROVIDER) &&loc.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
        public boolean isnetworking()
    {

        ConnectivityManager connectivityManager = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    public void callactivity(String select){
        switch (select)
        {

            case  "Logout":
                sharedPreferences = mCtx.getSharedPreferences("MyPrefs", mCtx.MODE_PRIVATE);
                Edit=sharedPreferences.edit();
                Edit.clear();
                Edit.commit();
                Intent logout = new Intent(mCtx,Login.class);
                logout.addCategory(Intent.CATEGORY_HOME);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                mCtx.startActivity(logout);

                break;
        }
    }
    public void makerequest(com.android.volley.Request request)
    {
        if(requestQueue == null){
            requestQueue =  Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        requestQueue.add(request);
    }

    public ArrayList<String> processerrorjson(VolleyError error)
    {
        if(error.networkResponse != null && error.networkResponse.data != null){
            VolleyError error1 = new VolleyError(new String(error.networkResponse.data));
            error = error1;
        }
        try {

            JSONObject j = new JSONObject(error.getMessage().toString());
            int jarray_length=j.length();
            if(jarray_length>=1)
            {
                 JSONArray jarray = j.getJSONArray("errors");
            message.clear();
            for(int i=0;i<jarray.length();i++)
            {
                message.add(jarray.getString(i));

            }
            Log.d("res", "" + message.toString());

            }
            else
            {
             message.clear();
                message.add(j.getString("error"));
            }



        } catch (Exception e) {
            Log.d("rese", e.toString());
            message.clear();
          //  message.add("Something Went Wrong...");
        }
        return message;
    }
    public  void showdialog(String mes,String info)
    {
        dialog= new AlertDialog.Builder(mCtx);
        switch(mes)
        {
            case "sucess":

                dialog= new AlertDialog.Builder(mCtx);
                dialog.setTitle("User Created sucessfully");
                dialog.setMessage("User Created sucessfully");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        

                    }
                });
                dialog.show();
                break;
            case "failed":
                dialog= new AlertDialog.Builder(mCtx);
                dialog.setTitle("Failed ");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setMessage(info);
                dialog.show();
        }



}
}
