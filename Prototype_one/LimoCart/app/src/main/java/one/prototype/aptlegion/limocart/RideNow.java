package one.prototype.aptlegion.limocart;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.androidmapsextensions.SupportMapFragment;
import com.androidmapsextensions.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;
import one.prototype.aptlegion.limocart.adapter.GooglePlacesAutocompleteAdapter;

/**
 * Created by Pragadees on 21-01-2015.
 */

public class RideNow extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private SupportMapFragment fragment;
    Context context;
    Location location;
    Bitmap b;
    AutoCompleteTextView source, dest;
    Geocoder geo;
    List<Address> list1, list2;
    Handler handler;
    GoogleApiClient googleApiClient;
    String url, token, vehicle, Country, city, srcCity, destCity;
    LatLng source_coordinates, destination_coordinates;
    HashMap<String, String> params = new HashMap<>();
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    Htttpcall http;
    SharedPreferences sharedPreferences, pref1;
    FloatingActionMenu menu;
    FloatingActionButton floatingActionButton;
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    Set<String> Cities;
    TextView textView;
    Htttpcall htttpcall;
    Button currentLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ridenow, container, false);
        context = v.getContext();
        textView = (TextView) v.findViewById(R.id.noservice);
        floatingActionButton = new FloatingActionButton(context);
        menu = (FloatingActionMenu) v.findViewById(R.id.menu);
        menu.setIconAnimated(false);
       /* pref3= v.getContext().getSharedPreferences("MyPrefs3", context.MODE_PRIVATE);
        editor3 = pref3.edit();*/
        sharedPreferences = v.getContext().getSharedPreferences("MyPrefs", context.MODE_PRIVATE);
        pref1 = context.getApplicationContext().getSharedPreferences("Companyname", context.MODE_PRIVATE);
        Cities = pref1.getStringSet("Cities", null);
        try {
            vehicles = (ArrayList<Vehicle>) ObjectSerializer.deserialize(pref1.getString("Vehicles", ObjectSerializer.serialize(new ArrayList<Vehicle>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait while we Book your Cab");
        progressDialog.setTitle("Booking");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(new IndeterminateProgressDrawable(context));
        token = sharedPreferences.getString("token", null);
        url = "http://creatg.webserversystems.com/api/bookings?token={" + token + "}";
        dialog = new android.support.v7.app.AlertDialog.Builder(context);
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        handler = new Handler();
        currentLocation = (Button) v.findViewById(R.id.button);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                String result = null;

                try {
                    if (geo.isPresent()) {
                        List<Address> addressList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address dragSrcAddress = addressList.get(0);

                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < dragSrcAddress.getMaxAddressLineIndex(); i++) {
                                sb.append(dragSrcAddress.getAddressLine(i));
                            }

                            result = sb.toString();
                            source.setText(result);
                            city = dragSrcAddress.getLocality();
                            Country = dragSrcAddress.getCountryName();
                            srcCity = city;
                            //    Toast.makeText(getActivity(), "onMarkerDragEnd" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                            source_coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(source_coordinates).title(result.toString()));
                            locatecmaera(source_coordinates);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        geo = new Geocoder(context);
        source = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextViewFrom);
        source.setAdapter(new GooglePlacesAutocompleteAdapter(context, R.layout.auto_complete));
        dest = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextViewTo);
        dest.setAdapter(new GooglePlacesAutocompleteAdapter(context, R.layout.auto_complete));
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String str1 = (String) parent.getItemAtPosition(position);
                try {
                    if (geo.isPresent()) {
                        list1 = geo.getFromLocationName(str1, 1);
                        if (!(list1.isEmpty())) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(source.getWindowToken(), 0);
                            source.setText(str1);
                            Address address = list1.get(0);
                            if (Cities.contains(address.getLocality())) {
                                city = address.getLocality();
                                srcCity = city;
                                Country = address.getCountryName();
                                LatLng src = new LatLng(address.getLatitude(), address.getLongitude());
                                source_coordinates = src;
                                mMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(source_coordinates).title(str1)).setDraggable(true);
                                locatecmaera(src);
                                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                    @Override
                                    public void onMarkerDragStart(Marker marker) {

                                    }

                                    @Override
                                    public void onMarkerDrag(Marker marker) {

                                    }

                                    @Override
                                    public void onMarkerDragEnd(Marker marker1) {
                                        LatLng dragSrc = marker1.getPosition();
                                        String result = null;

                                        try {
                                            if (geo.isPresent()) {
                                                List<Address> addressList = geo.getFromLocation(dragSrc.latitude, dragSrc.longitude, 1);

                                                if (addressList != null && addressList.size() > 0) {
                                                    Address dragSrcAddress = addressList.get(0);
                                                    StringBuilder sb = new StringBuilder();
                                                    for (int i = 0; i < dragSrcAddress.getMaxAddressLineIndex(); i++) {
                                                        sb.append(dragSrcAddress.getAddressLine(i));
                                                    }

                                                    result = sb.toString();
                                                    source.setText(result);
                                                    //     Toast.makeText(getActivity(), "onMarkerDragEnd" + marker.getPosition().latitude, Toast.LENGTH_SHORT).show();
                                                    source_coordinates = new LatLng(dragSrc.latitude, dragSrc.longitude);
                                                  //  mMap.addMarker(new com.androidmapsextensions.MarkerOptions().title(result.toString()));


                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                            } else {
                                srcCity = "";
                                source.setText("");
                                dialog.setMessage("Service Not Available in Selected Zone");
                                dialog.setPositiveButton("ok", null);
                                dialog.show();
                            }

                        } else {
                            source.setText("");
                            Toast.makeText(context, "No Coordinates Available for this location Please select a nearby Location", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                try {

                    if (geo.isPresent()) {
                        list2 = geo.getFromLocationName(str, 1);
                        if (!(list2.isEmpty())) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(dest.getWindowToken(), 0);
                            dest.setText(str);
                            Address ad = list2.get(0);
                            if (Cities.contains(ad.getLocality())) {
                                destCity = ad.getLocality();
                                city = destCity;
                                LatLng l = new LatLng(ad.getLatitude(), ad.getLongitude());
                                destination_coordinates = l;
                                mMap.addMarker(new com.androidmapsextensions.MarkerOptions()
                                        .position(destination_coordinates).title(str)).setDraggable(true);
                                locatecmaera(l);
                                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                    @Override
                                    public void onMarkerDragStart(Marker marker) {

                                    }

                                    @Override
                                    public void onMarkerDrag(Marker marker) {

                                    }

                                    public void onMarkerDragEnd(Marker marker2) {
                                        LatLng dragDest = marker2.getPosition();
                                        String result = null;

                                        try {
                                            if (geo.isPresent()) {
                                                List<Address> addressList = geo.getFromLocation(dragDest.latitude, dragDest.longitude, 1);

                                                if (addressList != null && addressList.size() > 0) {
                                                    Address dragDestAddress = addressList.get(0);
                                                    StringBuilder sb = new StringBuilder();
                                                    for (int i = 0; i < dragDestAddress.getMaxAddressLineIndex(); i++) {
                                                        sb.append(dragDestAddress.getAddressLine(i));
                                                    }

                                                    result = sb.toString();
                                                    dest.setText(result);
                                                    //    Toast.makeText(getActivity(), "onMarkerDragEnd" + marker.getPosition().latitude, Toast.LENGTH_SHORT).show();
                                                    destination_coordinates = new LatLng(dragDest.latitude, dragDest.longitude);
                                                  //  mMap.addMarker(new com.androidmapsextensions.MarkerOptions().title(result.toString()));

                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });

                            } else {
                                destCity = "";
                                dest.setText("");
                                dialog.setMessage("Service Not Available in selected Zone");
                                dialog.setPositiveButton("ok", null);
                                dialog.show();

                            }

                        } else {
                            dest.setText("");
                            Toast.makeText(context, "No Coordinates Available for this location Please select a nearby Location", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    Log.d("Error", e.getMessage());
                }

            }
        });



        View.OnClickListener clicklistnrer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingActionButton vv = (FloatingActionButton) v;
                vehicle = vv.getLabelText();
                bookcab();

            }
        };
        if (vehicles != null) {
            for (int i = 0; i < vehicles.size(); i++) {
                Vehicle vehicle = new Vehicle();
                vehicle = vehicles.get(i);
                floatingActionButton = new FloatingActionButton(context);
                floatingActionButton.setLabelText(vehicle.Vehicletype.toString());
                floatingActionButton.setImageResource(R.drawable.car1);
                floatingActionButton.setColorNormal(R.attr.colorAccent);
                floatingActionButton.setColorPressed(R.attr.colorPrimary);
                floatingActionButton.setOnClickListener(clicklistnrer);
                menu.addMenuButton(floatingActionButton);
            }
        } else {
            menu.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

        dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();
                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (dest.getRight() - dest.getCompoundDrawables()[2].getBounds().width())) {

                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (dest.getRight() - dest.getCompoundDrawables()[2].getBounds().width())) {
                            dest.setText("");
                            mMap.clear();
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            return false;
                        }
                        break;
                }

                return true;

            }
        });
        source.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id = event.getAction();

                switch (id) {
                    case MotionEvent.ACTION_DOWN:

                        if (event.getRawX() >= (source.getRight() - source.getCompoundDrawables()[2].getBounds().width())) {

                        } else {
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getRawX() >= (source.getRight() - source.getCompoundDrawables()[2].getBounds().width())) {
                            source.setText("");
                            mMap.clear();
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            return false;
                        }
                        break;
                }

                return true;

            }
        });


        return v;
    }

    public void bookcab() {
        if (Globalclass.getInstance(context).isnetworking()) {
            String check1 = source.getText().toString();
            String check = dest.getText().toString();
           /* if (!((source_coordinates == null) || (destination_coordinates == null))) {*/
                if (!((check.equals("") || (check1.equals(""))))) {
                    if (!(srcCity.equals("") || (destCity.equals("")))) {
                        if (!(source_coordinates.equals(destination_coordinates))) {
                            params.put("source", source.getText().toString());
                            params.put("source_lat", String.valueOf(source_coordinates.latitude));
                            params.put("source_long", String.valueOf(source_coordinates.longitude));
                            params.put("dest_lat", String.valueOf(destination_coordinates.latitude));
                            params.put("dest_long", String.valueOf(destination_coordinates.longitude));
                            params.put("city", city);
                            params.put("vehicletype", vehicle);
                            params.put("country", Country);
                            params.put("destination", dest.getText().toString());
                            JSONObject json = new JSONObject(params);
                           /* editor3.putString("source",params.get("source"));
                            editor3.putString("destination",params.get("destination"));
                            editor3.putString("source_lat",params.get("source_lat"));
                            editor3.putString("source_long",params.get("source_long"));
                            editor3.putString("dest_lat",params.get("dest_lat"));
                            editor3.commit();*/
                            handler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message inputMessage) {
                                    if (inputMessage.what == 1)
                                    {
                                        Bundle b1 = inputMessage.getData();

                                        try {

                                            JSONObject jsonObject = new JSONObject(b1.getString("message"));

                                            JSONObject data = new JSONObject(jsonObject.getString("data"));

                                           /* pref2 = getActivity().getPreferences(0);
                                            editor = pref2.edit();
                                            editor.putString("Distance",data.getString("distance"));
                                            editor.putString("PreAmount",data.getString("estimated_cost"));
                                            editor.putString("BookingId",data.getString("bookingid"));
                                            editor.commit();*/

                                        }catch (Exception ex){

                                        }
                                        Handler refresh = new Handler(Looper.getMainLooper()) {
                                            @Override
                                            public void handleMessage(Message inputMessage) {
                                                if (inputMessage.what == 1) {
                                                    progressDialog.dismiss();
                                                    dialog.setTitle("Success");
                                                    dialog.setMessage("Booking Created");
                                                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            Intent intent = new Intent(getActivity(),MyBookings.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    dialog.show();
                                                }
                                            }

                                        };
                                        Refreshdata refreshdata = new Refreshdata(context, refresh);
                                        refreshdata.refreshdata();
                                    }

                                    if (inputMessage.what == 0) {
                                        progressDialog.dismiss();
                                        Bundle b = inputMessage.getData();
                                        dialog.setTitle("Oops...");
                                        dialog.setMessage(b.get("message").toString());
                                        dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Globalclass.getInstance(context).callactivity("Logout");
                                            }
                                        });
                                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                source.setText("");
                                                dest.setText("");
                                            }
                                        });
                                        dialog.show();

                                    }
                                }

                            };
                                           http = new Htttpcall(context, handler, url);
                            progressDialog.setMessage("Booking Cab");
                            progressDialog.show();
                            if (Globalclass.getInstance(context).isnetworking()) {
                                http.post(json.toString());
                            } else {
                                progressDialog.dismiss();
                                    Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            dialog.setMessage("Destination and Source cannot be same");
                            dialog.setPositiveButton("ok", null);
                            dialog.show();
                        }
                    } else {
                        dialog.setMessage("Correctly fill the Address");
                        dialog.setPositiveButton("ok", null);
                        dialog.show();
                    }

                } else {
                    dialog.setMessage("Source or Destination Cannot be empty");
                    dialog.setPositiveButton("ok", null);
                    dialog.show();
                }

           /* } else {
                Toast.makeText(context, "Please Enter Address", Toast.LENGTH_LONG).show();
            }*/
        } else {
            Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }


    }

    public void locatecmaera(LatLng pos) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos).zoom(17f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onActivityCreated(Bundle Savedinstance) {
        super.onActivityCreated(Savedinstance);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(googleApiClient.isConnected())) {
            googleApiClient.connect();
        } else {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        googleApiClient.disconnect();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            location = ((GlobalVaraible) context.getApplicationContext()).getMylocation();
        }
        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (location != null) {
            if (mMap == null) {
                mMap = fragment.getExtendedMap();
                mMap.setClustering(new ClusteringSettings().addMarkersDynamically(true));
                mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (mylocation != null) {
                    locatecmaera(mylocation);
                }
                mMap.setMyLocationEnabled(true);
               /* mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                        String result = null;

                        try {
                            if (geo.isPresent()) {
                                List<Address> addressList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addressList != null && addressList.size() > 0) {
                                    Address dragSrcAddress = addressList.get(0);

                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < dragSrcAddress.getMaxAddressLineIndex(); i++) {
                                        sb.append(dragSrcAddress.getAddressLine(i));
                                    }

                                    result = sb.toString();
                                    source.setText(result);
                                    city = dragSrcAddress.getLocality();
                                    Country = dragSrcAddress.getCountryName();
                                    srcCity = city;
                                    //    Toast.makeText(getActivity(), "onMarkerDragEnd" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                                    source_coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(source_coordinates).title("Source"));

                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                       *//* if (Cities.contains(dragSrcAddress.getLocality())) {
                            city = dragSrcAddress.getLocality();
                            srcCity = city;
                            Country = dragSrcAddress.getCountryName();
                        }else {
                            Toast.makeText(getActivity(),"Source city is null",Toast.LENGTH_SHORT).show();
                        }*//*
                        return true;

                    }

                });
*/
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Globalclass.getInstance(context).callactivity("Logout");
    }

    @Override
    public void onResume(){
        super.onResume();
    }



}