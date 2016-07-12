package one.prototype.aptlegion.limocart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by hp on 11-Aug-15.
 */
public class Getalldata {
    Context mContext;
    Handler mHandler;
    Message mMessage;
    Getdata getdata;
    SharedPreferences sharedPreferences,pref1;
    public Getalldata(Context context, Handler handler)
    {
        this.mContext= context;
        this.mHandler= handler;
        pref1 = mContext.getApplicationContext().getSharedPreferences("Companyname",mContext.MODE_PRIVATE);
        sharedPreferences = mContext.getSharedPreferences("MyPrefs", mContext.MODE_PRIVATE);
    }
    public void getalldata()
    {
        if(mHandler != null)
        {
            mMessage=mHandler.obtainMessage();
        }
        Handler getcustomerdata = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what ==1)
                {
                    Handler getopenbookings = new Handler(Looper.getMainLooper())
                    {
                        @Override
                        public void handleMessage(Message inputMessage)
                        {
                            if(inputMessage.what==1) {
                                Handler getallbookings = new Handler(Looper.getMainLooper()) {
                                    @Override
                                    public void handleMessage(Message inputMessage) {
                                        Handler getcartypes = new Handler(Looper.getMainLooper())
                                        {
                                            @Override
                                            public void handleMessage(Message message)
                                            {
                                                if (message.what == 1) {
                                                    mMessage.what = 1;
                                                    mMessage.sendToTarget();
                                                }
                                                if (message.what == 0) {
                                                    Bundle b = message.getData();
                                                    mMessage.setData(b);
                                                    mMessage.what = 0;
                                                    mMessage.sendToTarget();
                                                }
                                            }
                                        };
                                        getdata = new Getdata();
                                        String companyname = pref1.getString("companyname","");
                                        getdata.getcompanyifo(mContext,"getcartypes",companyname,getcartypes);
                                    }
                                };
                                getdata = new Getdata();
                                getdata.startAction(mContext, "getallbookings", sharedPreferences.getString("token", null), getallbookings);
                            }
                            if (inputMessage.what==0)
                            {
                                Bundle b ;
                                b=inputMessage.getData();
                                mMessage.what=0;
                                mMessage.setData(b);
                                mMessage.sendToTarget();

                            }
                        }
                    };
                    getdata = new Getdata();
                    getdata.startAction(mContext,"getopenbookings",sharedPreferences.getString("token",null),getopenbookings);
                }
                if(inputMessage.what==0)
                {
                    if (inputMessage.what==0)
                    {
                        Bundle b ;
                        b=inputMessage.getData();
                        mMessage.what=0;
                        mMessage.setData(b);
                        mMessage.sendToTarget();

                    }
                }


            }

        };
        getdata = new Getdata();
        getdata.startAction(mContext,"getcustomerdata",sharedPreferences.getString("token",null),getcustomerdata);
    }
}
