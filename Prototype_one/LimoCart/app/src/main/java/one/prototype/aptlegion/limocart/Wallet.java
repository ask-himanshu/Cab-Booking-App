package one.prototype.aptlegion.limocart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class Wallet extends AppCompatActivity {

    EditText Amount;
    String amount="";
    TextView balance;
    SharedPreferences sharedPreferences;
    Context context;
    HashMap<String, String> user = new HashMap<String, String>();
    String TAG = "Wallet";
    SwipeRefreshLayout swipeRefreshLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_wallet);
        Amount = (EditText) findViewById(R.id.Amountedittext);
        balance = (TextView) findViewById(R.id.t1);
        user = ((GlobalVaraible) getApplicationContext()).getUserinfo();
        balance.setText("Rs "+user.get("wallet"));
    }

    public void Addmoney(View view)
    {
        amount=Amount.getText().toString();
        if(!(amount.equals("")))
        {
            Intent intent = new Intent(Wallet.this,LoadBalance.class);
            intent.putExtra("amount",amount);
            startActivity(intent);
        }else
        {
            YoYo.with(Techniques.Shake).duration(700).playOn(Amount);
            Amount.setError("Wallet Amount Empty");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallet, menu);
        return true;
    }
    public void Pay(View view)
    {

    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
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
