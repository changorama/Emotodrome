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
import android.widget.Toast;


import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Context;



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
 * @author Seano Whitecloud, Justin Murray, Darren Cheng, Luke Fowlie, and Dohwee Kim
 * 
 * Class Description: Initializes android activity, sets renderer
 * 
 *MOVE____ EVENT TRIGGERED, CALLS ON BACKEND TO UPDATE IMAGE LOCATION A,B,C, RECEIVES A DONE
 */
public class Run extends Activity {
	
	public static Backend backend; 
	private OpenGLRenderer openGLRenderer;
	private TextView textView;	
	private Button button;
	private boolean spawnerToggle, shouldGetImage, shouldUpdateData;
	private int updatedCount;
	private boolean optionsVisible = false;
	
	
	//Variables for sound control
	private boolean volcontrolVisible = false;
	
	//private SoundPool soundEffects;
	SoundManager snd;
	private Context pContext;            // local copy of app context 
	public int bellSound , drumSound;	
	public SeekBar seekvolbar;  // Volume control bar 
	OnSeekBarChangeListener barChange; //Handler for seekbar
	
	// 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Sound files loading part
		snd = new SoundManager(getApplicationContext());  //Create an instance of our sound manager 
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);  //Set volume rocker mode to media volume
		bellSound = snd.load(R.raw.gong_burmese);
		drumSound = snd.load(R.raw.bowla_emoto);
		
		// Seekbar listener, This will detect moving signal by seekbar 
		barChange = new OnSeekBarChangeListener(){
			@Override
			public void onStopTrackingTouch(SeekBar seekBar){    }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {  }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				switch (seekBar.getId()){
				case R.id.volbar:
					snd.setVolume((float)progress/100.0f);
					break;
				}
			}
			
		};
		seekvolbar = (SeekBar)findViewById(R.id.volbar);
		seekvolbar.setOnSeekBarChangeListener(barChange);
		
		//soundEffects = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
	
		
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
			backend.closeConnections();			//if application is closed, close connections to server
			if (openGLRenderer != null){
				openGLRenderer.locating = false;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		spawnerToggle = false;
		//openGLRenderer.onPause();
	}
	
	//create options menu
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	//called when options item is clicked
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
	
	
	public void toggleOptions(View view){
		if (optionsVisible) {
			findViewById(R.id.optionSub1).setVisibility(View.GONE);
			findViewById(R.id.optionSub2).setVisibility(View.GONE);
		}
		else {
			findViewById(R.id.optionSub1).setVisibility(View.VISIBLE);
			findViewById(R.id.optionSub2).setVisibility(View.VISIBLE);
		}
		optionsVisible = !optionsVisible;
	}
	
	//handles press on the main option button
	public void changeOption(View view){
		Button option = (Button) view;
		Button main = (Button) findViewById(R.id.optionMain);
		CharSequence current = main.getText();
		CharSequence newText = option.getText();
		main.setText(newText);
		option.setText(current);
		toggleOptions(main);
		
		if (newText.equals("SPEED")){
			setSpeedIcon();
		}
		else if (newText.equals("LOCATE")){
			setLocateIcon();
		}
	}
	
	//add locate icon to the screen
	private void setLocateIcon() {
		Button loc = (Button) findViewById(R.id.currentOption);
		loc.setBackgroundResource(R.drawable.locate1);		
	}

	//handles click on a side tab
	public void currentOptionClicked(View view){
		Button main = (Button) findViewById(R.id.optionMain);
		String current = main.getText().toString();
		if (current.equals("SPEED")){
			changeSpeed();
		}
		else if (current.equals("LOCATE")){
			openGLRenderer.toggleLocating();
		}
	}
	

	
	// The case Volume button has been clicked by user 
	public void volumeClicked(View View){
		//Testing playing Sound 
		//playSound(soundSpeed , soundVolume);
		snd.play(bellSound);
		snd.play(drumSound);
		
		if (volcontrolVisible) {
			findViewById(R.id.volbar).setVisibility(View.GONE);
			//findViewById(R.id.optionSub1).setVisibility(View.GONE);
			//findViewById(R.id.optionSub2).setVisibility(View.GONE);
		}
		else {
			findViewById(R.id.volbar).setVisibility(View.VISIBLE);
			//findViewById(R.id.optionSub1).setVisibility(View.VISIBLE);
			//findViewById(R.id.optionSub2).setVisibility(View.VISIBLE);
		}
		volcontrolVisible = !volcontrolVisible;
	}
	
	
	
	public void volplusclicked(View View){
		
	}
	
	public void volminusclicked(View View){
		
	}
	public void playSound(float fSpeed, float fvolume){
		//soundEffects.play(bellSound, fvolume, fvolume, 0, 1, fSpeed);
		//soundEffects.play(drumSound, fvolume, fvolume, 0, 1, fSpeed);
	}
	//increase speed unless we are at max speed, in which case go back to first speed
	private void changeSpeed(){
		if (openGLRenderer.speed  == openGLRenderer.SPEED1)
		{
			openGLRenderer.speed = openGLRenderer.SPEED2;
		}
		else if (openGLRenderer.speed  == openGLRenderer.SPEED2)
		{
			openGLRenderer.speed = openGLRenderer.SPEED3;
		}
		else if (openGLRenderer.speed  == openGLRenderer.SPEED3)
		{
			openGLRenderer.speed = openGLRenderer.SPEED4;
		}
		else
		{
			openGLRenderer.speed = openGLRenderer.SPEED1;
		}
		setSpeedIcon();
	}
	
	//set the speed icon based on our current speed
	private void setSpeedIcon(){
		Button speed = (Button) findViewById(R.id.currentOption);
		if (openGLRenderer.speed  == openGLRenderer.SPEED2)
			speed.setBackgroundResource(R.drawable.speed2);
		else if (openGLRenderer.speed  == openGLRenderer.SPEED3)
			speed.setBackgroundResource(R.drawable.speed3);
		else if (openGLRenderer.speed  == openGLRenderer.SPEED4)
			speed.setBackgroundResource(R.drawable.speed4);
		else
			speed.setBackgroundResource(R.drawable.speed1);
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