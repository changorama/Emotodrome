package emotodrome.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class RunMap extends MapActivity{

	private MapView mapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map);
		
		mapView = (MapView)findViewById(R.id.mapview);
		
		
	}
	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    menu.getItem(0).setTitle("3D View");
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection	
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
	    return super.onOptionsItemSelected(item);
	}
}
