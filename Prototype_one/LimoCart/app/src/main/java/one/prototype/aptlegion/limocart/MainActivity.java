package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONObject;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter;


public class MainActivity extends AppCompatActivity {


    CharSequence Titles[]={"Ride Later","Ride Now","Airport"};
    int Numboftabs =3;
    ViewPager pager;
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor Edit;
    private boolean doubleBackToExitPressedOnce =false;
    NavigationView navigationView;
    TabLayout tabLayout ;
    Getdata getdata;
    HashMap<String, String> user = new HashMap<>();
    HashMap<String,String>params = new HashMap<>();
    ProgressDialog progressDialog;
    JSONObject js;
    TextView name;
    String TAG = "MainActivity";
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we Book your Cab");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        tabLayout = (TabLayout) findViewById(R.id.mainactivity_tabs);
        name = (TextView) findViewById(R.id.Navigation_name);
        name.setText(sharedPreferences.getString("firstname", "") + " " + sharedPreferences.getString("lastname", ""));
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        Edit = sharedPreferences.edit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.sidenavigation_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);
        pager = (ViewPager) findViewById(R.id.pager_main);
        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);

      mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("dr", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }

    @Override
    public void onResume() {
         super.onResume();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Globalclass.getInstance(MainActivity.this).startactivity(MainActivity.class);
                        break;
                    case R.id.navigation_item_2:
                        progressDialog.setMessage("Opening My Bookings...");
                        progressDialog.show();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Handler refresh = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                if (inputMessage.what == 1) {
                                    progressDialog.dismiss();
                                    Globalclass.getInstance(MainActivity.this).startactivity(MyBookings.class);

                                }
                                if (inputMessage.what == 0) {
                                    progressDialog.dismiss();
                                    Bundle b = inputMessage.getData();
                                    Toast.makeText(MainActivity.this, b.get("message").toString(), Toast.LENGTH_LONG).show();

                                }
                            }

                        };
                        Refreshdata r = new Refreshdata(MainActivity.this, refresh);
                        r.refreshdata();

                        break;
                    case R.id.navigation_item_3:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent logout = new Intent(MainActivity.this,Login.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout);
                        finish();
                        break;
                    case R.id.navigation_item_4:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                        Handler up = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                if (inputMessage.what == 1) {
                                    Intent update = new Intent(MainActivity.this, Update_Profile.class);
                                    update.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(update);
                                    progressDialog.dismiss();
                                }
                                if (inputMessage.what == 0) {
                                    progressDialog.dismiss();
                                    Bundle b = inputMessage.getData();
                                    Toast.makeText(MainActivity.this, b.get("message").toString(), Toast.LENGTH_LONG).show();

                                }
                            }
                        };
                    //   Message m = up.obtainMessage();
                       getdata.startAction(MainActivity.this, "getcustomerdata", sharedPreferences.getString("token", null), up);
                      //  getdata.startAction(MainActivity.this, sharedPreferences.getString("token", null), m);

                        break;
                    case R.id.navigation_item_5:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                        Handler wallet = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message message){
                                progressDialog.dismiss();
                                if(message.what ==1){
                                     Intent intent = new Intent(MainActivity.this, Wallet.class);
                                    startActivity(intent);
                                }
                                if(message.what ==0){
                                    Bundle b = message.getData();
                                    Toast.makeText(MainActivity.this, b.get("message").toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        };
                      //  progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                      //  one.prototype.aptlegion.limocart.getdata.getPromotions(MainActivity.this, wallet, sharedPreferences.getString("token", ""));
                        getdata.startAction(MainActivity.this, "getcustomerdata", sharedPreferences.getString("token", null), wallet);
                        break;
                    case R.id.navigation_item_6:
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message message) {
                                progressDialog.dismiss();
                                if (message.what == 1) {
                                    Intent intent1 = new Intent(MainActivity.this, PromotionsActivity.class);
                                    startActivity(intent1);
                                }
                                if (message.what == 0) {
                                    Bundle b = message.getData();
                                    Toast.makeText(MainActivity.this, b.get("message").toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        };
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                        Getdata.getPromotions(MainActivity.this, handler, sharedPreferences.getString("token", ""));


                }
                return false;
            }

        });
        if(Globalclass.getInstance(this).isgpsenabled())
        {
            setupViewPager(pager);
            mDrawerToggle.syncState();

        }else
        {
            Toast.makeText(MainActivity.this,"Please set the GPS to High Power Mode",Toast.LENGTH_LONG).show();
            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
       /* if(((GlobalVaraible)getApplicationContext()).getUserinfo().isEmpty()||(((GlobalVaraible) getApplicationContext()).getAllbookings().isEmpty())||((GlobalVaraible)getApplicationContext()).getBookingdata().isEmpty())
        {
       if (Globalclass.getInstance(MainActivity.this).isnetworking()) {
            params.put("company", "Aptlegion");
            params.put("loginid", username.trim());
            params.put("password", password);
            params.put("type", "Customer");
            JSONObject json = new JSONObject(params);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            Handler resume  = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message inputMessage) {
                    if(inputMessage.what ==1){
                        Bundle b ;
                        b = inputMessage.getData();
                        String message = b.getString("message");
                        try {
                            js = new JSONObject(message);
                            Edit.remove("token").commit();
                            Edit.putString("token", js.getString("token"));
                            Edit.commit();
                            ((GlobalVaraible)getApplicationContext()).setToken(js.getString("token"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Handler h = new Handler(Looper.getMainLooper())
                        {  @Override
                           public void handleMessage(Message inputMessage) {
                               progressDialog.dismiss();

                            }


                        };
                        refreshdata refresh = new refreshdata(MainActivity.this,h);
                        refresh.refreshdata();
                    }
                }

                };
            htttpcall http = new htttpcall(MainActivity.this, resume, "http://creatg.webserversystems.com/api/login");
            http.post(json.toString());
        }else
       {
           Intent login = new Intent(MainActivity.this, one.prototype.aptlegion.limocart.login.class);
           login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(login);
       }
        }*/
        }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        adapter.addFrag(new RideLater());
        adapter.addFrag(new RideNow());
        adapter.addFrag(new Airport());
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed(){
        if(!(mDrawerLayout.isDrawerOpen(Gravity.LEFT))){
        if (doubleBackToExitPressedOnce)
        {

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
                doubleBackToExitPressedOnce=false;

            }
        }, 2000);}else
        {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
