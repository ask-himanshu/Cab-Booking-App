package one.prototype.aptlegion.limocart;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elitebook on 17-Jun-15.
 */
public class GlobalVaraible extends Application {
    String token;
    HashMap<String,String>userinfo,bookingById = new HashMap<>();
    ArrayList<Openbookingdata>bookingdata;
    ArrayList<Openbookingdata> allbookings;
    ArrayList<Openbookingdata> completedbookiings;

    ArrayList<Promotions>Promos =new ArrayList<>();
    Location mylocation;

    public void setToken(String token)
    {
        this.token=token;
    }
    public String getToken()
    {
        return this.token;
    }
    public void setUserinfo (HashMap userinfo)
    {
        this.userinfo = userinfo;
    }
    public HashMap getUserinfo()
    {
        return  this.userinfo;
    }
    public void setBookingdata (ArrayList<Openbookingdata> bookingdata)
    {
        this.bookingdata=bookingdata;
    }
    public ArrayList<Openbookingdata> getBookingdata()
    {
        return  this.bookingdata;
    }
    public void setAllbookings (ArrayList<Openbookingdata> bookingdata)
    {
        this.allbookings=bookingdata;
    }
    public ArrayList<Openbookingdata> getAllbookings()
    {
        return  this.allbookings;
    }
    public void setMylocation(Location location)
    {
        mylocation =location;
    }
    public Location getMylocation()
    {
        return this.mylocation;
    }
    public  void  setCompletedbookiings(ArrayList<Openbookingdata> completedbookiings)
    {
        this.completedbookiings = completedbookiings;
    }
    public ArrayList<Openbookingdata> getCompletedbookiings()
    {
        return this.completedbookiings;
    }
    public void setPromotions(ArrayList<Promotions> promotions)
    {
        this.Promos=promotions;
    }
    public ArrayList<Promotions>getPromos(){return Promos;}

    public void setBookingById(HashMap bookingById ){
        this.bookingById = bookingById;
    }

    public HashMap getBookingById(){
        return this.bookingById;
    }

}
