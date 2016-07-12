package driver.prototype.aptlegion.limocartdriver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class createuser extends AppCompatActivity {
    private AutoCompleteTextView country;
    private EditText edit1,edit2,edit3,edit4,edit5,edit6,edit8,edit9,rescity;
    Boolean Valdidate = false;
    View parent;
    ViewGroup vg ;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    ArrayList<String> message = new ArrayList<>();
    HashMap<String, String> params = new HashMap<String, String>();
    AlertDialog.Builder dialog;
    ProgressDialog progressDialog;
    Spinner vehicletype,city;
    String Vehicle,City="";
    SharedPreferences sharedPreferences,pref1;
    List<String> Cars= new ArrayList<>();
    ArrayList<Vehicle>Vehicles = new ArrayList<>();
    ArrayList<String>Cities=new ArrayList<>();

      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);
          Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
          vehicletype = (Spinner)findViewById(R.id.vehicletype);
          city = (Spinner)findViewById(R.id.city);
          sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
          pref1 = getApplicationContext().getSharedPreferences("MyPrefs_driver1", MODE_PRIVATE);
          Set<String> Citi =pref1.getStringSet("Cities",null);
          if(Citi!= null)
          {
              for (String c : Citi)
              {
                  Cities.add(c);
              }
              ArrayAdapter<String>C= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Cities);
              city.setAdapter(C);
          }
          city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  City = parent.getItemAtPosition(position).toString();
                  try {
                      HashMap<String, ArrayList<Vehicle>> info = (HashMap<String, ArrayList<Vehicle>>) ObjectSerializer.deserialize(pref1.getString("VehicleCity", null));
                      ArrayList<Vehicle> Vehicles = info.get(City);
                      if(Vehicles != null)
                      {
                          Cars.clear();
                          for (driver.prototype.aptlegion.limocartdriver.Vehicle v : Vehicles) {
                              Cars.add(v.Vehicletype);
                          }
                          ArrayAdapter<String> adapter = new ArrayAdapter<String>(createuser.this, android.R.layout.simple_list_item_1, Cars);
                          vehicletype.setAdapter(adapter);
                      }else
                      {
                          dialog = new AlertDialog.Builder(createuser.this);
                          dialog.setMessage("No Vehicles for the Selected City");
                          dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                              }
                          });
                          dialog.show();
                      }

                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });
          City = city.getSelectedItem().toString();
          try{
              HashMap<String, ArrayList<Vehicle>> info = (HashMap<String, ArrayList<Vehicle>>) ObjectSerializer.deserialize(pref1.getString("VehicleCity", null));
              ArrayList<Vehicle> Vehicles = info.get(City);
              if(Vehicle!= null)
              {
                  Cars.clear();
                  for (driver.prototype.aptlegion.limocartdriver.Vehicle v : Vehicles) {
                      Cars.add(v.Vehicletype);
                  }
                  ArrayAdapter<String> adapter = new ArrayAdapter<String>(createuser.this, android.R.layout.simple_list_item_1, Cars);
                  vehicletype.setAdapter(adapter);
              }

          }catch (Exception ex)
          {

          }

          vehicletype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  Vehicle = parent.getItemAtPosition(position).toString();
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });


          parent = findViewById(R.id.createuser);
          progressDialog = new ProgressDialog(this);
          progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
          progressDialog.setMessage("Creating Please Wait");
          progressDialog.setTitle("Create New User");
          progressDialog.setCancelable(false);
          vg=(ViewGroup)parent;

          edit1 =(EditText)findViewById(R.id.editText3);
          edit2 =(EditText)findViewById(R.id.editText4);
          edit3 =(EditText)findViewById(R.id.editText5);
          edit4 =(EditText)findViewById(R.id.editText6);
          edit5 =(EditText)findViewById(R.id.editText7);
          edit6 =(EditText)findViewById(R.id.editText8);
          edit8 =(EditText)findViewById(R.id.editText10);
          edit9 =(EditText)findViewById(R.id.editText11);
          rescity=(EditText)findViewById(R.id.Rescity);
          String[] Country = getResources().getStringArray(R.array.countries_array);
          country = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
          ArrayAdapter<String> countryadapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Country);
          country.setAdapter(countryadapter);

    }
    @Override
    public  void onBackPressed()
    {
        Globalclass.getInstance(this).startactivity(Login.class);
    }

    public void registeruser(View v){

       for(int i = 0; i < vg.getChildCount(); i++){
            View v1 = vg.getChildAt(i);
            if(v1 instanceof EditText){
               String check = ((EditText) v1).getText().toString();
                if(check.equals(""))
                {
                    YoYo.with(Techniques.Shake).duration(700).playOn(v1);
                    Valdidate = false;

                }
                else {
                    Valdidate = true;

                }

            }

        }

       Valdidate= valdidate();

       if(Valdidate){
           if(!(Vehicle.equals("")))
           {
             Vehicle = vehicletype.getSelectedItem().toString();
           }
           if(City.equals(""))
           {
               City=city.getSelectedItem().toString();
           }
            params.put("company",pref1.getString("Company",""));
            params.put("name",edit1.getText().toString().trim());
            params.put("firstname",edit1.getText().toString().trim());
            params.put("lastname",edit2.getText().toString().trim());
            params.put("phone",edit3.getText().toString().trim());
            params.put("loginid",edit4.getText().toString().trim().toLowerCase());
            params.put("vehicletype",Vehicle);
            params.put("vehiclenumber",edit8.getText().toString().trim());
            params.put("licenseno",edit9.getText().toString().trim());
            params.put("service_city",City);
            params.put("country",country.getText().toString().trim());
            params.put("password",edit5.getText().toString().trim());
            params.put("password_confirmation", edit5.getText().toString().trim());
            params.put("city",rescity.getText().toString().trim());
            JSONObject json = new JSONObject(params);
            JsonObjectRequest string = new JsonObjectRequest(com.android.volley.Request.Method.POST,"http://creatg.webserversystems.com/api/driver",json.toString(), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response != null){
                    message.clear();
                    message.add("sucess");
                    progressDialog.dismiss();
                    showdialog("sucess");
                    }else
                    {
                        Toast.makeText(createuser.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error1 = new VolleyError(new String(error.networkResponse.data));
                        error = error1;
                        message=Globalclass.getInstance(createuser.this).processerrorjson(error);
                        progressDialog.dismiss();
                        showdialog("failed");
                    }

                }}
            );
           string.setRetryPolicy(new DefaultRetryPolicy(30000,
                   DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                   DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

           if(Globalclass.getInstance(this).isnetworking()) {
                progressDialog.show();
                Globalclass.getInstance(this).makerequest(string);
            }else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        }

    }
        public boolean valdidate(){
        final String email = edit4.getText().toString().trim();
        if (false){
            YoYo.with(Techniques.Shake).duration(700).playOn(edit4);
            edit4.setError("please enter Correct mail address");
            Valdidate=false;
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

                }else{
                   if(!((edit3.getText().toString().length()) >=10))
                   {
                       YoYo.with(Techniques.Shake).duration(700).playOn(edit3);
                       edit3.setError("Mobile No should be Minimum 10 Characters");
                       Valdidate=false;
                   }else
                   {
                       Valdidate = true;
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
               dialog.setMessage("Driver Created successfully Awaiting Approval");
               dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       Globalclass.getInstance(createuser.this).startactivity(Login.class);
                   }
               });
               dialog.show();
               break;
           case "failed":
               StringBuilder builder = new StringBuilder();
               if(message != null){
               for (String value : message) {
                   builder.append(value);
                   builder.append(System.getProperty("line.separator"));
               }
               String text = builder.toString();
               dialog= new AlertDialog.Builder(this);
               dialog.setTitle("User Creation Failed ");
               dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               });
               dialog.setMessage(text);
               dialog.show();}
               else {
                   Toast.makeText(createuser.this, "Please Try Again Later", Toast.LENGTH_SHORT).show();
               }
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


        return super.onOptionsItemSelected(item);
    }
}
