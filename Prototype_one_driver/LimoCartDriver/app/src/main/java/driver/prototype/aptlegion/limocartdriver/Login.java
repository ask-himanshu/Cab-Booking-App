package driver.prototype.aptlegion.limocartdriver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class Login extends AppCompatActivity implements ProgressGenerator.OnCompleteListener {
    SharedPreferences pref,pref1;
    SharedPreferences.Editor editor ;
    CheckBox remeberme;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    EditText username,password;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    HashMap<String,String>params = new HashMap<>();
    Message message_getdata;
    boolean doubleBackToExitPressedOnce = false;
    ActionProcessButton btnSignIn;
    Gson gson =new Gson();
    int ran;
    Random random = new Random();
    Handler h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        pref = getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        pref1= getApplicationContext().getSharedPreferences("MyPrefs_driver1", MODE_PRIVATE);
        editor = pref.edit();
        remeberme = (CheckBox) findViewById(R.id.checkBox);
        username =(EditText)findViewById(R.id.editText);
        password=(EditText)findViewById(R.id.editText2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we connect to Server");
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        progressDialog.setCancelable(false);
        dialog = new AlertDialog.Builder(this);
        final Handler randomgenerator = new Handler();
        randomgenerator.postDelayed(new Runnable() {
            @Override
            public void run() {
                ran = random.nextInt(100);
            }
        }, generateDelay());
        h = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message)
            {
                btnSignIn.setText("Sign In");
                btnSignIn.setProgress(0);
            }
        };

    }
    private int generateDelay() {
        return random.nextInt(1000);
    }

    public void login(View v)
    {
        if(Globalclass.getInstance(this).isnetworking())
    {
        if(Globalclass.getInstance(this).isgpsenabled())
        {

            if (username.getText().toString().equals("")||password.getText().toString().equals(""))
            {   YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText));
                YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText2));
            } else
            {
                final String email = username.getText().toString().trim();
                if(email.matches(emailPattern))
                {
                    params.put("company",pref1.getString("Company",""));
                    params.put("loginid",username.getText().toString().trim().toLowerCase());
                    params.put("password",password.getText().toString().toString());
                    params.put("type","Driver");
                    JSONObject json = new JSONObject(params);
                    Handler handler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message inputMessage)
                        {

                            if(inputMessage.what ==1){
                                editor.clear();
                                Bundle b = inputMessage.getData();
                                try {
                                    String message = b.getString("message");
                                    JSONObject js = new JSONObject(message);
                                    editor.putString("token", js.getString("token"));
                                    ((GlobalVaraible)getApplicationContext()).setToken(js.getString("token"));
                                    editor.putString("Username",username.getText().toString().trim().toLowerCase());
                                    editor.putString("Password",password.getText().toString());
                                    editor.commit();

                                    if (remeberme.isChecked())
                                    {
                                       editor.putString("rememberme","true");
                                       editor.commit();
                                    }
                                    Handler  handler1 = new android.os.Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message inputMessage) {

                                            Handler h = new Handler(Looper.getMainLooper())
                                            { @Override
                                              public void handleMessage(Message inputMessage)
                                                {
                                                 if(inputMessage.what==1)
                                                 {
                                                    // progressDialog.dismiss();
                                                     HashMap<String,String>user = new HashMap<>();
                                                     user=((GlobalVaraible)getApplicationContext()).getUserinfo();
                                                     editor.putString("firstname",user.get("firstname"));
                                                     editor.putString("lastname",user.get("lastname"));
                                                     editor.commit();
                                                     btnSignIn.setProgress(100);
                                                     updateposition.updatepostion(Login.this);
                                                     Globalclass.getInstance(Login.this).startactivity(MainActivity.class);
                                                 }
                                                    if(inputMessage.what ==0)
                                                    {
                                                       // progressDialog.dismiss();
                                                        btnSignIn.setProgress(-1);
                                                        Bundle b= inputMessage.getData();
                                                        Toast.makeText(Login.this,b.get("messsage").toString(),Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            };
                                            Refreshdata r = new Refreshdata(Login.this,h);
                                            r.refreshdata();
                                        }};
                                    message_getdata = handler1.obtainMessage();
                                    getdata.getpendingbookings(Login.this,pref.getString("token",null), message_getdata);

                                } catch (JSONException e) {
                                    Log.d("res", e.toString());
                                }
                            }if(inputMessage.what==0){
                           // progressDialog.dismiss();
                            btnSignIn.setProgress(-1);
                            Bundle b = inputMessage.getData();
                            dialog.setTitle("Invalid LoginId or Password");
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    username.setText("");
                                    password.setText("");
                                    username.requestFocus();
                                }
                            });
                            dialog.setMessage(b.getString("message"));
                            dialog.show();

                        }

                        }
                    };
                    htttpcall http = new htttpcall(this,handler,"http://demo.olwebapps.com/api/login");
                   // progressDialog.show();
                   btnSignIn.setProgress(ran);


                    http.post(json.toString());


                }else
                {
                    YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText));
                    username.setError("Please enter correct Email address");

                }
        }

        }else
        {
            Toast.makeText(this, "Please Enable your GPS", Toast.LENGTH_SHORT).show();
        }

    }else
    {
        Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
    }
    }
public void register(View v){
    Intent signup = new Intent(this,createuser.class);
    startActivity(signup);
}

    @Override
    public void onBackPressed(){


        if (doubleBackToExitPressedOnce)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

         //   Intent intent = new Intent(Intent.ACTION_MAIN);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
         //   startActivity(intent);
         //   finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);


    }
    public void forget(View v)
    {
        if(username.getText().toString().equals(""))
        {
            username.setError("Please Enter Email address");
        }else
        {
            HashMap<String,String>params = new HashMap<>();
            params.put("loginid",username.getText().toString().trim());
            params.put("company",pref1.getString("Company", ""));
            params.put("type","Driver");
            JSONObject jsonObject = new JSONObject(params);
            Handler handler = new Handler(Looper.getMainLooper())
            {
                @Override
                public void handleMessage(Message message)
                {
                    if(message.what==1)
                    {
                        btnSignIn.setProgress(100);
                        btnSignIn.setText("Reset Successful");
                        dialog = new AlertDialog.Builder(Login.this);
                        dialog.setCancelable(false);
                        dialog.setMessage("Password Reset Successful, Reset Link sent to email ");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                username.setText("");
                            }
                        });
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = h.obtainMessage();
                                message.sendToTarget();
                            }
                        };
                        timer.schedule(timerTask, 1000);
                        dialog.show();
                    }

                    if(message.what==0)
                    {
                        btnSignIn.setProgress(-1);
                        btnSignIn.setText("Reset Failed");
                        Bundle b = message.getData();
                        dialog = new AlertDialog.Builder(Login.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(b.getString("message"));
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                username.setText("");
                            }
                        });
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = h.obtainMessage();
                                message.sendToTarget();
                            }
                        };
                        timer.schedule(timerTask, 1000);
                        dialog.show();
                    }
                }
            };
            btnSignIn.setText("Resetting...");
            btnSignIn.setProgress(22);
            htttpcall htttpcall = new htttpcall(Login.this,handler,"http://creatg.webserversystems.com/api/forgotPassword");
            htttpcall.post(jsonObject.toString());
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

    @Override
    public void onComplete() {

    }
}
