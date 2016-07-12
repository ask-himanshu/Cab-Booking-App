package driver.prototype.aptlegion.limocartdriver;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Elitebook on 18-Jun-15.
 */
public class openbookingdata implements Serializable {

    public String bookingid;
    public  String companyid;
    public  String driverid;
    public  String status;
    public  String sourceAddress;
    public  String destinationAddress;
    public  String bookingtime;
    public  String distance;
    public  String promotionid;
    public  String haspaid;
    public  String starttime;
    public  String endtime;
    public  String customerfeedback;
    public  String driverfeedback;
    public  String createdby;
    public  String modifiedby;
    public  String createdat;
    public  String updatedat;
    public  String SourceLatitude;
    public  String SourceLongitude;
    public  String DestinationLatitude;
    public  String DestinationLongitude;

    public String getBookingid() {
        return bookingid;
    }

    public void setBookingid(String bookingid) {
        this.bookingid = bookingid;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getBookingtime() {
        return bookingtime;
    }

    public void setBookingtime(String bookingtime) {
        this.bookingtime = bookingtime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(String promotionid) {
        this.promotionid = promotionid;
    }

    public String getHaspaid() {
        return haspaid;
    }

    public void setHaspaid(String haspaid) {
        this.haspaid = haspaid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCustomerfeedback() {
        return customerfeedback;
    }

    public void setCustomerfeedback(String customerfeedback) {
        this.customerfeedback = customerfeedback;
    }

    public String getDriverfeedback() {
        return driverfeedback;
    }

    public void setDriverfeedback(String driverfeedback) {
        this.driverfeedback = driverfeedback;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public String getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(String modifiedby) {
        this.modifiedby = modifiedby;
    }

    public String getCreatedat() {
        return createdat;
    }

    public void setCreatedat(String createdat) {
        this.createdat = createdat;
    }

    public String getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(String updatedat) {
        this.updatedat = updatedat;
    }

    public String getSourceLatitude() {
        return SourceLatitude;
    }

    public void setSourceLatitude(String sourceLatitude) {
        SourceLatitude = sourceLatitude;
    }

    public String getSourceLongitude() {
        return SourceLongitude;
    }

    public void setSourceLongitude(String sourceLongitude) {
        SourceLongitude = sourceLongitude;
    }

    public String getDestinationLatitude() {
        return DestinationLatitude;
    }

    public void setDestinationLatitude(String destinationLatitude) {
        DestinationLatitude = destinationLatitude;
    }

    public String getDestinationLongitude() {
        return DestinationLongitude;
    }

    public void setDestinationLongitude(String destinationLongitude) {
        DestinationLongitude = destinationLongitude;
    }
}
