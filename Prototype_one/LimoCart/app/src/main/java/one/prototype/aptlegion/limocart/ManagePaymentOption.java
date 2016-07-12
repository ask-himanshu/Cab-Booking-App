package one.prototype.aptlegion.limocart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManagePaymentOption extends AppCompatActivity {

    TextView Amount, Distance;
    Button prePayment;

    Intent prepaid;
    String Bookingid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payment_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Amount = (TextView) findViewById(R.id.amount);
        Distance = (TextView) findViewById(R.id.distance);
        prePayment = (Button) findViewById(R.id.btn_pay);


        prepaid = getIntent();
        Distance.setText("" + prepaid.getStringExtra(Constants.KEY_DISTANCE));
        Amount.setText("" + prepaid.getStringExtra(Constants.KEY_ESTIMATED_COST));
        Bookingid= prepaid.getStringExtra(Constants.KEY_BOOKINGID);

        prePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cardIntent = new Intent(ManagePaymentOption.this,CardPage.class);
                cardIntent.putExtra("amount",prepaid.getStringExtra(Constants.KEY_ESTIMATED_COST));
                cardIntent.putExtra("BookingId",prepaid.getStringExtra(Constants.KEY_BOOKINGID));
                startActivity(cardIntent);
            }
        });


    }

  /*  private void Prepaid(View view) {

        Intent prepay = new Intent(ManagePaymentOption.this,CardPage.class);
        prepay.putExtra("amount", Prepaid.getStringExtra(Constants.KEY_ESTIMATED_COST));
        prepay.putExtra("BookingId", Prepaid.getStringExtra(Constants.KEY_BOOKINGID));
        startActivity(prepay);


    }
*/    public void onBackPressed()
    {

        Intent main = new Intent(ManagePaymentOption.this,MyBookings.class);
        startActivity(main);
    }



}
