package driver.prototype.aptlegion.limocartdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONObject;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class updateprofile extends ActionBarActivity {
    HashMap<String,String>userdata,updateinfo=new HashMap<String,String>();
    EditText Fname,Lname,Address,City,Zip,pold,pnew,pconfirm,password;
    JSONObject json_update;
    Handler handler_update;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    String message,updateurl,token;
    htttpcall httpupdate;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AutoCompleteTextView Country;
    AlertDialog passworddialog;
    View passwordDialog;
    AlertDialog.Builder Dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        LayoutInflater layoutInflater =LayoutInflater.from(this);
        passwordDialog = layoutInflater.inflate(R.layout.password_change_dialog, null);
        passworddialog = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setPositiveButton("ok",null)
                .setNegativeButton("cancel",null)
                .setTitle("Change Password")
                .setView(passwordDialog)
                .setCancelable(false)
                .create();
        Dialog= new AlertDialog.Builder(this);
        pold = (EditText)passwordDialog.findViewById(R.id.p_old);
        pnew = (EditText)passwordDialog.findViewById(R.id.p_new);
        pconfirm =(EditText)passwordDialog.findViewById(R.id.p_confirm);
        sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        editor= sharedPreferences.edit();
        userdata=((GlobalVaraible)getApplicationContext()).getUserinfo();
        Fname =(EditText)findViewById(R.id.firstname);
        Lname =(EditText)findViewById(R.id.lastname);
        Address=(EditText)findViewById(R.id.ad);
        City=(EditText)findViewById(R.id.city);
        Zip=(EditText)findViewById(R.id.zip);
        Country=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        Fname.setText(userdata.get("firstname"));
        Lname.setText(userdata.get("lastname"));
        Address.setText(userdata.get("address"));
        City.setText(userdata.get("city"));
        if(userdata.get("zip").equals("null")|(userdata.get("zip").equals("0")))
        {
            Zip.setText("");
        }else{
            Zip.setText(userdata.get("zip"));
        }
     //   Zip.setText(userdata.get("zip"));
        Country.setText(userdata.get("country"));
        password=(EditText)findViewById(R.id.pass);
        progressDialog= new ProgressDialog(updateprofile.this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        String[] Con = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> countryadapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Con);
        Country.setAdapter(countryadapter);
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                if (id == MotionEvent.ACTION_DOWN) {
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

                        final HashMap<String, String> params = new HashMap<String, String>();
                        params.put("old_password", pold.getText().toString().trim());
                        params.put("password", pnew.getText().toString().trim());
                        params.put("password_confirmation", pconfirm.getText().toString().trim());
                        JSONObject jsonObject = new JSONObject(params);
                        Handler handler = new Handler(Looper.getMainLooper()) {

                            @Override
                            public void handleMessage(Message message) {
                                if (message.what == 1) {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    Dialog.setMessage("Password Changed");
                                    Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            pold.setText("");
                                            pnew.setText("");
                                            pconfirm.setText("");
                                            pold.requestFocus();
                                        }
                                    });
                                    Dialog.show();

                                }
                                if (message.what == 0) {
                                    progressDialog.dismiss();
                                    Bundle b = message.getData();
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
                        progressDialog.setMessage("Password Changing");
                        String token = sharedPreferences.getString("token", null);
                        String url = "http://demo.olwebapps.com/api/reset?token={" + token + "}";
                        htttpcall htttpcall = new htttpcall(updateprofile.this, handler, url);
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
                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (pnew.getRight() - pnew.getCompoundDrawables()[2].getBounds().width())) {
                            pnew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (pnew.getRight() - pnew.getCompoundDrawables()[2].getBounds().width())) {
                            pnew.setInputType(129);
                        } else {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            return false;
                        }
                        break;
                }

                return true;

            }
        });


    }
public void Update(View view)
{
    updateinfo.put("firstname",Fname.getText().toString().trim());
    updateinfo.put("lastname",Lname.getText().toString().trim());
    updateinfo.put("address",Address.getText().toString().trim());
    updateinfo.put("city",City.getText().toString().trim());
    updateinfo.put("zip",Zip.getText().toString().trim());
    updateinfo.put("country", Country.getText().toString().trim());

    try{
        json_update = new JSONObject(updateinfo);
    }catch (Exception ex){
        Log.d("Error", "" + ex.toString());}

    handler_update = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            progressDialog.dismiss();
            if (inputMessage.what == 1) {
                progressDialog.dismiss();
                dialog= new AlertDialog.Builder(updateprofile.this);
                dialog.setMessage("User Updated sucessfully");
               /* HashMap<String,String>user ;
                user=((GlobalVaraible)getApplicationContext()).getUserinfo();*/

                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        editor.putString("firstname",updateinfo.get("firstname"));
                        editor.putString("lastname", updateinfo.get("lastname"));
                        editor.commit();
                        Intent intent = new Intent(updateprofile.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();

            }
            if (inputMessage.what == 0) {
                progressDialog.dismiss();
                dialog= new AlertDialog.Builder(updateprofile.this);
                dialog.setTitle("User Updation Failed ");
                Bundle b = inputMessage.getData();
                message = b.getString("message");
                dialog.setMessage(message);
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    };
    progressDialog.show();
    progressDialog.setMessage("Updating user profile");
    token=sharedPreferences.getString("token",null);
    updateurl = "http://demo.olwebapps.com/api/driver/{" + userdata.get("id") + "}?token={" + token + "}";
    httpupdate = new htttpcall(updateprofile.this,handler_update,updateurl);
    httpupdate.put(json_update);
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_updateprofile, menu);
        return true;
    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this, MainActivity.class);
      //  main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
