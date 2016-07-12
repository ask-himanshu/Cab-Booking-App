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
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter;


public class MyBookings extends AppCompatActivity {

    CharSequence Titles[]={"Upcomming","Completed","All Rides"};
    int Numboftabs =3;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_my_bookings);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
        navigationView = (NavigationView)findViewById(R.id.sidenavigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_bookings);
        name = (TextView) findViewById(R.id.Navigation_name);
        name.setText(sharedPreferences.getString("firstname", "") + " " + sharedPreferences.getString("lastname", ""));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        Globalclass.getInstance(MyBookings.this).startactivity(MainActivity.class);
                        break;
                    case R.id.navigation_item_2:
                        Globalclass.getInstance(MyBookings.this).startactivity(MyBookings.class);
                        break;
                    case R.id.navigation_item_3:
                        Globalclass.getInstance(MyBookings.this).callactivity("Logout");
                        break;
                    case R.id.navigation_item_4:
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                        Handler up = new Handler(Looper.getMainLooper())
                        {
                            @Override
                            public void handleMessage(Message inputMessage)
                            {
                                if(inputMessage.what==1)
                                {
                                    progressDialog.dismiss();
                                    Intent update = new Intent(MyBookings.this,Update_Profile.class);
                                    update.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(update);
                                }
                                if(inputMessage.what ==0)
                                {
                                    progressDialog.dismiss();
                                    Bundle b = inputMessage.getData();
                                    Toast.makeText(MyBookings.this, b.get("message").toString(), Toast.LENGTH_LONG).show();

                                }
                            }
                        };

                        Getdata.startAction(MyBookings.this, "getcustomerdata", sharedPreferences.getString("token", null), up);
                        break;
                    case  R.id.navigation_item_5:
                        final Intent intent = new Intent(MyBookings.this,Wallet.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_6:
                        Handler handler = new Handler(Looper.getMainLooper()){
                            @Override
                            public void handleMessage(Message message)
                            {
                                progressDialog.dismiss();
                                if(message.what==1)
                                {
                                    Intent intent1 = new Intent(MyBookings.this,PromotionsActivity.class);
                                    startActivity(intent1);
                                }
                                if(message.what==0)
                                {
                                    Bundle b = message.getData();
                                    Toast.makeText(MyBookings.this, b.get("message").toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        };
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                        Getdata.getPromotions(MyBookings.this, handler, sharedPreferences.getString("token", ""));


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
                Log.e("dr", "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
        adapter.addFrag(new OpenBookings());
        adapter.addFrag( new CompletedBookings());
        adapter.addFrag(new Allbookings());
        viewPager.setAdapter(adapter);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
