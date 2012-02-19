package emotodrome.mesh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Mesh implements Cloneable{
	// Our vertex buffer.
	protected FloatBuffer verticesBuffer = null;

	// Our index buffer.
	protected ShortBuffer indicesBuffer = null;
	
	protected FloatBuffer normalBuffer = null;
	
	protected FloatBuffer textureBuffer = null;

	// The number of indices.
	protected int numOfIndices = -1;

	// Flat Color
	private float[] rgba = new float[] { 1.0f, 1.0f, 0.0f, .5f };
	
	private int[] textures = new int[1];
	private int[] textures2 = new int[1];
	
	private String textureLocation = null;

	private boolean switchBuff = true;
	// Smooth Colors
	private FloatBuffer colorBuffer = null;

	// Translate params.
	public float x = 0;

	public float y = 0;

	public float z = 0;

	// Rotate params.
	public float rx = 0;

	public float ry = 0;

	public float rz = 0;
	
	public float scalex = 1;
	
	public float scaley = 1;
	
	public float scalez = 1;
	
	
	public void draw(GL10 gl) {
		//Bind the texture according to the set texture filter
		if (switchBuff){
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);			
		}
		else {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures2[0]);
		}
		//gl.glEnable(GL10.GL_COLOR_MATERIAL);
		//Set the face rotation
		//gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		//gl.glCullFace(GL10.GL_BACK);
		//gl.glEnable(GL10.GL_DEPTH_TEST);
		//Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glFrontFace(GL10.GL_CCW);
		
		//Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		if (textureBuffer != null){
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		}
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		//gl.glColor4f(1f, 1f, 0f, 1f);
		
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);
		gl.glScalef(scalex, scaley, scalez);
		
		//gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
		//gl.glColor4f(1f, 1f, 0f, 1f);
		FloatBuffer mcolor = FloatBuffer.allocate(4);
		FloatBuffer.wrap(new float[]{ 1.0f, 1.0f, 0.0f, 0.5f });
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mcolor);
		//Draw the vertices as triangles, based on the Index Buffer information
		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (textureBuffer!= null)
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, normalBuffer);
//		gl.glDrawElements(GL10.GL_LINES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}
	
	protected void setNormals(float[] normals) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(normals.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		normalBuffer = vbb.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);
	}
	
	protected void setTexture(float[] texture){
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
	}

	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}

	protected void setColor(float red, float green, float blue, float alpha) {
		// Setting the flat color.
		rgba[0] = red;
		rgba[1] = green;
		rgba[2] = blue;
		rgba[3] = alpha;
	}

	protected void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		colorBuffer = cbb.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);
	}
	
	public void loadDownloadedTexture(GL10 gl, Context context) {
		//Get the texture from the Android resource directory
		gl.glDisable(GL10.GL_TEXTURE_2D);
		//if (textures.length > 0)
			//gl.glDeleteTextures(textures.length, textures, 0);
		
		//		Uri u = Uri.fromFile(new File(s));
		Bitmap bitmap = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inScaled = false;
		//BitmapFactory is an Android graphics utility for images
		bitmap = BitmapFactory.decodeFile(textureLocation, opt);
		//Generate there texture pointer
		if (switchBuff){
		//Create Nearest Filtered Texture and bind it to texture 0
			gl.glGenTextures(1, textures2, 0);
			System.out.println("Generating texture: " + gl.glGetError());
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures2[0]);
			switchBuff = false;
		}
		else 
		{
			gl.glGenTextures(1, textures, 0);
			System.out.println("Generating texture: " + gl.glGetError());
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			switchBuff = true;
		}
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);		
		
		//Clean up
		bitmap.recycle();
		gl.glEnable(GL10.GL_TEXTURE_2D);
	}
	
	public void loadGLTexture(GL10 gl, Context context, int id) {
		//Get the texture from the Android resource directory
		
		InputStream is = context.getResources().openRawResource(id);

		Bitmap bitmap = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			//Always clear and close
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		//Generate there texture pointer
		gl.glGenTextures(1, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		//Clean up
		bitmap.recycle();
	}
	
	
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setTextureLocation(String textureLocation) {
		this.textureLocation = textureLocation;
	}

	public String getTextureLocation() {
		return textureLocation;
	}
	
	public void setPosition(Vec3 p){
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
}
