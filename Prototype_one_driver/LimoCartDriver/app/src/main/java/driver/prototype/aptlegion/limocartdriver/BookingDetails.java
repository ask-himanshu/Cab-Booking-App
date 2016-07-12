package driver.prototype.aptlegion.limocartdriver;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class BookingDetails extends AppCompatActivity implements com.google.android.gms.maps.OnMapReadyCallback{

    public TextView From,To,bookingtime,status;
    com.google.android.gms.maps.GoogleMap mMap;
    LatLngBounds.Builder bounds;
    LatLng sourceAddress;
    LatLng destinationAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        From = (TextView) findViewById(R.id.from);
        To = (TextView) findViewById(R.id.to);
        bookingtime = (TextView) findViewById(R.id.bookingtime);
        status = (TextView) findViewById(R.id.status);
       /* ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().
                        findFragmentById(R.id.map_view_booking);
        mMap = mapFrag.getMap();

        Intent result = getIntent();

        String s= result.getStringExtra(Constants.KEY_STATUS);
        String bT = result.getStringExtra(Constants.KEY_BOOKING_TIME);
        String src = result.getStringExtra(Constants.KEY_SOURCE);
        String dest = result.getStringExtra(Constants.KEY_DESTINATION);
        double srcLat = Double.parseDouble(result.getStringExtra(Constants.KEY_SOURCE_LAT));
        double srcLon = Double.parseDouble(result.getStringExtra(Constants.KEY_SOURCE_LON));
        double destLat = Double.parseDouble(result.getStringExtra(Constants.KEY_DEST_LAT));
        double destLon = Double.parseDouble(result.getStringExtra(Constants.KEY_DEST_LON));

        sourceAddress = new LatLng(srcLat,srcLon);
        destinationAddress = new LatLng(destLat,destLon);


        From.setText("" + src);
        To.setText("" + dest);
        bookingtime.setText("" + bT);
        status.setText("" + s);

        if (mMap !=null){
            mMap = mapFrag.getMap();

            mMap.addMarker(new MarkerOptions().position(sourceAddress));
            mMap.addMarker(new MarkerOptions().position(destinationAddress));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sourceAddress).target(destinationAddress).zoom(17f).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void boundsMethod() {
        bounds = new LatLngBounds.Builder();
        bounds.include(sourceAddress);
        bounds.include(destinationAddress);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 30);
        mMap.moveCamera(cu);
        /*mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));*/

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
        this.mMap = googleMap;
        boundsMethod();
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
