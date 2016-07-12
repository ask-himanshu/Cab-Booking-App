package driver.prototype.aptlegion.limocartdriver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by Elitebook on 24-Jun-15.
 */
public class acceptedbookinglist_adapter extends RecyclerView.Adapter<acceptedbookinglist_adapter.ViewHolder>  {

    static Context c;
    static ArrayList<openbookingdata>mDataset= new ArrayList<>();
    static LatLng latLng_source,latLng_destination;
    static ViewHolder v ;
    static  ArrayList<LatLng>l = new ArrayList<>();
    static Message mMessage;
    static  Handler mHandler;
    static  String sou_lat,sou_lon,des_lat,des_lon;

    public acceptedbookinglist_adapter(ArrayList<openbookingdata> main)
    {
        mDataset = main;
    }
    @UiThread
    public void updatelist(ArrayList<openbookingdata> main)
    {
     mDataset = main;
     notifyDataSetChanged();

    }
    public acceptedbookinglist_adapter(ArrayList<openbookingdata> main,  Context context,Message message,Handler handler) {
        mDataset = main;
        c=context;
        mMessage= message;
        mHandler=handler;
    }
    public acceptedbookinglist_adapter(ArrayList<openbookingdata> main,  Context context){
        mDataset = main;
        c=context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        public TextView From,To,bookingtime,status;
        public MapView mapView;
        GoogleMap gMap;
        static Button starttrip;
        RelativeLayout relativeLayout;


        public ViewHolder(View v) {
            super(v);

            From =(TextView)v.findViewById(R.id.from);
            To=(TextView)v.findViewById(R.id.to);
            status=(TextView)v.findViewById(R.id.status);
            bookingtime=(TextView)v.findViewById(R.id.bookingtime);
            mapView = (MapView) v.findViewById(R.id.map_view_booking);
            starttrip= (Button)v.findViewById(R.id.accept);
            relativeLayout = (RelativeLayout)v.findViewById(R.id.card);
            if (mapView != null)
            {
                mapView.onCreate(null);
                mapView.onResume();
                gMap=mapView.getExtendedMap();
            }
//            gMap.setClustering(new ClusteringSettings().addMarkersDynamically(true));

        }


        @Override
        public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
            MapsInitializer.initialize(c.getApplicationContext());

        }
    }
    @Override
    public acceptedbookinglist_adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.accptedbookinglist_card, viewGroup, false);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);


        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,  final int i) {


        viewHolder.starttrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positon = i;
                viewHolder.starttrip.setText("Accepted");
                mMessage = mHandler.obtainMessage();
                mMessage.what=1;
                Bundle b = new Bundle();
                Log.e("response",""+b.toString());
                b.putString("message", String.valueOf(i));
                mMessage.setData(b);
                mMessage.sendToTarget();
            }
        });

        v= viewHolder;
        l = new ArrayList<>();
        v.gMap.clear();
        sou_lat= mDataset.get(i).SourceLatitude;
        sou_lon = mDataset.get(i).SourceLongitude;
        des_lat= mDataset.get(i).DestinationLatitude;
        des_lon=mDataset.get(i).DestinationLongitude;
        if (!(sou_lat.equals("")))
        {
            latLng_source = new LatLng(Double.valueOf(sou_lat),Double.valueOf(sou_lon));
            l.add(latLng_source);
        }else {

        }
        if (!des_lat.equals(""))
        {
            latLng_destination = new LatLng(Double.valueOf(des_lat),Double.valueOf(des_lon));
            l.add(latLng_destination);
        }
        else {

        }
        if(l.size()>0) {
            for (int ii = 0; ii < l.size(); ii++) {
                v.gMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(l.get(ii)));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(l.get(0)).zoom(12f).build();
                v.gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
        viewHolder.From.setText(mDataset.get(i).sourceAddress);
        viewHolder.To.setText(mDataset.get(i).destinationAddress);
        viewHolder.bookingtime.setText(mDataset.get(i).bookingtime);
        viewHolder.status.setText(mDataset.get(i).status);
        viewHolder.gMap.setOnMapLoadedCallback(new com.androidmapsextensions.GoogleMap.OnMapLoadedCallback() {

                                                   @Override
                                                   public void onMapLoaded() {
                                                       if (!(sou_lat.equals("")))
                                                       {
                                                           latLng_source = new LatLng(Double.valueOf(sou_lat),Double.valueOf(sou_lon));
                                                           l.add(latLng_source);
                                                       }else {

                                                       }
                                                       if (!des_lat.equals(""))
                                                       {
                                                           latLng_destination = new LatLng(Double.valueOf(des_lat),Double.valueOf(des_lon));
                                                           l.add(latLng_destination);
                                                       }
                                                       else {

                                                       }
                                                       if (l.size() > 0) {
                                                           LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                           for (int ii = 0; ii < l.size(); ii++) {

                                                               builder.include(l.get(ii));
                                                           }
                                                           LatLngBounds bounds = builder.build();
                                                           int padding = 50;
                                                           CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                           v.gMap.moveCamera(cu);
                                                       }

                                                   }
                                               }

        );


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
