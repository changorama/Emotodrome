package emotodrome.data;

public class Mercator {
	private static final int WIDTH = 256;
	private static final int HEIGHT = 256;
	
	public static double getBottomFromLat(double latitude, int zoom) {
		return fromPixToLat(fromLatToPix(latitude, zoom) + HEIGHT, zoom);
	}
	
	public static double getTopFromLat(double latitude, int zoom) {
		return fromPixToLat(fromLatToPix(latitude, zoom) - HEIGHT, zoom);
	}
	
	public static double getRightFromLng(double longitude, int zoom) {
		return fromPixToLng(fromLngToPix(longitude, zoom) + WIDTH, zoom);
	}
	
	public static double getLeftFromLng(double longitude, int zoom) {
		return fromPixToLng(fromLngToPix(longitude, zoom) - WIDTH, zoom);
	}
	
	private static int fromLngToPix(double longitude, int zoom) {
		double lngrad = deg2rad(longitude);
		double mercx = lngrad;
		double cartx = mercx + Math.PI;
		double pixelx = cartx * WIDTH/(2*Math.PI);
		double pixelx_zoom = pixelx * Math.pow(2, zoom);
		return (int)pixelx_zoom;
	}

	private static int fromLatToPix(double latitude, int zoom)
  {
  	double pixely;
    if (latitude == 90.0)
      pixely = 0.0;
    else if (latitude == -90.0)
      pixely = HEIGHT;
    else {
      double latrad = deg2rad(latitude);
      double mercy = Math.log(Math.tan(Math.PI/4+latrad/2));
      double carty = Math.PI - mercy;
      pixely = carty * HEIGHT / 2 / Math.PI;
      pixely = Math.max(0, pixely);    // correct rounding errors near north and south poles
      pixely = Math.min(HEIGHT, pixely);  // correct rounding errors near north and south poles
    }
    double pixely_zoom = pixely * Math.pow(2,zoom);
    return (int)pixely_zoom;
  }

	private static double fromPixToLng(int pixelx_zoom, int zoom)
  {
    double pixelx = pixelx_zoom / Math.pow(2,zoom);
    double cartx = pixelx / WIDTH * 2 * Math.PI;    
    double mercx = cartx - Math.PI;
    double lngrad = mercx;
    double lng = rad2deg(lngrad);
    return lng;
  }

	private static double fromPixToLat(int pixely_zoom, int zoom)
  {        
    double pixely = pixely_zoom / Math.pow(2,zoom);
    double lat;
    if (pixely == 0.0) 
      lat = 90.0; 
    else if (pixely == HEIGHT)
      lat = -90.0;
    else {
      double carty = pixely / HEIGHT* 2 * Math.PI;
      double mercy = Math.PI - carty;
      double latrad = 2 * Math.atan(Math.exp(mercy))-Math.PI/2;
      lat = rad2deg(latrad);
    }
    return lat;
  }

  
  
  
  
	
	
	private static double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}
	private static double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}
}
