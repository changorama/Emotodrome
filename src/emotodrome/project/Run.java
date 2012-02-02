package emotodrome.project;

import java.io.IOException;
import java.net.MalformedURLException;

import emotodrome.data.Backend;
import emotodrome.data.Server;
import emotodrome.project.R;
import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Title: Emotodrome
 * 
 * @DISCLAIMER
 * This source and the whole package comes without warranty. It may or may
 * not harm your computer or cell phone. Please use with care. Any damage
 * cannot be related back to the author. The source has been tested on a
 * virtual environment and scanned for viruses and has passed all tests.
 * 
 * Code created with help from: 
 * {@linkhttp://insanitydesign.com/wp/projects/nehe-android-ports/}
 * {@linkhttp://iphonedevelopment.blogspot.com/}
 * {@linkhttp://blog.jayway.com/2010/02/15/opengl-es-tutorial-for-android-%E2%80%93-part-v/}
 * 
 * @author Seano Whitecloud, Justin Murray, Darren Cheng, and Luke Fowlie
 * 
 * Class Description: Initializes android activity, sets renderer
 * 
 *MOVE____ EVENT TRIGGERED, CALLS ON BACKEND TO UPDATE IMAGE LOCATION A,B,C, RECEIVES A DONE
 */
public class Run extends Activity {
	
	private OpenGLRenderer openGLRenderer;
	private Backend backend; 
	private TextView textView;	
	//private ImageView imageView;
	private Button button;
	private boolean spawnerToggle, shouldGetImage, shouldUpdateData;
	private int updatedCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		//String s = Server.getFileFromServer("README");

		backend = new Backend(this);
		
		textView = (TextView)findViewById(R.id.text); 
		textView.setText("***WELCOME*** (" + backend.getLatitude() + "," + backend.getLongitude() + ")");
		
		GLSurfaceView glView = (GLSurfaceView) findViewById(R.id.surfaceview);
		openGLRenderer = new OpenGLRenderer(this, glView, backend);
		//imageView = (ImageView)findViewById(R.id.image);
		//Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath());
		//imageView.setImageBitmap(bm);
		
		/*new Thread(new Runnable(){
			@Override
			public void run(){
				while (true){
					backend.getServerUpdates();
				}
			}
			
		});*/

		shouldGetImage = true;
		shouldUpdateData = true;
		spawnerToggle = true;
		updatedCount = 0;
		
		//new BackendThreadSpawner().execute();
		//new MapUpdater().execute();
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//setContentView(openGLRenderer);
		//glView.setRenderer(openGLRenderer);
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		//openGLRenderer.onResume();
	}
	
	protected void onDestroy(){
		try {
			backend.closeConnections();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		spawnerToggle = false;
		//openGLRenderer.onPause();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection	
	    Intent intent = new Intent(this, RunMap.class);
        startActivityForResult(intent, 0);
	    item.setTitle("3D View");
	    return super.onOptionsItemSelected(item);
	}
	
	public void changeZoom(View view) {
		backend.changeZoom();
	}
	/*
	protected class NewImageUpdateThread extends AsyncTask<Integer, Void, Void> {
		private int index;
		@Override
		protected Void doInBackground(Integer... params) {
			index = params[0];
			System.out.println(index);
			
			backend.spawnImageDownload(index);
			return null;
		}
		@Override
		protected void onPostExecute(Void params) {
			updatedCount++;
			System.out.println(backend.getImagePath()+"/"+updatedCount+backend.getImageName(index));
			//openGLRenderer.updateMapTextures(backend.getImagePath()+"/"+index+backend.getImageName(), index);
			System.out.println("Displayed Image Map "+index + ", " + updatedCount +" of 9 done");
			backend.switchBuffer(index);
			if(updatedCount == 9) {
				System.out.println("Finished Displaying All Map Images");
				//buttonAvailable = true;
				//button.setText("Change Zoom");
			}
		}
	}
	
	
	protected class MapUpdater extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			int i = 0;
			while(spawnerToggle && shouldGetImage) {
				System.out.println("Start Map Image Update Thread");
				new NewImageUpdateThread().execute(i % 9);
				i++;
				try { Thread.sleep(10000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return null;
		}
	}
	
//	protected class ImageUpdateThread extends AsyncTask<Void, Void, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
//			shouldGetImage = false;
//			System.out.println("Start Updating Map Image");
//			boolean isUpdated = backend.getIsSwap();
//			backend.updateMapImage();
//			while(isUpdated == backend.getIsSwap()) {
//				try { Thread.sleep(500); }
//				catch (InterruptedException e) { e.printStackTrace(); }
//			}
//			return null;
//		}
//		@Override
//		protected void onProgressUpdate(Void... params) { }
//		@Override
//		protected void onPostExecute(Void params) {
//			//Bitmap bm = BitmapFactory.decodeFile(backend.getImagePath());
//			//imageView.setImageBitmap(bm);
//			//openGLRenderer.loadTextures(openGLRenderer.mygl, backend.getImagePath());
//			openGLRenderer.updateTexture(backend.getImagePath());
//			System.out.println("Finished Updating Map Image from path" + backend.getImagePath());
//			shouldGetImage = true;
//		}
//	}	
//

	protected class BackendThreadSpawner extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while(spawnerToggle && shouldUpdateData) {
				new BackendThread().execute();
				try { Thread.sleep(3000); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
			return null;
		}
	}

	protected class BackendThread extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			shouldUpdateData = false;
			backend.queryServer();
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... params) { }
		@Override
		protected void onPostExecute(Void params) {
			shouldUpdateData = true;
			textView.setText(backend.getServerResponse());
		}
	}
	*/
	
}