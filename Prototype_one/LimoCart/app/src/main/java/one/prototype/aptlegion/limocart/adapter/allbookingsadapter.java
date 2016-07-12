package one.prototype.aptlegion.limocart.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import one.prototype.aptlegion.limocart.DetailsBooking;
import one.prototype.aptlegion.limocart.R;
import one.prototype.aptlegion.limocart.Openbookingdata;

/**
 * Created by Elitebook on 30-Jun-15.
 */
public class allbookingsadapter extends RecyclerView.Adapter<allbookingsadapter.ViewHolder> {

    static Context c;
    static ArrayList<Openbookingdata> mDataset= new ArrayList<>();
    static ViewHolder v ;
    static Handler handler;
    private OnItemClickListener mListener;
    public allbookingsadapter(ArrayList<Openbookingdata> main,  Context context,Handler handler) {
        mDataset = main;
        c=context;
        this.handler = handler;

    }
    public  interface OnItemClickListener{
        public void onItemClick(View view,int position);
    }

    public void setOnClickListner(OnItemClickListener mListener){
        this.mListener = mListener;
    }

    public allbookingsadapter(Context context) {

    }




    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView From,To,bookingtime,status;
        public CardView parentLayout;


        public ViewHolder(View v) {
            super(v);
            From =(TextView)v.findViewById(R.id.from);
            To=(TextView)v.findViewById(R.id.to);
            status=(TextView)v.findViewById(R.id.status);
            bookingtime=(TextView)v.findViewById(R.id.bookingtime);
            parentLayout = (CardView) v.findViewById(R.id.card_view);
         //   mapView = (MapView) v.findViewById(R.id.map_view_booking);



           /* if (mapView != null)
            {
                mapView.onCreate(null);
                mapView.onResume();
                gMap=mapView.getExtendedMap();
            }
            gMap.setClustering(new ClusteringSettings().addMarkersDynamically(true));
            */

        }


       /* @Override
        public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
            MapsInitializer.initialize(c.getApplicationContext());

        }*/


    }


    @Override
    public allbookingsadapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.allbookingscard, viewGroup, false);




        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,  final int i) {

        v= viewHolder;

        viewHolder.From.setText(mDataset.get(i).sourceAddress);
        viewHolder.To.setText(mDataset.get(i).destinationAddress);
        viewHolder.bookingtime.setText(mDataset.get(i).bookingtime);
        viewHolder.status.setText(mDataset.get(i).status);


        if(mListener != null){
            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, i);
                }
            });
        }




       /* v.gMap.clear();
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
        if(l.size()>0)  {
            for ( int ii=0; ii<l.size();ii++)
            {   String title="";
                if(ii==0){
                    title ="From";
                }if(ii==1)
            {
                 title= "To";
            }



                v.gMap.addMarker(new com.androidmapsextensions.MarkerOptions().position(l.get(ii)).title(title));
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

                                                           if(l.size()>0)  {
                                                               LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                               for ( int ii=0; ii<l.size();ii++)
                                                               {
                                                                   builder.include(l.get(ii));
                                                               }
                                                               LatLngBounds bounds = builder.build();
                                                               int padding = 50;
                                                               CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                               if(v.gMap != null){v.gMap.moveCamera(cu);}


                                                           }
                                                       }
                                                   }

            );*/

    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }




}
