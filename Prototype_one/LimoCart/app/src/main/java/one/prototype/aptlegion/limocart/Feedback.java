package one.prototype.aptlegion.limocart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONObject;

import java.util.HashMap;


public class Feedback extends AppCompatActivity {
    RatingBar ratingBar ;
    String BookingId,accept_url,Rating;
    SharedPreferences sharedPreferences;
    ActionProcessButton button;
    EditText custFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        button = (ActionProcessButton) findViewById(R.id.btnFeedback);
        button.setMode(ActionProcessButton.Mode.ENDLESS);
        custFeedback = (EditText) findViewById(R.id.comments);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        Intent intent = new Intent();
        intent = getIntent();
        BookingId = intent.getStringExtra("Bookingid");
    }
    public void feedback(View view)
    {
        accept_url = "http://demo.olwebapps.com/api/feedback/"+BookingId+"?token="+sharedPreferences.getString("token",null)+"";
        HashMap<String,String>params = new HashMap<>();
        params.put("feedback",String.valueOf(ratingBar.getRating()));
        params.put("feedback_comment",String.valueOf(custFeedback));
        JSONObject jsonObject = new JSONObject(params);
        Handler postfeedback = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
               if(message.what==1)
               {
                   button.setProgress(100);
                  Intent main = new Intent(Feedback.this,MyBookings.class);
                  startActivity(main);



               }
                if(message.what==0)
                {
                    button.setProgress(-1);
                    Intent main = new Intent(Feedback.this,MyBookings.class);
                    startActivity(main);

                }
            }
        };
        button.setProgress(22);
        Htttpcall htttpcall = new Htttpcall(this,postfeedback,accept_url);
        htttpcall.post(jsonObject.toString());

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent main = new Intent(Feedback.this,MyBookings.class);
        startActivity(main);
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
