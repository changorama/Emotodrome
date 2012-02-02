package emotodrome.data;

import android.content.Context;

public class MyLocationManager {
	private MyLocation myLocation;
	private LocationResult locationResult;
	private Context context;
	private int zoom;
	
	public MyLocationManager(Context context) {
		System.out.println("Initializing Location Manager");
		myLocation = new MyLocation();
		this.context = context;
		locationResult = new LocationResult();
		zoom = 15;
		
		System.out.println("Call Get Location");
		
		System.out.println(myLocation.getLocation(this.context, locationResult));
		if(myLocation.getLocation(this.context, locationResult))
			System.out.println("Got location in MyLocationManager constructor");
		else
			System.out.println("Did not get location in MyLocationManager constructor");
	}
	
	public double getLongitude() {
		return locationResult.getLongitude();
	}
	public double getLatitude() {
		return locationResult.getLatitude();
	}
	public int zoom() {
		return zoom;
	}
}
