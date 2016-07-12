package driver.prototype.aptlegion.limocartdriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Feedback extends AppCompatActivity {
    RatingBar ratingBar ;
    String BookingId,accept_url,Rating;
    SharedPreferences sharedPreferences;
    ActionProcessButton button;
    Boolean Feedbackstatus = false;
    EditText custFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        button = (ActionProcessButton) findViewById(R.id.btnFeedback);
        custFeedback = (EditText) findViewById(R.id.comments);
        button.setMode(ActionProcessButton.Mode.ENDLESS);
        sharedPreferences = getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        Intent intent = new Intent();
        intent = getIntent();
        BookingId = intent.getStringExtra("BookingId");

    }
    public void feedback(View view)
    {
        accept_url = "http://demo.olwebapps.com/api/feedback/"+BookingId+"?token="+sharedPreferences.getString("token",null)+"";
        HashMap<String,String>params = new HashMap<>();
        params.put("feedback",String.valueOf(ratingBar.getRating()));
        JSONObject jsonObject = new JSONObject(params);
        Handler postfeedback = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
               if(message.what==1)
               {
                   Feedbackstatus=true;
                   Handler handler = new Handler(Looper.getMainLooper())
                   {
                       @Override
                     public void handleMessage(Message message1)
                     {

                         button.setProgress(100);
                         Intent main = new Intent(Feedback.this, MainActivity.class);
                         main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         startActivity(main);
                         ArrayList<openbookingdata> list,list1,list2 = new ArrayList<openbookingdata>();
                         list=((GlobalVaraible)getApplicationContext()).getAcceptedbooking();
                         list1=((GlobalVaraible)getApplicationContext()).getBookingdata();
                         list2=((GlobalVaraible)getApplicationContext()).getAllbookings();
                         acceptedbookinglist_adapter a = new acceptedbookinglist_adapter(list);
                         allbookingsadapter ab = new allbookingsadapter(list2);
                         mycard m = new mycard(list1);
                         m.notifyDataSetChanged();
                         a.notifyDataSetChanged();
                         ab.notifyDataSetChanged();

                         finish();
                     }
                   };
                   Refreshdata refreshdata = new Refreshdata(Feedback.this,handler);
                   refreshdata.refreshdata();
                }
                if(message.what==0)
                {
                    Feedbackstatus=false;
                    button.setProgress(-1);
                    button.setText("FeedBack Failed");

                }
            }
        };
        button.setProgress(22);
        htttpcall htttpcall = new htttpcall(this,postfeedback,accept_url);
        htttpcall.post(jsonObject.toString());

    }
    @Override
    public void onBackPressed()
    {
        if(Feedbackstatus)
        {
            Intent main = new Intent(Feedback.this,MainActivity.class);
            startActivity(main);
        }else
        {
            Toast.makeText(Feedback.this,"Please Provide Feedback",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
