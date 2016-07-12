package one.prototype.aptlegion.limocart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import one.prototype.aptlegion.limocart.adapter.Promoadapter;


public class PromotionsActivity extends ActionBarActivity {

    Promoadapter adapter;
    ArrayList<Promotions>Promotions= new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout ;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    Htttpcall htttpcall;
    TextView nopromos,swipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        nopromos =(TextView)findViewById(R.id.nopromos);
        swipe=(TextView)findViewById(R.id.swipe);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.Promotions_swipe);
        sharedPreferences =getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Promotions=((one.prototype.aptlegion.limocart.GlobalVaraible)getApplicationContext()).getPromos();
        recyclerView = (RecyclerView)findViewById(R.id.promotions_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Promoadapter(getApplicationContext(),Promotions);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nopromos.setVisibility(View.GONE);
                swipe.setVisibility(View.GONE);
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (message.what == 1) {
                            Promotions = ((one.prototype.aptlegion.limocart.GlobalVaraible) getApplicationContext()).getPromos();
                            adapter = new Promoadapter(PromotionsActivity.this, Promotions);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            if (Promotions.isEmpty()) {
                                nopromos.setVisibility(View.VISIBLE);
                                swipe.setVisibility(View.VISIBLE);
                            }
                        }
                        if (message.what == 0) {
                            swipeRefreshLayout.setRefreshing(false);
                            Bundle b = message.getData();
                            Toast.makeText(PromotionsActivity.this, b.get("message").toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                };
                Getdata.getPromotions(PromotionsActivity.this, handler, sharedPreferences.getString("token", ""));
            }
        });
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Promotions=((one.prototype.aptlegion.limocart.GlobalVaraible)getApplicationContext()).getPromos();
        adapter = new Promoadapter(PromotionsActivity.this,Promotions);
        recyclerView.setAdapter(adapter);
        if(Promotions.isEmpty())
        {
            nopromos.setVisibility(View.VISIBLE);
            swipe.setVisibility(View.VISIBLE);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(this,MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }
}
