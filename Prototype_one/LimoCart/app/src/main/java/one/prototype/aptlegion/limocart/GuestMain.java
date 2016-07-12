package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

import static android.os.Process.killProcess;
import static android.os.Process.myPid;

public class GuestMain extends AppCompatActivity {
    CharSequence Titles[]={"Ride Later","Ride Now"};
    int Numboftabs =2;
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
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait while we Book your Cab");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        tabLayout = (TabLayout) findViewById(R.id.mainactivity_tabs);
        name = (TextView) findViewById(R.id.Navigation_name);
        name.setText(sharedPreferences.getString("firstname", "") + " " + sharedPreferences.getString("lastname",""));
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                   case R.id.guest_myrides:
                        progressDialog.setMessage("Opening My Bookings...");
                        progressDialog.show();
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Handler refresh = new Handler(Looper.getMainLooper())
                        {
                            @Override
                            public void handleMessage(Message inputMessage)
                            {
                                if(inputMessage.what==1)
                                {   progressDialog.dismiss();
                                    Globalclass.getInstance(GuestMain.this).startactivity(GuestBookings.class);

                                }
                                if(inputMessage.what ==0)
                                {
                                    progressDialog.dismiss();
                                    Bundle b = inputMessage.getData();
                                    Toast.makeText(GuestMain.this, b.get("message").toString(), Toast.LENGTH_LONG).show();

                                }
                            }

                        };
                        Refreshdata r = new Refreshdata(GuestMain.this,refresh);
                        r.refreshdata();

                        break;
                    case R.id.guest_logout:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Globalclass.getInstance(GuestMain.this).callactivity("Logout");
                        break;

                }
                return false;
            }

        });
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
    private void setupViewPager(ViewPager viewPager) {

        one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter adapter = new one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        adapter.addFrag(new RideLater());
        adapter.addFrag(new RideNow());
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onBackPressed(){
        if(!(mDrawerLayout.isDrawerOpen(Gravity.LEFT))){
            if (doubleBackToExitPressedOnce)
            {

                super.onBackPressed();
                killProcess(myPid());

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
    public void onResume() {
        super.onResume();
        if(Globalclass.getInstance(this).isgpsenabled())
        {
            setupViewPager(pager);
            mDrawerToggle.syncState();

        }else
        {
            Toast.makeText(GuestMain.this,"Please set the GPS to High Power Mode",Toast.LENGTH_LONG).show();
            Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guest_main, menu);
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
