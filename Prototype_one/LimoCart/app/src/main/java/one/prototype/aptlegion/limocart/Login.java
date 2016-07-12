package one.prototype.aptlegion.limocart;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class Login extends AppCompatActivity {

    CheckBox remeberme;
    SharedPreferences sharedpreferences, pref1;
    private boolean doubleBackToExitPressedOnce = false;
    EditText username, password, guestemail, phoneno;
    SharedPreferences.Editor editor;
    Userinfo user = new Userinfo();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    HashMap<String, String> params = new HashMap<String, String>();
    HashMap<String, String> u = new HashMap<>();
    ProgressDialog progressDialog;
    android.support.v7.app.AlertDialog.Builder Dialog;
    ActionProcessButton btnSignIn;
    Handler h;
    AlertDialog Guestdialog;
    View guestDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_login);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        guestDialog = layoutInflater.inflate(R.layout.guestdialog, null);
        Guestdialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setPositiveButton("ok", null)
                .setNegativeButton("cancel", null)
                .setTitle("Guest Login")
                .setView(guestDialog)
                .setCancelable(false)
                .create();
        guestemail = (EditText) guestDialog.findViewById(R.id.email_guest);
        phoneno = (EditText) guestDialog.findViewById(R.id.contactno);
        remeberme = (CheckBox) findViewById(R.id.checkBox);
        username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we connect to Server");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedpreferences.edit();
        editor.putString("companyname", "ZipZap");
        editor.commit();
        pref1 = getApplicationContext().getSharedPreferences("Companyname", MODE_PRIVATE);

        Guestdialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button ok = Guestdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (guestemail.getText().toString().equals("")) {
                            guestemail.setError("Please enter mailid");

                            guestemail.requestFocus();
                            guestemail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    guestemail.setError(null);
                                }
                            });

                        } else if (phoneno.getText().toString().equals("")) {

                            phoneno.setError("Please enter phone number");

                            phoneno.requestFocus();
                            phoneno.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    phoneno.setError(null);
                                }
                            });

                            // YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.contactno));
                            // YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.email_guest));
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("company", pref1.getString("companyname", ""));
                            params.put("phone", phoneno.getText().toString().trim());
                            params.put("loginid", guestemail.getText().toString().trim());
                            JSONObject jsonObject = new JSONObject(params);
                            Handler handler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message message) {
                                    progressDialog.dismiss();
                                    if (message.what == 1) {
                                        Bundle b = message.getData();
                                        String messages = b.getString("message");
                                        JSONObject js = null;
                                        try {
                                            js = new JSONObject(messages);
                                            editor.putString("token", js.getString("token"));
                                            editor.commit();
                                            Intent intent = new Intent(Login.this, GuestMain.class);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (message.what == 0) {
                                        Dialog = new AlertDialog.Builder(Login.this);
                                        Bundle b = message.getData();
                                        String messages = b.getString("message");
                                        Dialog.setMessage(messages);
                                        Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        Dialog.show();
                                    }


                                }
                            };
                            Htttpcall http = new Htttpcall(Login.this, handler, "http://demo.olwebapps.com/api/guest");
                            progressDialog.show();
                            http.post(jsonObject.toString());
                        }

                    }

                });
                Button cancel = Guestdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        Guestdialog.dismiss();
                    }
                });

            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                password.requestFocus();
                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width())) {
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width())) {
                            password.setInputType(129);
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
        h = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                btnSignIn.setText("Sign In");
                btnSignIn.setProgress(0);
            }
        };

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);

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

    public void login(View v) {
        if (username.getText().toString().equals("")) {
            YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText));
        } else if (password.getText().toString().equals("")) {
            YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText2));
        } else {
            final String email = username.getText().toString().trim();
            if (email.matches(emailPattern)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
                params.put("company", pref1.getString("companyname", ""));
                params.put("loginid", username.getText().toString().trim().toLowerCase());
                params.put("password", password.getText().toString());
                params.put("type", "Customer");
                JSONObject json = new JSONObject(params);
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {

                        if (inputMessage.what == 1) {

                            Bundle b = inputMessage.getData();
                            try {
                                String message = b.getString("message");
                                JSONObject js = new JSONObject(message);
                                editor.putString("token", js.getString("token"));
                                ((GlobalVaraible) getApplicationContext()).setToken(js.getString("token"));
                                editor.putString("Username", username.getText().toString());
                                editor.putString("Password", password.getText().toString());
                                editor.commit();

                                if (remeberme.isChecked()) {
                                    editor.putString("rememberme", "true");
                                    editor.commit();

                                }
                                if (Globalclass.getInstance(Login.this).isgpsenabled() && Globalclass.getInstance(Login.this).isgpsenabled()) {
                                    final Handler start = new Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message inputMessage) {
                                            if (inputMessage.what == 1) {
                                                u = ((GlobalVaraible) getApplicationContext()).getUserinfo();
                                                editor.putString("firstname", u.get("firstname"));
                                                editor.putString("lastname", u.get("lastname"));
                                                editor.commit();
                                                // progressDialog.dismiss();
                                                btnSignIn.setProgress(100);
                                                Intent main = new Intent(Login.this, MainActivity.class);
                                                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(main);
                                                finish();
                                            }
                                            if (inputMessage.what == 0) {
                                                Bundle b = inputMessage.getData();
                                                Toast.makeText(Login.this, b.getString("message").toString(), Toast.LENGTH_LONG).show();
                                                btnSignIn.setProgress(-1);
                                            }

                                        }
                                    };
                                    Getalldata r = new Getalldata(Login.this, start);
                                    r.getalldata();

                                } else {
                                    Toast.makeText(Login.this, "Please Enable your Gps and Internet", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Log.d("res", e.toString());
                            }
                        }
                        if (inputMessage.what == 0) {
                            //progressDialog.dismiss();
                            btnSignIn.setProgress(-1);
                            Bundle b = inputMessage.getData();
                            Dialog = new android.support.v7.app.AlertDialog.Builder(Login.this)
                                    .setTitle("Invalid LoginId or Password");
                            Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    username.setText("");
                                    password.setText("");
                                    username.requestFocus();

                                }
                            });
                            Dialog.setMessage(b.getString("message"));
                            Dialog.show();

                        }

                    }
                };
                Htttpcall http = new Htttpcall(this, handler, "http://demo.olwebapps.com/api/login");
                //  progressDialog.show();
                btnSignIn.setProgress(21);
                http.post(json.toString());

            } else {
                YoYo.with(Techniques.Shake).duration(700).playOn(findViewById(R.id.editText));
                username.setError("Please enter correct Email address");
            }

        }
    }

    public void register(View v) {
        Intent register = new Intent(this, Createuser.class);
        startActivity(register);
    }

    public void guest(View v) {
        Guestdialog.show();
    }

    public void forget(View v) {
        if (username.getText().toString().equals("")) {
            username.setError("Please Enter Email address");
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("loginid", username.getText().toString().trim());
            params.put("company", pref1.getString("companyname", ""));
            params.put("type", "Customer");
            JSONObject jsonObject = new JSONObject(params);
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    if (message.what == 1) {
                        btnSignIn.setProgress(100);
                        btnSignIn.setText("Reset Successful");
                        Dialog = new AlertDialog.Builder(Login.this);
                        Dialog.setCancelable(false);
                        Dialog.setMessage("Password Reset Successful, Reset Link sent to email ");
                        Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
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
                        Dialog.show();
                    }


                    if (message.what == 0) {
                        btnSignIn.setProgress(-1);
                        btnSignIn.setText("Reset Failed");
                        Bundle b = message.getData();
                        Dialog = new AlertDialog.Builder(Login.this);
                        Dialog.setCancelable(false);
                        Dialog.setMessage(b.getString("message"));
                        Dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        Dialog.show();
                    }
                }
            };
            btnSignIn.setText("Resetting...");
            btnSignIn.setProgress(22);
            Htttpcall htttpcall = new Htttpcall(Login.this, handler, "http://demo.olwebapps.com/api/forgotPassword");
            htttpcall.post(jsonObject.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

}
