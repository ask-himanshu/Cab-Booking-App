package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import org.json.JSONObject;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

public class Createuser extends AppCompatActivity {
    AutoCompleteTextView country;
    EditText edit1,edit2,edit3,edit4,edit5,edit6,address,city,zip;
    Boolean Valdidate = false;
    View parent;
    ViewGroup vg ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    HashMap<String, String> user,updateinfo = new HashMap<String, String>();
    HashMap<String, String> params = new HashMap<>();
    AlertDialog.Builder dialog;
    ProgressDialog progressDialog;
    String message;
    Bundle b = new Bundle();
    Button button;
    Drawable img;
    RelativeLayout relativeLayout;
    String updateurl,token;
    Handler handler_update;
    Htttpcall httpupdate;
    JSONObject json_update;
    SharedPreferences sharedPreferences;


      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);
          if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
              // Here activity is brought to front, not created,
              // so finishing this will get you to the last viewed activity
              finish();
              return;
          }
          Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
          sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
          img= this.getResources().getDrawable(R.drawable.edit);
          img.setBounds( 0, 0, 20, 20 );
          edit1 =(EditText)findViewById(R.id.editText3);
          edit2 =(EditText)findViewById(R.id.editText4);
          edit3 =(EditText)findViewById(R.id.editText5);
          edit4 =(EditText)findViewById(R.id.editText6);
          edit5 =(EditText)findViewById(R.id.editText7);
          edit6 =(EditText)findViewById(R.id.editText8);
          city =(EditText)findViewById(R.id.city);
          relativeLayout = (RelativeLayout)findViewById(R.id.createuser);
          country = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
          parent = findViewById(R.id.createuser);
          progressDialog = new ProgressDialog(this);
          progressDialog.setMessage("Creating New User");
          progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
          progressDialog.setCancelable(false);
          dialog= new AlertDialog.Builder(Createuser.this);
          vg=(ViewGroup)parent;
          String [] Country = getResources().getStringArray(R.array.countries_array);
          ArrayAdapter<String> countryadapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Country);
          country.setAdapter(countryadapter);
          edit5.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  int id = event.getAction();
                  edit5.requestFocus();
                  switch (id)
                  {
                      case MotionEvent.ACTION_DOWN:

                          if(event.getRawX() >= (edit5.getRight() - edit5.getCompoundDrawables()[2].getBounds().width())) {
                              edit5.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                          }else
                          {
                              return false;
                          }
                          break;
                      case MotionEvent.ACTION_UP:
                          if(event.getRawX() >= (edit5.getRight() - edit5.getCompoundDrawables()[2].getBounds().width())) {
                              edit5.setInputType(129);
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


    public void registeruser(View v){

           params.put("company",sharedPreferences.getString("companyname",""));
            params.put("firstname",edit1.getText().toString().trim());
            params.put("lastname",edit2.getText().toString().trim());
            params.put("phone",edit3.getText().toString().trim());
            params.put("loginid",edit4.getText().toString().toLowerCase().trim());
            params.put("city",city.getText().toString().trim());
            params.put("country",country.getText().toString().trim());
            params.put("password", edit5.getText().toString().trim());
            params.put("password_confirmation", edit5.getText().toString().trim());
            try{
            JSONObject json = new JSONObject(params);

                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                public void handleMessage(Message inputMessage)
                    {
                          progressDialog.dismiss();
                          if(inputMessage.what == 1){
                              showdialog("sucess");
                          }
                        if(inputMessage.what== 0){
                          Bundle b = inputMessage.getData();
                          message= b.getString("message");
                          showdialog("failed");
                    }

                    }
                };
                Htttpcall http = new Htttpcall(this,handler,"http://demo.olwebapps.com/api/customer");
                progressDialog.show();
                if(Globalclass.getInstance(Createuser.this).isnetworking())
                {
                    http.post(json.toString());
                }else
                {
                    progressDialog.dismiss();
                    Toast.makeText(Createuser.this,"Please Check Your Internet Connection",Toast.LENGTH_LONG).show();
                }
                }catch (Exception ex)
            {
                Log.d("error",ex.toString());
            }

        }


    public Boolean checkIfempty(){
        for(int i = 0; i < vg.getChildCount(); i++){
            View v1 = vg.getChildAt(i);
            if(v1 instanceof EditText){
                String check = ((EditText) v1).getText().toString();
                if(check.equals(""))
                {
                    YoYo.with(Techniques.Shake).duration(700).playOn(v1);
                    Valdidate = false;
                    break;
                }
                else {
                    Valdidate = true;

                }
            }
        }
        return Valdidate;
    }
    @Override
   public void onBackPressed(){
        super.onBackPressed();
    }
        public boolean valdidate(){
        final String email = edit4.getText().toString().trim();
        if (false){
            YoYo.with(Techniques.Shake).duration(700).playOn(edit4);
            edit4.setError("Please enter correct email address");
            Valdidate=false;
            edit4.requestFocus();
        }
        else{
            if(!edit5.getText().toString().equals(edit6.getText().toString()))
            {
                YoYo.with(Techniques.Shake).duration(700).playOn(edit5);
                YoYo.with(Techniques.Shake).duration(700).playOn(edit6);
                edit5.setText(null);
                edit6.setText(null);
                edit5.setError("please enter same password");
                edit6.setError("please enter same password");
                Valdidate=false;
            }else {
                if(country.getText().toString().equals("")){
                    country.setError("Please Mention Country");
                    Valdidate=false;
                    country.requestFocus();

                }else{
                   if(!((edit3.getText().toString().length()) >=10))
                   {
                       YoYo.with(Techniques.Shake).duration(700).playOn(edit3);
                       edit3.setError("Mobile No should be of Minimum 10 Characters");
                       Valdidate=false;
                       edit3.requestFocus();
                   }else
                   {
                      if(!((edit5.getText().toString().length()) >=6))
                       {
                           YoYo.with(Techniques.Shake).duration(700).playOn(edit5);
                           edit5.setError("Password should be of Minimum 6 Characters");
                           Valdidate=false;
                           edit5.requestFocus();
                       }
                       else
                      {
                          Valdidate=true;
                      }
                   }
                }

            }
        }

        return Valdidate;
    }
   public  void showdialog(String mes)
   {
       switch(mes)
       {
           case "sucess":

               dialog= new AlertDialog.Builder(this);
               dialog.setTitle("User Created sucessfully");
               dialog.setMessage("User Created sucessfully");
               dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       Globalclass.getInstance(Createuser.this).startactivity(Login.class);

                   }
               });
               dialog.show();
               break;
           case "failed":
               dialog= new AlertDialog.Builder(this);
               dialog.setTitle("User Creation Failed ");
               dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               dialog.setMessage(message);
               dialog.show();
       }
   }



}
