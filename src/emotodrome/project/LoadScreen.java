package emotodrome.project;

import emotodrome.data.Backend;
import emotodrome.data.MyLocationManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class LoadScreen extends Activity{
	private Context context;
	private MyLocationManager locationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = new MyLocationManager(this);
		setContentView(R.layout.load_screen);
		context = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		new LoadBackendThread().execute(this);
	}
	
	protected class LoadBackendThread extends AsyncTask<Activity, Void, Void>{
		
		Activity activity;
		@Override
		protected Void doInBackground(Activity... a) {
			activity = a[0];
//			activity.runOnUiThread(new Runnable(){
//				@Override
//				public void run() {
					Run.backend = new Backend(activity, locationManager);
//				}
//			});

			return null;
		}
		
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			if (!Run.backend.checkConnected()){
				Toast toast = Toast.makeText(activity, "Error connecting to server, please check your connection", Toast.LENGTH_SHORT);
				toast.show();
				finish();
			}
			else{
				startActivity(new Intent(context, Run.class));
				finish();
			}
		}
	}

}