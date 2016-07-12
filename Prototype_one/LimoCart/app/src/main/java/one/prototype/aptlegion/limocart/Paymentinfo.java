package one.prototype.aptlegion.limocart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class Paymentinfo extends AppCompatActivity {

    TextView Amount,Unitprice,Distance;
    String Bookingid;
    Intent pay;
    String driverbooking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentinfo);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        pay = new Intent();
        pay= getIntent();
        Amount = (TextView)findViewById(R.id.ammount);
        Unitprice=(TextView)findViewById(R.id.unitprice);
        Distance=(TextView)findViewById(R.id.distance);
       /* Minimumdistance=(TextView)findViewById(R.id.minimum_dist);
        Minimumfare=(TextView)findViewById(R.id.minimum_fare);*/
        if(pay.getStringExtra("amount")!=null)
        {
            Amount.setText("Amount :"+pay.getStringExtra("amount"));
            Unitprice.setText("Unit Price:"+pay.getStringExtra("unitprice"));
            Distance.setText("Distance:"+pay.getStringExtra("distance"));
          /*  Minimumfare.setText("Minimum Fare:"+pay.getStringExtra("minimum_fare"));
            Minimumdistance.setText("Minimum Distance"+pay.getStringExtra("minimum_dist"));*/
            Bookingid=pay.getStringExtra("Bookingid");
        }

    }
   public void pay(View view)
    {
        Intent payment = new Intent(Paymentinfo.this,CardPage.class);
            payment.putExtra("amount",pay.getStringExtra("amount"));
            payment.putExtra("BookingId",pay.getStringExtra("Bookingid"));
            startActivity(payment);


    }
    public void onBackPressed()
    {

        Intent main = new Intent(Paymentinfo.this,MyBookings.class);
        startActivity(main);
    }

}
