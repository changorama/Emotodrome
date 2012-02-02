package emotodrome.project;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import emotodrome.data.Backend;
import emotodrome.data.LocationValuePair;
import emotodrome.mesh.*;
import emotodrome.user.Camera;
import emotodrome.user.User;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.GestureDetector.OnGestureListener;

/**
 * This is a port of the {@link http://nehe.gamedev.net} OpenGL 
 * tutorials to the Android 1.5 OpenGL ES platform. Thanks to 
 * NeHe and all contributors for their great tutorials and great 
 * documentation. This source should be used together with the
 * textual explanations made at {@link http://nehe.gamedev.net}.
 * The code is based on the original Visual C++ code with all
 * comments made. It has been altered and extended to meet the
 * Android requirements. The Java code has according comments.
 * 
 * If you use this code or find it helpful, please visit and send
 * a shout to the author under {@link http://www.insanitydesign.com/}
 * 
 * @DISCLAIMER
 * This source and the whole package comes without warranty. It may or may
 * not harm your computer or cell phone. Please use with care. Any damage
 * cannot be related back to the author. The source has been tested on a
 * virtual environment and scanned for viruses and has passed all tests.
 * 
 * 
 * This is an interpretation of "Lesson 07: Texture Mapping"
 * for the Google Android platform.
 * 
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class OpenGLRenderer implements Renderer, OnGestureListener, SensorEventListener {
	public GL10 mygl;
	
	private Backend backend;
	
	private HashMap<Integer, User> users;
	private HashMap<Vec3, Float> iceData;
	private Group ice;
	/** Root instance */
	private Group root;
	private Group pyrite;
	private AnchoredBezier bezier;
	//private Mesh map;
	private Group sky;
	private Mesh userAvatar;
	
	//private Mesh skyfront;
	private final int NUMSHAPES = 10;
	private final double SENSITIVITY = 0.5; // Sensitivity of motion controls.
	//private boolean YAW = false; // Whether or not tilt controls yaw instead of roll.  I prefer roll.
	
	private final float skyHeight = 101f;
	private final float skyDist = 2000f;
	private final float MAPWIDTH = 200;
	private final float MAPHEIGHT = 200;
	private final int MAPROWS = 3;
	private final int MAPCOLUMNS = 3;
	private float mapMoveForward;
	private float mapMoveBackward;
	private float mapMoveRight;
	private float mapMoveLeft;
	
	public final int NUMMAPIMAGES = MAPROWS * MAPCOLUMNS;
	private boolean updateTexture = false;
	private Group mapgroup;
	
	/** Is light enabled ( NEW ) */
	private boolean light = true;

	/* 
	 * The initial light values for ambient and diffuse
	 * as well as the light position ( NEW ) 
	 */
	private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightSpecular = {0.5f, 1.0f, 0.5f, 1.0f};
	private float[] lightPosition = {0.0f, 2.0f, 2.0f, 1.0f};
	
	private float[] light1Position = {0.0f, 1.0f, 0.0f, 1.0f};
	
	private float[] light2Position = {0.0f, -1.0f, 0.0f, 1.0f};
	
	//rgb is 181 208 209
	private float[] fogColor = {.7098f, .8156f, .8196f};
	
	private float[] gravity = null;
	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightSpecularBuffer;
	private FloatBuffer lightPositionBuffer;
	private FloatBuffer light1PositionBuffer;
	private FloatBuffer light2PositionBuffer;
	private FloatBuffer fogColorBuffer;
	
	private float moveBezier = 0.0f;
	
	Random r;
	
    private GestureDetector detector;
    
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
	
    private Camera camera;
    
	/** The Activity Context */
	private Context context;
	
	/**
	 * Instance the Cube0 object and set the Activity Context 
	 * handed over. Initiate the light buffers and set this 
	 * class as renderer for this now GLSurfaceView.
	 * Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons  
	 * 
	 * @param context - The Activity Context
	 */
	public OpenGLRenderer(Context context, GLSurfaceView view, Backend backend) {
		//super(context);
		Rect bounds = new Rect();
		view.getHitRect(bounds);
		view.setTouchDelegate(new TouchDelegate(bounds, view){
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				
				return detector.onTouchEvent(event);
			}
		});
		
		//Set this as Renderer
		view.setRenderer(this);
		//Request focus, otherwise buttons won't react
		view.requestFocus();
		view.setFocusableInTouchMode(true);
		
		
		//
		this.context = context;		
		this.backend = backend;
		
        detector = new GestureDetector(this);
        
        //Accelerometer stuff.
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightSpecular.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightSpecularBuffer = byteBuf.asFloatBuffer();
		lightSpecularBuffer.put(lightSpecular);
		lightSpecularBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(light1Position.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		light1PositionBuffer = byteBuf.asFloatBuffer();
		light1PositionBuffer.put(light1Position);
		light1PositionBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(light2Position.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		light2PositionBuffer = byteBuf.asFloatBuffer();
		light2PositionBuffer.put(light2Position);
		light2PositionBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(fogColor.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		fogColorBuffer = byteBuf.asFloatBuffer();
		fogColorBuffer.put(fogColor);
		fogColorBuffer.position(0);
		
		//
		mapgroup = new Group();
		for (int i = 0; i < NUMMAPIMAGES; i++){
			Plane p = new Plane(MAPWIDTH, MAPHEIGHT);
			p.rz = -90;
			p.rx = -90;
			p.y = -1;
			p.z = (float) (-MAPHEIGHT + MAPHEIGHT * (i/MAPROWS) + backend.longitude);
			p.x = (float) (-MAPWIDTH + MAPWIDTH * (i%MAPCOLUMNS) + backend.latitude);
			mapgroup.add(i, p);
		}

		mapMoveForward = -MAPHEIGHT/2;
		mapMoveBackward = MAPHEIGHT/2;
		mapMoveRight = MAPWIDTH/2;
		mapMoveLeft = -MAPWIDTH/2;
		
		Mesh skytop = new Plane(skyDist, skyDist, 5, 5);
		skytop.rx = 90;
		skytop.y = skyHeight;
		sky = new Group();
		sky.add(skytop);
		
		root = new Group();
		for (int i = 0; i < NUMSHAPES; i++){
			Mesh cube = new Cube(.5f, .5f, .5f);
			cube.x = 0;
			cube.y = 0;
			cube.z = -i;
			root.add(cube);	
		}
		
		users = new HashMap<Integer, User>();
		iceData = backend.processIceData();
		ice = new Group();
		pyrite = new Group();
		backend.listenUserUpdates(users);
		
		r = new Random();
		//pyrite = new Pyrite(.5f, .5f, .5f, r);
		
		//bezier = new AnchoredBezier(0, 0, 0, 0 , 1, 0, 20);

		//camera = new Camera(new Vec3((float) backend.longitude, 0f, (float)backend.latitude));
		camera = new Camera(new Vec3(0f, 0f, 0f));
		userAvatar = new Cube(1f, 1f, 1f);
		userAvatar.x = camera.getEyeX();
		userAvatar.y = camera.getEyeY();
		userAvatar.z = camera.getEyeZ();
		
//		Circle c = new Circle(5, 100);
//		c.y = 0;
//		c.x = camera.getEyeX();
//		c.z = camera.getEyeZ() -1;
		//CircleWave c = new CircleWave(5, .01f, 1, .1f, 1f, .01f, .1f, 0f, 0f, 2f, new float[] {0,0,0,1}, new float[]{0,1,0,1});
		Circle c = new Circle(1, .1f, 1, new float[]{0, 1, 0, 1});
		c.z = -5;
		ice.add(c);
		
//		ice = new TriangleOrigami[170][360];
//		for (int i = 0; i < 170; i++){
//			for (int j = 0; j < 360; j++){
//				if (iceData[i][j] > 0){
//					TriangleOrigami t = new TriangleOrigami(new Vec3(i, 0, j), new Vec3(1, 1, 1), new Vec3(-1, 1, -1), r);
//					ice[i][j] = t;
//					
//				}
//			}
//		}
		
//		new Thread(new Runnable(){
//			@Override
//			public void run(){
//				addIce();
//			}
//		}).start();
//		new Thread(new Runnable(){
//			@Override
//			public void run(){
//				removeIce();
//			}
//		}).start();
		System.out.println("done");
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		mygl = gl;
		//And there'll be light!
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecularBuffer);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT0);											//Enable Light 0 ( NEW )

		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, light1PositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT1);
		
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, light2PositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT2);

		//Settings
		gl.glDisable(GL10.GL_DITHER);				//Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(.7098f, .8156f, .8196f, 0.5f); 	//same as fog
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		gl.glEnable(GL10.GL_FOG);
		
		gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_LINEAR);
		gl.glFogfv(GL10.GL_FOG_COLOR, fogColorBuffer);
		gl.glFogf(GL10.GL_FOG_START, 50f);
		gl.glFogf(GL10.GL_FOG_END, 300f);
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
		
		for (int i = 0; i < NUMMAPIMAGES; i++){
			updateMapTextures(backend.getImagePath() + "/" + i + backend.getImageName(i), i);
		}
		updateTexture = true;
		//skyfront.loadGLTexture(gl, this.context, R.drawable.sky1);
		for (int i = 0; i < sky.size(); i++){
			sky.get(i).loadGLTexture(gl, this.context, R.drawable.sky1);
		}
		
		
	}

	
	public void updateMapTextures(String loc, int index){
		mapgroup.get(index).setTextureLocation(loc);
	}
	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		//Check if the light flag has been set to enable/disable lighting
		if(light) {
			gl.glEnable(GL10.GL_LIGHTING);
		} else {
			gl.glDisable(GL10.GL_LIGHTING);
		}
		
		camera.moveCamera();
		if (camera.getMoveForward() == true){
			backend.updateUserLocation(camera.getEye());
		}
		GLU.gluLookAt(gl, camera.getEyeX(), camera.getEyeY(), camera.getEyeZ(), 
				camera.getPerspX(), camera.getPerspY(), camera.getPerspZ(),
				camera.getUpX(), camera.getUpY(), camera.getUpZ());
		
		if (camera.getEyeZ() < mapMoveForward){
			mapMoveForward -= MAPHEIGHT;
			new Thread(new Runnable(){
				@Override
				public void run(){
					onMapMoveForward();
				}
				
			}).start();
		}
		else if (camera.getEyeZ() > mapMoveBackward){
			mapMoveBackward += MAPHEIGHT;
			new Thread(new Runnable(){
				@Override
				public void run(){
					onMapMoveBackward();
				}
				
			}).start();
		}
		if (camera.getEyeX() > mapMoveRight){
			mapMoveRight += MAPWIDTH;
			new Thread(new Runnable(){
				@Override
				public void run(){
					onMapMoveRight();
				}
				
			}).start();
		}
		else if (camera.getEyeX() < mapMoveLeft){
			mapMoveLeft -= MAPWIDTH;
			new Thread(new Runnable(){
				@Override
				public void run(){
					onMapMoveLeft();
				}
				
			}).start();
		}
		if (updateTexture){
			for (int i=0; i < NUMMAPIMAGES; i++){
				Plane p = (Plane) mapgroup.get(i);
				p.loadDownloadedTexture(gl, this.context);
			}
			updateTexture = false;
		}
		
		userAvatar.x = camera.getEyeX();
		userAvatar.y = camera.getEyeY();
		userAvatar.z = camera.getEyeZ() + 1;
		
		//gl.glPushMatrix();
		//userAvatar.draw(gl);
		//gl.glPopMatrix();
		
		Collection<User> collection = users.values();
		gl.glPushMatrix();
		for (User user:collection){
			Mesh avatar = user.getUserAvatar();
			if (avatar == null){
				avatar = user.setUserAvatar(new Plane(1, 1));
				avatar.rx = 90;
				avatar.loadGLTexture(gl, context, R.drawable.avatar);
			}
			Vec3 userVector = user.getUserVector();
			avatar.x = userVector.x;
			avatar.y = userVector.y;
			avatar.z = userVector.z;
			user.draw(gl);
		}
		gl.glPopMatrix();

