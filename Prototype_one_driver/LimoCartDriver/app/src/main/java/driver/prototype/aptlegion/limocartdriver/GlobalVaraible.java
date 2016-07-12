package driver.prototype.aptlegion.limocartdriver;

import android.app.Application;
import android.app.Fragment;
import android.location.Location;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elitebook on 17-Jun-15.
 */
public class GlobalVaraible extends Application {
    String token;
    HashMap<String,String>userinfo = new HashMap<>();
    ArrayList<openbookingdata>bookingdata= new ArrayList<>();
    ArrayList<openbookingdata>acceptedbooking= new ArrayList<>();
    ArrayList<openbookingdata>allbookings= new ArrayList<>();
    Handler handler;
    Location location;

    public void setToken(String token)
    {
        this.token=token;
    }
    public String getToken()
    {
        return this.token;
    }
    public void setUserinfo (HashMap userinfo){this.userinfo = userinfo;}
    public HashMap getUserinfo(){return  this.userinfo;}

    public void setBookingdata (ArrayList<openbookingdata> bookingdata){this.bookingdata=bookingdata;}
    public ArrayList<openbookingdata> getBookingdata(){return  this.bookingdata;}

    public  void setAcceptedbooking(ArrayList<openbookingdata> bookingdata){this.acceptedbooking = bookingdata;}
    public ArrayList<openbookingdata> getAcceptedbooking(){return this.acceptedbooking;}

    public  void setAllbookings(ArrayList<openbookingdata> bookingdata){this.allbookings = bookingdata;}
    public ArrayList<openbookingdata> getAllbookings(){return this.allbookings;}

    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }
    public Handler getHandler(){return this.handler;}

    public void setLocation(Location location){this.location = location;}
    public Location getLocation(){return this.location;}



}
