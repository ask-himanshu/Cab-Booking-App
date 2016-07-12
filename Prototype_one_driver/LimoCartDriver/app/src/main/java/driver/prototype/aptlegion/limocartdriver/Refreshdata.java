package driver.prototype.aptlegion.limocartdriver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;


/**
 * Created by Elitebook on 02-Jul-15.
 */
public class Refreshdata {
    Context mContext;
    Handler mHandler;
    String Token;
    SharedPreferences pref;
    Message message_refresh;
    public Refreshdata(Context context,Handler handler) {
        this.mContext=context;
        this.mHandler = handler;
        pref=mContext.getApplicationContext().getSharedPreferences("MyPrefs_driver", mContext.MODE_PRIVATE);

    }

    public void refreshdata()
    {
        if(mHandler != null)
        {
            message_refresh=mHandler.obtainMessage();
        }
        Handler getpendingbookings = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what==0)
                {
                    Bundle b = inputMessage.getData();
                    message_refresh.setData(b);
                    message_refresh.what =0;
                    message_refresh.sendToTarget();
                }
                if(inputMessage.what==1)
                {
                   Handler getacceptedbookings = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message inputMessage)
                        {
                            if(inputMessage.what==0)
                            {
                                Bundle b = inputMessage.getData();
                                message_refresh.setData(b);
                                message_refresh.what =0;
                                message_refresh.sendToTarget();
                            }
                            if(inputMessage.what==1)
                            {
                             Handler getallbookings = new Handler(Looper.getMainLooper())
                             {
                                 @Override
                                 public void handleMessage(Message inputMessage)
                                 {
                                     if(inputMessage.what==0)
                                     {
                                         Bundle b = inputMessage.getData();
                                         message_refresh.setData(b);
                                         message_refresh.what =0;
                                         message_refresh.sendToTarget();
                                     }
                                     if(inputMessage.what ==1)
                                     {
                                         Handler getdriverdata = new Handler(Looper.getMainLooper())
                                         {
                                             @Override
                                             public void handleMessage(Message inputMessage)
                                             {
                                                 if (inputMessage.what ==1)
                                                 {
                                                     message_refresh.what =1;
                                                     message_refresh.sendToTarget();
                                                 }
                                                 if(inputMessage.what==0)
                                                 {
                                                     Bundle b = inputMessage.getData();
                                                     message_refresh.setData(b);
                                                     message_refresh.what =0;
                                                     message_refresh.sendToTarget();
                                                 }

                                             }
                                         };
                                         Message m = getdriverdata.obtainMessage();
                                         getdata.getdriverdata(mContext, pref.getString("token", null),m);

                                     }
                                 }
                             };
                                Message message = getallbookings.obtainMessage();
                                getdata.getallbookings(mContext, pref.getString("token", null), message);

                            }
                        }
                    };
                    Message message_getdata_accepted = getacceptedbookings.obtainMessage();
                    getdata.getacceptedbookings(mContext, pref.getString("token", null), message_getdata_accepted);

                }
            }
        };
       Message message_getdata = getpendingbookings.obtainMessage();
       getdata.getpendingbookings(mContext, pref.getString("token",null), message_getdata);

    }
}
