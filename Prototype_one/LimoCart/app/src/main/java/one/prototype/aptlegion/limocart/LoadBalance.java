package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class LoadBalance extends ActionBarActivity {

    String[] paymentype={},cardtype={};
    Spinner Paymenttype,Cardtype;
    String c,p="";
    Intent intent;
    EditText Ammount,Cardno,Eyear,Emonth,Cvv;
    String Bookingid;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    JSONObject jsonObject;
    Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_balance);
        intent = new Intent();
        intent = getIntent();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Ammount = (EditText)findViewById(R.id.ammount);
        Cardno=(EditText)findViewById(R.id.Cardno);
        Eyear=(EditText)findViewById(R.id.Eyear);
        Emonth=(EditText)findViewById(R.id.Emonth);
        Cvv=(EditText)findViewById(R.id.Cvv);
        Cardtype = (Spinner)findViewById(R.id.Cardtype);
        pay = (Button)findViewById(R.id.PAY);
        Ammount.setText(intent.getStringExtra("amount"));
        cardtype = new String[]{"Visa","Master Card"};
        ArrayAdapter<String> card=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cardtype);
        Cardtype.setAdapter(card);
        Cardtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                c = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
      pay.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(c.equals(""))
              {
                  c=Cardtype.getSelectedItem().toString();
              }
              HashMap<String,String> params = new HashMap<>();
              params.put("amount",intent.getStringExtra("amount"));
              params.put("paymenttype","Card");
              params.put("cardtype",c);
              params.put("cardno",Cardno.getText().toString());
              params.put("expyear",Eyear.getText().toString());
              params.put("expmonth",Emonth.getText().toString());
              params.put("cvv", Cvv.getText().toString());
              jsonObject = new JSONObject(params);
              Handler handler = new Handler(Looper.getMainLooper())
              {
                  @Override
                  public void handleMessage(Message message)
                  {
                      if(message.what==1)
                      {
                          progressDialog.dismiss();
                          AlertDialog.Builder dialog = new AlertDialog.Builder(LoadBalance.this);
                          dialog.setMessage("Payment Sucessfull");
                          dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  Intent bookings = new Intent(LoadBalance.this,MainActivity.class);
                                  startActivity(bookings);

                              }

                          });
                          dialog.show();
                      }
                      if(message.what==0)
                      {
                          progressDialog.dismiss();
                          AlertDialog.Builder dialog = new AlertDialog.Builder(LoadBalance.this);
                          Bundle b = message.getData();
                          dialog.setTitle("Payment UnSucessfull");
                          dialog.setMessage(b.getString("message"));
                          dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();

                              }

                          });
                          dialog.setNegativeButton("Cancel Payment", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  Intent bookings = new Intent(LoadBalance.this, Wallet.class);
                                  startActivity(bookings);
                              }
                          });
                          dialog.show();

                      }
                  }
              };
              progressDialog.setMessage("Making Payment...");
              progressDialog.show();
              String url = "http://demo.olwebapps.com/api/loadwallet?token="+sharedPreferences.getString("token",null)+"";
              Htttpcall htttpcall = new Htttpcall(LoadBalance.this,handler,url);
              htttpcall.post(jsonObject.toString());
          }
      });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_balance, menu);
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
