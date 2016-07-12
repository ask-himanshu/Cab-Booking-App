package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;


public class GuestBookings extends ActionBarActivity {
    CharSequence Titles[]={"Upcomming","Completed","All Rides"};
    int Numboftabs =3;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    private boolean doubleBackToExitPressedOnce =false;
    ActionBarDrawerToggle mDrawerToggle;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_bookings);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_my_bookings);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(this));
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
        navigationView = (NavigationView)findViewById(R.id.sidenavigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_bookings);
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
    }
    private void setupViewPager(ViewPager viewPager) {

        one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter adapter = new one.prototype.aptlegion.limocart.adapter.ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
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
        if(id==android.R.id.home)
        {
            Intent main = new Intent(this,GuestMain.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);

        }

        return super.onOptionsItemSelected(item);
    }


}
