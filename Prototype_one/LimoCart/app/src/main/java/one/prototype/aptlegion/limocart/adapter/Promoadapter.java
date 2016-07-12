package one.prototype.aptlegion.limocart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import java.util.ArrayList;

import one.prototype.aptlegion.limocart.Promotions;
import one.prototype.aptlegion.limocart.R;


/**
 * Created by Pragadees Waran on 08-09-2015.
 */
public class Promoadapter extends  RecyclerView.Adapter<Promoadapter.ViewHolder> {
    static Context c;
    static ArrayList<Promotions> mDataset= new ArrayList<>();
    static ViewHolder v ;
    public Promoadapter(Context context,ArrayList<Promotions> promotions)
    {
        this.mDataset=promotions;
        this.c=context;
    }

    @Override
    public Promoadapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promocard, parent, false);


        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);


        return vh;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Code,Description;




        public ViewHolder(View v) {
            super(v);
            Code =(TextView)v.findViewById(R.id.code);
            Description=(TextView)v.findViewById(R.id.des);

        }


    }
    @Override
    public void onBindViewHolder(Promoadapter.ViewHolder holder, int position) {
         Promotions p = mDataset.get(position);
        holder.Code.setText(p.Code);
        holder.Description.setText(p.Description);
    }

    @Override

    public int getItemCount() {
        return mDataset.size();
    }
}
