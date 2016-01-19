package com.weijie.firerunning.map;

public class LocationPoint {  
	  
    private long _id;  
    private double latitude;  
    private double longitutde;  
  
    public long get_id() {  
        return _id;  
    }  
  
    public void set_id(long _id) {  
        this._id = _id;  
    }  
  
    public double getLatitude() {  
        return latitude;  
    }  
  
    public void setLatitude(double latitude) {  
        this.latitude = latitude;  
    }  
  
    public double getLongitutde() {  
        return longitutde;  
    }  
  
    public void setLongitutde(double longitutde) {  
        this.longitutde = longitutde;  
    }  
  
    @Override  
    public String toString() {  
        return "LocationPoint [_id=" + _id + ", latitude=" + latitude  
                + ", longitutde=" + longitutde + "]";  
    }  
}  