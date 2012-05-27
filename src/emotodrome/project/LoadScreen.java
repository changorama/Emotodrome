package emotodrome.project;

import emotodrome.data.Backend;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class LoadScreen extends Activity{
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.load_screen);
		context = this;
		new LoadBackendThread().execute(this);
	}
	
	protected class LoadBackendThread extends AsyncTask<Activity, Void, Void>{
		
		Activity activity;
		@Override
		protected Void doInBackground(Activity... a) {
			activity = a[0];
			activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Run.backend = new Backend(activity);
				}
			});

			return null;
		}
		
		protected void onPostExecute(Activity result){
			startActivity(new Intent(context, Run.class));
		}
	}

}
