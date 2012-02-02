package emotodrome.data;

import android.location.Location;

public class LocationResult{
	private Location location;
	private final double DEFAULT_LONG = -75.29;
	private final double DEFAULT_LAT = 43.0;
	
	public LocationResult() {
		location = null;
	}
	
  public void gotLocation(Location location) {
  	this.location = location;
  	if (location != null){
	  	System.out.println("Got Location Called - Location Stored");
	  	System.out.println("latitude: " + location.getLatitude());
	  	System.out.println("longitude: " + location.getLongitude());
  	}
  }
  
  public double getLatitude() {
  	if(location == null)
  		return DEFAULT_LAT;
  	return location.getLatitude();
  }
  
  public double getLongitude() {
  	if(location == null)
  		return DEFAULT_LONG;
  	return location.getLongitude();
  }
}