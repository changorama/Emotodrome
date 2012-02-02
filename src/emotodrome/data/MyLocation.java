package emotodrome.data;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class MyLocation {
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled=false;
    boolean network_enabled=false;
    Handler handler = new Handler();
    
    public boolean getLocation(Context context, LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
          lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        System.out.println("Initialize Real Location Manager");

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}
        catch(Exception ex){System.out.println("GPS Exception" + ex.getMessage());}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}
        catch(Exception ex){System.out.println("Network Exception" + ex.getMessage());}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled) {
        	System.out.println("No listeners enabled");
          return false;
        }

        if(gps_enabled) {
        	System.out.println("Get Updates From GPS");
          lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
          System.out.println("After Request");
          Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
          if(loc != null) {
          	locationResult.gotLocation(loc);
//          	System.out.println("lat: " + loc.getLatitude());
//          	System.out.println("long: " + loc.getLongitude());
          }
        }
        if(network_enabled) {
        	System.out.println("Get Updates From Network");
          lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
          System.out.println("After Request");
          Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
          if(loc != null) {
          	locationResult.gotLocation(loc);
          }
        }

        
        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 20000);

        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
        	handler.post(new Runnable() {
        		public void run() {
        	
             lm.removeUpdates(locationListenerGps);
             lm.removeUpdates(locationListenerNetwork);

             Location net_loc=null, gps_loc=null;
             if(gps_enabled)
                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(network_enabled)
                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime())
                     locationResult.gotLocation(gps_loc);
                 else
                     locationResult.gotLocation(net_loc);
                 return;
             }

             if(gps_loc!=null){
                 locationResult.gotLocation(gps_loc);
                 return;
             }
             if(net_loc!=null){
                 locationResult.gotLocation(net_loc);
                 return;
             }
             locationResult.gotLocation(null);
        		}
        	});
        }
    }
}
