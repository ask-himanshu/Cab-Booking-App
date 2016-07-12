package driver.prototype.aptlegion.limocartdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    Boolean doubleBackToExitPressedOnce = false;
    SharedPreferences sharedPreferences;
    ArrayList<openbookingdata> openbookingdatas = new ArrayList<>();
    CharSequence Titles[] = {"Ride Now ", "Accepted ", "All Booking"};
    int Numboftabs = 3;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    SharedPreferences.Editor Edit;
    LocationManager locationManager;
    Location loc1;
    ViewPagerAdapter adapter;
    getdata getdata;
    SharedPreferences.Editor editor;
    ViewPager viewPager;
    AlertDialog.Builder dialog;
    TextView navigationname;
    PowerManager.WakeLock wakeLock;
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        navigationname = (TextView) findViewById(R.id.Navigation_name);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = criteria();
        loc1 = locationManager.getLastKnownLocation(provider);
        dialog = new AlertDialog.Builder(MainActivity.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedPreferences = getSharedPreferences("MyPrefs_driver", MODE_PRIVATE);
        navigationname.setText(sharedPreferences.getString("firstname", null) + " " + sharedPreferences.getString("lastname", null));
        editor = sharedPreferences.edit();
        Edit = sharedPreferences.edit();
        openbookingdatas = ((GlobalVaraible) this.getApplicationContext()).getBookingdata();
        navigationView = (NavigationView) findViewById(R.id.sidenavigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_bookings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);
        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setBackgroundColor(Color.rgb(103, 58, 183));
        tabLayout.setSmoothScrollingEnabled(true);
        tabLayout.setHorizontalScrollBarEnabled(true);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        navigationname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
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

    @Override
    public void onResume()

    {
        super.onResume();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {

                    case R.id.navigation_item_3:
                        editor.clear();
                        editor.commit();
                        Globalclass.getInstance(MainActivity.this).callactivity("Logout");
                        finish();
                        break;
                    case R.id.navigation_item_2:
                        Intent book = new Intent(MainActivity.this, CreateBooking.class);
                        startActivity(book);
                        break;
                    case R.id.navigation_item_1:

                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        getdata = new getdata();
                        Handler diver = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message inputMessage) {
                                if (inputMessage.what == 1) {
                                    progressDialog.dismiss();
                                    Globalclass.getInstance(MainActivity.this).startactivity(updateprofile.class);
                                }
                                if (inputMessage.what == 0) {
                                    progressDialog.dismiss();
                                    dialog.setMessage("Something Went wrong");
                                    dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent login = new Intent(MainActivity.this, Login.class);
                                            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(login);

                                        }
                                    });
                                    dialog.show();
                                }
                            }

                        };
                        Message m = diver.obtainMessage();
                        getdata.getdriverdata(MainActivity.this, sharedPreferences.getString("token", null), m);
                        break;
                }


                return false;
            }

        });



    }


    private void setupViewPager(final ViewPager viewPager) {
        final pendingbookings pendingBookings = new pendingbookings();
        final acceptedbooking acceptBooking = new acceptedbooking();
        pendingBookings.setOnAcceptClickListner(new pendingbookings.OnAcceptClickListner() {
            @Override
            public void onAcceptiClicked() {
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
                wakeLock.acquire();
                Handler refreshviewpager = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {

                        if (message.what == 1) {
                            ArrayList<openbookingdata> list, list1, list2 = new ArrayList<openbookingdata>();
                            list = ((GlobalVaraible) getApplicationContext()).getAcceptedbooking();
                            list1 = ((GlobalVaraible) getApplicationContext()).getBookingdata();
                            list2 = ((GlobalVaraible) getApplicationContext()).getAllbookings();
                            acceptedbookinglist_adapter a = new acceptedbookinglist_adapter(list);
                            allbookingsadapter ab = new allbookingsadapter(list2);
                            mycard m = new mycard(list1);
                            m.notifyDataSetChanged();
                            a.notifyDataSetChanged();
                            ab.notifyDataSetChanged();


                            viewPager.setVisibility(RelativeLayout.VISIBLE);
                            setupViewPager(viewPager);

                        }
                        if (message.what == 0) {
                            Bundle b = message.getData();
                            dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setMessage(b.getString("message").toString());
                            dialog.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Globalclass.getInstance(MainActivity.this).startactivity(Login.class);
                                }
                            });
                        }
                    }
                };

                ((GlobalVaraible) getApplicationContext()).setHandler(refreshviewpager);
                viewPager.setCurrentItem(1,false);  //first position

            }
        });
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);
        adapter.addFrag(pendingBookings);
        adapter.addFrag(acceptBooking);
        adapter.addFrag(new allbookings());
        viewPager.setAdapter(adapter);
    }


    public String criteria() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        return locationManager.getBestProvider(criteria, true);
    }

    @Override
    public void onRestart() {
        super.onRestart();

    }


    @Override
    public void onBackPressed() {
        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

                //    Intent intent = new Intent(Intent.ACTION_MAIN);
                //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //   intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //   startActivity(intent);
                //  finish();

            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);

        } else

        {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

   /* @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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



