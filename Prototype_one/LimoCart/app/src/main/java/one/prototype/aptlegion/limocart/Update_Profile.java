package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class Update_Profile extends AppCompatActivity {
    EditText fname,lname,email,mobileno,Address,city,zip,password,pold,pnew,pconfirm;
    AutoCompleteTextView country;
    HashMap<String, String> user,updateinfo = new HashMap<String, String>();
    JSONObject json_update;
    Handler handler_update;
    AlertDialog.Builder Dialog;
    AlertDialog passworddialog;
    Htttpcall httpupdate;
    AlertDialog.Builder dialog;
    String  updateurl ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActionProcessButton update;
    View passwordDialog;
    ProgressDialog progressDialog ;
    Handler j;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_update_profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        progressDialog.setMessage("Changing Password...");
        progressDialog.setCancelable(false);


        LayoutInflater layoutInflater =LayoutInflater.from(this);
        passwordDialog = layoutInflater.inflate(R.layout.password_change_dialog, null);
        passworddialog = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setPositiveButton("ok",null)
                .setNegativeButton("cancel",null)
                .setTitle("Change Password")
                .setView(passwordDialog)
                .setCancelable(false)
                .create();
        update = (ActionProcessButton)findViewById(R.id.update);
        update.setMode(ActionProcessButton.Mode.ENDLESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        user = ((GlobalVaraible)getApplicationContext()).getUserinfo();
        Dialog= new AlertDialog.Builder(this);
        pold = (EditText)passwordDialog.findViewById(R.id.p_old);
        pnew = (EditText)passwordDialog.findViewById(R.id.p_new);
        pconfirm =(EditText)passwordDialog.findViewById(R.id.p_confirm);
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
          editor= sharedPreferences.edit();
        fname = (EditText)findViewById(R.id.firstname);
        lname = (EditText)findViewById(R.id.lastname);
        email = (EditText)findViewById(R.id.email_address);
        mobileno = (EditText)findViewById(R.id.mobileno);
        Address = (EditText)findViewById(R.id.ad);
        city=(EditText)findViewById(R.id.city);
        zip=(EditText)findViewById(R.id.zip);
        country = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        password=(EditText)findViewById(R.id.pass);
        fname.setText(user.get("firstname"));
        lname.setText(user.get("lastname"));
        email.setText(user.get("loginid"));
        email.setEnabled(false);
        mobileno.setText(user.get("phone"));
        Address.setText(user.get("address"));
        city.setText(user.get("city"));
         // if(user.get("zip").equals("null"))
          //{
            //  zip.setText("");
          //}else{
            //  zip.setText(user.get("zip"));
          //}
          if(user.get("zip").equals("null")|(user.get("zip").equals("0")))
          {
              zip.setText("");
          }else{
              zip.setText(user.get("zip"));
          }

        country.setText(user.get("country"));
          progressDialog= new ProgressDialog(Update_Profile.this);
          progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        String [] Country = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> countryadapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Country);
        country.setAdapter(countryadapter);
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                if(id==MotionEvent.ACTION_DOWN)
                {
                    passworddialog.show();

                }
                return true;
            }
        });

        passworddialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button ok = passworddialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       final HashMap<String,String>params = new HashMap<String, String>();
                        params.put("old_password",pold.getText().toString().trim());
                        params.put("password",pnew.getText().toString().trim());
                        params.put("password_confirmation",pconfirm.getText().toString().trim());
                        JSONObject jsonObject  = new JSONObject(params);
                        Handler handler = new Handler(Looper.getMainLooper())
                        {

                            @Override
                            public void handleMessage(Message message)
                            {
                                if(message.what==1)
                                {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    Dialog.setMessage("Password Changed");
                                    Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    Dialog.show();

                                }
                                if(message.what==0)
                                {
                                    progressDialog.dismiss();
                                    Bundle b= message.getData();
                                    Dialog.setMessage(b.get("message").toString());
                                    Dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    Dialog.show();
                                }
                            }

                        };
                        progressDialog.show();
                        String token = sharedPreferences.getString("token",null);
                        String url = "http://demo.olwebapps.com/api/reset?token={" + token + "}";
                        Htttpcall htttpcall = new Htttpcall(Update_Profile.this,handler,url);
                        htttpcall.post(jsonObject.toString());

                    }
                });
                Button cancel = passworddialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pold.setText(null);
                        pconfirm.setText(null);
                        pnew.setText(null);
                        dialog.dismiss();
                    }
                });
            }
        });
          pold.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  int id = event.getAction();
                  pold.requestFocus();
                  switch (id)
                  {
                      case MotionEvent.ACTION_DOWN:

                          if(event.getRawX() >= (pold.getRight() - pold.getCompoundDrawables()[2].getBounds().width())) {
                              pold.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                          }else
                          {
                              return false;
                          }
                          break;
                      case MotionEvent.ACTION_UP:
                          if(event.getRawX() >= (pold.getRight() - pold.getCompoundDrawables()[2].getBounds().width())) {
                              pold.setInputType(129);
                          }
                          else
                          {
                              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                              return false;
                          }
                          break;
                  }

                  return true;

              }
          });
          pnew.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  int id = event.getAction();
                  pnew.requestFocus();
                  switch (id)
                  {
                      case MotionEvent.ACTION_DOWN:

                          if(event.getRawX() >= (pnew.getRight() - pnew.getCompoundDrawables()[2].getBounds().width())) {
                              pnew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                          }else
                          {
                              return false;
                          }
                          break;
                      case MotionEvent.ACTION_UP:
                          if(event.getRawX() >= (pnew.getRight() - pnew.getCompoundDrawables()[2].getBounds().width())) {
                              pnew.setInputType(129);
                          }
                          else
                          {
                              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                              imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                              return false;
                          }
                          break;
                  }

                  return true;

              }
          });

    }
    public void updateprofile(View v)
    {
        updateinfo.put("firstname",fname.getText().toString().trim());
        updateinfo.put("lastname",lname.getText().toString().trim());
        updateinfo.put("address", Address.getText().toString().trim());
        updateinfo.put("city",city.getText().toString().trim());
        updateinfo.put("zip",zip.getText().toString().trim());
        updateinfo.put("phone",mobileno.getText().toString().trim());
        updateinfo.put("country", country.getText().toString().trim());
        try{
            json_update = new JSONObject(updateinfo);}catch (Exception ex){
            Log.d("Error", "" + ex.toString());}

        handler_update = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.what == 1) {
                    progressDialog.dismiss();
                    dialog = new AlertDialog.Builder(Update_Profile.this);
                   update.setProgress(100);
                   update.setText("Updated");
                    dialog.setMessage("User Updated sucessfully");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putString("firstname",updateinfo.get("firstname"));
                            editor.putString("lastname", updateinfo.get("lastname"));
                            editor.commit();
                           Intent intent = new Intent(Update_Profile.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = j.obtainMessage();
                            message.sendToTarget();
                        }
                    };
                    timer.schedule(timerTask,1000);

                }
                if (inputMessage.what == 0) {
                    update.setProgress(-1);
                    update.setText("Update Failed");
                    Dialog.setTitle("User Updating Failed... ");
                    Bundle b = inputMessage.getData();
                    Dialog.setMessage(b.getString("message").toString());
                    Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Timer timer = new Timer();
                            TimerTask timerTask = new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = j.obtainMessage();
                                    message.sendToTarget();
                                }
                            };
                            timer.schedule(timerTask, 1000);
                        }
                    });
                    Dialog.show();

                }
            }
        };
        update.setText("Updating");
        update.setProgress(24);
        String token = sharedPreferences.getString("token",null);
        updateurl = "http://demo.olwebapps.com/api/customer/{" + user.get("id") + "}?token={" + token + "}";
        httpupdate = new Htttpcall(Update_Profile.this,handler_update,updateurl);
        httpupdate.put(json_update.toString());
        j = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message message)
            {
                update.setProgress(0);
                update.setText("Update");


            }
        };


    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        if(id==android.R.id.home)
        {
            Intent main = new Intent(this,MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }

        return super.onOptionsItemSelected(item);
    }
}
