package one.prototype.aptlegion.limocart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


/**
 * Created by Elitebook on 06-Jul-15.
 */
public class Refreshdata {
    Context mContext;
    Handler mHandler;
    Message mMessage;
    Getdata getdata;
    SharedPreferences sharedPreferences;
    public Refreshdata(Context context, Handler handler)
    {
        this.mContext= context;
        this.mHandler= handler;
        sharedPreferences = mContext.getSharedPreferences("MyPrefs", mContext.MODE_PRIVATE);
    }
    public void refreshdata()
    {
      if(mHandler != null)
      {
          mMessage=mHandler.obtainMessage();
      }
       Handler getopenbookings = new Handler(Looper.getMainLooper())
                  {
                      @Override
                      public void handleMessage(Message inputMessage)
                      {
                          if(inputMessage.what==1) {
                              Handler getallbookings = new Handler(Looper.getMainLooper()) {
                                  @Override
                                  public void handleMessage(Message inputMessage) {

                                      if (inputMessage.what == 1) {
                                          Handler getcompletedbooking = new Handler(Looper.getMainLooper())
                                          {
                                              @Override
                                              public void handleMessage(Message inputMessage)
                                              {
                                                  if(inputMessage.what==1)
                                                  {
                                                      mMessage.what = 1;
                                                      mMessage.sendToTarget();
                                                  }
                                                  if(inputMessage.what==0)
                                                  {
                                                      Bundle b = inputMessage.getData();
                                                      mMessage.setData(b);
                                                      mMessage.what = 0;
                                                      mMessage.sendToTarget();
                                                  }

                                              }

                                          };
                                          Getdata.startAction(mContext, "getcompletedbookings", sharedPreferences.getString("token", null), getcompletedbooking);

                                      }
                                      if(inputMessage.what==0)
                                      {
                                          Bundle b = inputMessage.getData();
                                          mMessage.setData(b);
                                          mMessage.what = 0;
                                          mMessage.sendToTarget();
                                      }

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



          }




