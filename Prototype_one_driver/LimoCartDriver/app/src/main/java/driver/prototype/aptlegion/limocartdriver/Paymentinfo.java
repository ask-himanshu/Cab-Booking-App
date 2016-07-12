package driver.prototype.aptlegion.limocartdriver;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class Paymentinfo extends ActionBarActivity {

    TextView Amount,Unitprice,Distance;
    String Bookingid;
    Intent pay;
    String driverbooking;
    Boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout mDrawerLayout;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentinfo);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        button = (Button)findViewById(R.id.button2);
        pay = new Intent();
        pay= getIntent();
        Amount = (TextView)findViewById(R.id.ammount);
        Unitprice=(TextView)findViewById(R.id.unitprice);
        Distance=(TextView)findViewById(R.id.distance);
      /*  Minimumdistance=(TextView)findViewById(R.id.minimum_dist);
        Minimumfare=(TextView)findViewById(R.id.minimum_fare);*/
        if(pay.getStringExtra("amount")!=null)
        {
            Amount.setText("Amount: "+pay.getStringExtra("amount"));
            Unitprice.setText("Unit Price:"+pay.getStringExtra("unitprice"));
            Distance.setText("Distance: "+pay.getStringExtra("distance"));
           /* Minimumfare.setText("Minimum Fare: "+pay.getStringExtra("minimum_fare"));
            Minimumdistance.setText("Minimum Distance"+pay.getStringExtra("minimum_dist"));*/
            Bookingid=pay.getStringExtra("Bookingid");
            driverbooking=pay.getStringExtra("driverbookig");
            if(!driverbooking.equals("true"))
            {
               button.setText("Feedback");
            }

        }

    }
   public void pay(View view)
    {
        if(driverbooking.equals("true"))
        {

            Intent payment = new Intent(Paymentinfo.this,CardPage.class);
            payment.putExtra("amount",pay.getStringExtra("amount"));
            payment.putExtra("BookingId",pay.getStringExtra("Bookingid"));
            startActivity(payment);
        }else {
            Intent payment = new Intent(Paymentinfo.this,Feedback.class);
            payment.putExtra("BookingId",pay.getStringExtra("Bookingid"));
            startActivity(payment);

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paymentinfo, menu);
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