//		for (int i = (int) (camera.getEyeX()); i < camera.getEyeX() + 20; i++){
//			for (int j = (int) camera.getEyeZ(); j < camera.getEyeZ() + 20; j++){
//				if (i > 0 && j > 0 && i < 170 && j < 360 && ice[i][j] != null)
//					ice[i][j].draw(gl);
//			}
//		}
		
		//pyrite.draw(gl);
//		gl.glDisable(GL10.GL_TEXTURE_2D);
//		for (int i = 0; i < ice.size(); i++){
//			gl.glPushMatrix();
//			ice.get(i).draw(gl);
//			gl.glPopMatrix();
//		}
//		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glPushMatrix();
		mapgroup.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		sky.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		ice.draw(gl);
		gl.glPopMatrix();

		gl.glDisable(GL10.GL_TEXTURE_2D);
//		angle = (angle + 3.0f) % 360;
//		for (int i = 0; i < NUMSHAPES; i++){
//			gl.glPushMatrix();
//			Mesh cube = root.get(i);
//			gl.glTranslatef(cube.x, cube.y, cube.z);
//			cube.ry = angle * (i % 2 == 0 ? 1 : -1);
//			cube.draw(gl);
//			gl.glPopMatrix();
//			
//		}
//		gl.glPushMatrix();
//		pyrite.draw(gl);
//		gl.glPopMatrix();
		
		//gl.glPushMatrix();
		//moveBezier += .01;
		//bezier.x = moveBezier;
		//bezier.draw(gl);
		//gl.glPopMatrix();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}		

	private synchronized void addIce() {
		while (true){
			Queue<LocationValuePair> add = backend.getIceToAdd();
			if (add != null){
				LocationValuePair lvp;
				while ((lvp = add.poll()) != null && ice.size() < 10){
					Vec3 loc = lvp.getLocation();
					float size = lvp.getValue()/200;
					TriangleOrigami t = new TriangleOrigami(new Vec3(loc.x, 0, loc.z), new Vec3(loc.x + size, 2 * size, loc.z + size), new Vec3(loc.x - size, 2 * size, loc.z - size), new Random());
					t.setId(loc);
					ice.add(t);
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void removeIce() {
		while(true){
			Queue<Vec3> remove = backend.getIceToRemove();
			Vec3 id;
			while ((id = remove.poll()) != null){
				for (int i = 0; i < ice.size(); i++){
					if (((TriangleOrigami) ice.get(i)).getId().equals(id)){
						ice.remove(i);
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity();//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 600.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		camera.setMoveForward(true);
		return true;
	}
	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		camera.setMoveForward(false);
		return false;
	}
	
	public void onMapMoveForward(){
		backend.updateAvatarLocation(Backend.MOVE_FORWARD);
		for (int i = NUMMAPIMAGES - 1; i > NUMMAPIMAGES - MAPCOLUMNS - 1; i--){
			mapgroup.get(i).z -= MAPROWS*MAPHEIGHT;
		}
		for (int i = NUMMAPIMAGES - 1; i > MAPCOLUMNS - 1; i--){
			Plane temp = (Plane) mapgroup.get(i - MAPCOLUMNS);
			mapgroup.set((i - MAPCOLUMNS), mapgroup.get(i));
			mapgroup.set(i, temp);
		}
		for (int i = 0; i < MAPCOLUMNS; i++){
			updateMapTextures(backend.getImagePath() + "/" + i + backend.getImageName(i), i);
		}
		sky.z -= MAPHEIGHT;
		mapMoveBackward -= MAPHEIGHT;
		updateTexture = true;
	}
	
	public void onMapMoveBackward(){
		backend.updateAvatarLocation(Backend.MOVE_BACKWARD);
		for (int i = 0; i < MAPCOLUMNS; i++){
			mapgroup.get(i).z += MAPROWS*MAPHEIGHT;
		}
		for (int i = 0; i < NUMMAPIMAGES - MAPCOLUMNS; i++){
			Plane temp = (Plane) mapgroup.get(i + MAPCOLUMNS);
			mapgroup.set((i + MAPCOLUMNS), mapgroup.get(i));
			mapgroup.set(i, temp);
		}
		for (int i = NUMMAPIMAGES - 1; i > NUMMAPIMAGES - MAPCOLUMNS - 1; i--){
			updateMapTextures(backend.getImagePath() + "/" + i + backend.getImageName(i), i);
		}
		sky.z += MAPHEIGHT;
		mapMoveForward += MAPHEIGHT;
		updateTexture = true;
		
	}
	
	public void onMapMoveRight(){
		backend.updateAvatarLocation(Backend.MOVE_RIGHT);
		for (int i = 0; i < MAPROWS; i++){
			mapgroup.get(i * MAPCOLUMNS).x += MAPCOLUMNS*MAPWIDTH;
		}
		for (int i = 0; i < NUMMAPIMAGES; i++){
			if (i%MAPROWS == MAPROWS - 1)
				continue;
			Plane temp = (Plane) mapgroup.get(i + 1);
			mapgroup.set((i + 1), mapgroup.get(i));
			mapgroup.set(i, temp);
		}
		for (int i = MAPCOLUMNS - 1; i < NUMMAPIMAGES; i+= MAPCOLUMNS){
			updateMapTextures(backend.getImagePath() + "/"+ i + backend.getImageName(i), i);
		}
		sky.x += MAPWIDTH;
		mapMoveLeft += MAPWIDTH;
		updateTexture = true;
	}
	
	public void onMapMoveLeft(){
		backend.updateAvatarLocation(Backend.MOVE_LEFT);
		for (int i = MAPROWS; i > 0; i--){
			mapgroup.get(i*MAPCOLUMNS - 1).x -= MAPCOLUMNS*MAPWIDTH;
		}
		for (int i = mapgroup.size(); i > 0; i--){
			if (i%MAPROWS == 0)
				continue;
			Plane temp = (Plane) mapgroup.get(i - 1);
			mapgroup.set((i - 1), mapgroup.get(i));
			mapgroup.set(i, temp);
		}
		for (int i = 0; i < NUMMAPIMAGES - MAPCOLUMNS + 1; i+=MAPCOLUMNS){
			updateMapTextures(backend.getImagePath() + "/" + i + backend.getImageName(i), i);
		}
		sky.x -= MAPWIDTH;
		mapMoveRight -= MAPWIDTH;
		updateTexture = true;
	}
	
	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			if(gravity == null)
			{
				gravity = new float[3];
				gravity[0] = event.values[0];
				gravity[1] = event.values[1];
				gravity[2] = event.values[2];
			}
			else
			{
				gravity[0] = (float) (0.8f * gravity[0] + 0.2f * event.values[0]);
				gravity[1] = (float) (0.8f * gravity[1] + 0.2f * event.values[1]);
				gravity[2] = (float) (0.8f * gravity[2] + 0.2f * event.values[2]);
			}
			
			double scale = Math.sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]);
			gravity[0] /= scale;
			gravity[1] /= scale;
			gravity[2] /= scale;
			
			camera.setRotatorP("norm");
			if(gravity[0] > SENSITIVITY && gravity[2] < SENSITIVITY)
				camera.setRotatorP("up");
			else if(gravity[0] < SENSITIVITY && gravity[2] > SENSITIVITY)
				camera.setRotatorP("down");
			
			camera.setRotatorR("norm");
			if(gravity[1] < -0.3)
				camera.setRotatorR("left");
			else if(gravity[1] > 0.3)
				camera.setRotatorR("right");
		}
		
		return;
	}
	
	private class IceThread implements Runnable{

		@Override
		public void run() {
			while (true){
				Vec3 userLocation = camera.getEye();
				for (int lat = -50; lat < 50; lat++){
					for (int lon = -50; lon < 50; lon++){
						Float iceValue = iceData.get(new Vec3(userLocation.x + lat, 0, userLocation.z + lon));
						if (iceValue != null){
							
						}
					}
				}
				
			}
			
		}
		
	}
	
}