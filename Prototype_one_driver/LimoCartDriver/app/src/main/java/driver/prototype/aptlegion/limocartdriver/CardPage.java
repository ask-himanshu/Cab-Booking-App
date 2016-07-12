package driver.prototype.aptlegion.limocartdriver;

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
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class CardPage extends ActionBarActivity {

    String[] paymentype={},cardtype={};
    Spinner Paymenttype,Cardtype;
    String c,p="";
    Intent intent;
    EditText Ammount,Cardno,Eyear,Emonth,Cvv;
    String Bookingid;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_page);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedPreferences =getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        Ammount = (EditText)findViewById(R.id.ammount);
        Cardno=(EditText)findViewById(R.id.Cardno);
        Eyear=(EditText)findViewById(R.id.Eyear);
        Emonth=(EditText)findViewById(R.id.Emonth);
        Cvv=(EditText)findViewById(R.id.Cvv);
        intent = new Intent();
        intent= getIntent();
        Ammount.setText(intent.getStringExtra("amount"));
        Bookingid=intent.getStringExtra("BookingId").toString();
        paymentype= new String[]{"Card", "Wallet"};
        cardtype = new String[]{"Visa","Master Card"};
        Paymenttype = (Spinner)findViewById(R.id.Paymentype);
        Cardtype = (Spinner)findViewById(R.id.Cardtype);
        ArrayAdapter<String>pay = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,paymentype);
        Paymenttype.setAdapter(pay);
        ArrayAdapter<String>card=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cardtype);
        Cardtype.setAdapter(card);
        Paymenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                p = parent.getItemAtPosition(position).toString();
                if(p.equals("Wallet"))
                {
                    Cardtype.setVisibility(View.GONE);
                    Cardno.setVisibility(View.GONE);
                    Eyear.setVisibility(View.GONE);
                    Emonth.setVisibility(View.GONE);
                    Cvv.setVisibility(View.GONE);
                }
                if(p.equals("Card"))
                {
                    Cardtype.setVisibility(View.VISIBLE);
                    Cardno.setVisibility(View.VISIBLE);
                    Eyear.setVisibility(View.VISIBLE);
                    Emonth.setVisibility(View.VISIBLE);
                    Cvv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
       Cardtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               c= parent.getItemAtPosition(position).toString();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });
    }
    public void Pay(View view)
    {
        if(p.equals(""))
        {
           p= Paymenttype.getSelectedItem().toString();

        }
        if(c.equals(""))
        {
            c=Cardtype.getSelectedItem().toString();
        }
        if(p.equals("Wallet"))
        {
            HashMap<String,String>params = new HashMap<>();
            params.put("amount",intent.getStringExtra("amount"));
            params.put("paymenttype",p);
            jsonObject = new JSONObject(params);
        }else
        {
            HashMap<String,String>params = new HashMap<>();
            params.put("amount",intent.getStringExtra("amount"));
            params.put("paymenttype",p);
            params.put("cardtype",c);
            params.put("cardno",Cardno.getText().toString());
            params.put("expyear",Eyear.getText().toString());
            params.put("expmonth",Emonth.getText().toString());
            params.put("cvv",Cvv.getText().toString());
            jsonObject = new JSONObject(params);
        }

        Handler handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    progressDialog.dismiss();
                    Intent payment = new Intent(CardPage.this, Feedback.class);
                    payment.putExtra("BookingId", Bookingid);
                    startActivity(payment);
                }
                if(message.what==0)
                {
                    progressDialog.dismiss();
                    progressDialog.dismiss();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CardPage.this);
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
                            Intent main = new Intent(CardPage.this, MainActivity.class);
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
                    });
                    dialog.show();


                }
            }
        };
        progressDialog.setMessage("Making Payment");
        progressDialog.show();
        String url = "http://demo.olwebapps.com/api/payment/"+Bookingid+"?token="+sharedPreferences.getString("token",null)+"";
        htttpcall htttpcall = new htttpcall(CardPage.this,handler,url);
        htttpcall.post(jsonObject.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_page, menu);
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
