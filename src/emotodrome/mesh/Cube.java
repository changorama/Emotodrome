package emotodrome.mesh;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import emotodrome.project.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * This class is an object representation of 
 * a Cube0 containing the vertex information,
 * texture coordinates, the vertex indices
 * and drawing functionality, which is called 
 * by the renderer.
 *  
 * @author Savas Ziplies (nea/INsanityDesign)
 */
public class Cube extends Mesh implements Cloneable{

	/** The buffer holding the texture coordinates */

	/** Our texture pointer */
	private int[] textures = new int[3];


	/** The initial texture coordinates (u, v) */	
//	private float texture[] = {
//						//Mapping coordinates for the vertices
//						0.0f, 0.0f, 
//						0.0f, 1.0f, 
//						1.0f, 0.0f, 
//						1.0f, 1.0f,
//			
//						0.0f, 0.0f,
//						0.0f, 1.0f, 
//						1.0f, 0.0f,
//						1.0f, 1.0f,
//			
//						0.0f, 0.0f, 
//						0.0f, 1.0f, 
//						1.0f, 0.0f, 
//						1.0f, 1.0f,
//			
//						0.0f, 0.0f, 
//						0.0f, 1.0f, 
//						1.0f, 0.0f, 
//						1.0f, 1.0f,
//			
//						0.0f, 0.0f, 
//						0.0f, 1.0f, 
//						1.0f, 0.0f, 
//						1.0f, 1.0f,
//			
//						0.0f, 0.0f, 
//						0.0f, 1.0f, 
//						1.0f, 0.0f, 
//						1.0f, 1.0f, 
//									};

	/** The initial indices definition 
	private byte indices[] = {
						// Faces definition
						0, 1, 3, 0, 3, 2, 		// Face front
						4, 5, 7, 4, 7, 6, 		// Face right
						8, 9, 11, 8, 11, 10, 	// ...
						12, 13, 15, 12, 15, 14, 
						16, 17, 19, 16, 19, 18, 
						20, 21, 23, 20, 23, 22, 
												};

*/
	/**
	 * The Cube0 constructor.
	 * 
	 * Initiate the buffers.
	 */
	public Cube(float width, float height, float depth) {
	        width  /= 2;
	        height /= 2;
	        depth  /= 2;
	 
	        float vertices[] = { -width, -height, -depth, // 0
	                              width, -height, -depth, // 1
	                              width,  height, -depth, // 2
	                             -width,  height, -depth, // 3
	                             -width, -height,  depth, // 4
	                              width, -height,  depth, // 5
	                              width,  height,  depth, // 6
	                             -width,  height,  depth, // 7
		
	        };
	        
	        short indices[] = { 0, 4, 5,		//defines which vertices to connect to form triangles
	                0, 5, 1,
	                1, 5, 6,
	                1, 6, 2,
	                2, 6, 7,
	                2, 7, 3,
	                3, 7, 4,
	                3, 4, 0,
	                4, 7, 6,
	                4, 6, 5,
	                3, 0, 1,
	                3, 1, 2, };
	        
	        float vertnormals[] = new float[24]; //8 vertices * 3d vertex space
	        
	        short count[] = new short[8];
	        
	        for(int i = 0; i < indices.length; i++){
	        	//calc points that make up this triangle
	        	float[] a = new float[]{vertices[3*indices[i]], vertices[3*indices[i]+1], vertices[3*indices[i]+2]};
	        	i++;
	        	float[] b = new float[]{vertices[3*indices[i]], vertices[3*indices[i]+1], vertices[3*indices[i]+2]};
	        	i++;
	        	float[] c = new float[]{vertices[3*indices[i]], vertices[3*indices[i]+1], vertices[3*indices[i]+2]};
	        	
	        	float[] vec = calcSurfaceNormal(a, b, c);
	        	for (int j = i - 2; j <= i; j++){
	        		//x, y, and z values of each vert are the average of the normals at adjacent faces, so count the number of faces
	        		//they are adjacent to
	        		count[indices[j]]+=1;
	        		vertnormals[3*indices[j]] += -vec[0];
	        		vertnormals[3*indices[j]+1] += -vec[1];
	        		vertnormals[3*indices[j]+2] += -vec[2];
	        	} 	
	        }
	    	for (int i = 0; i < count.length; i++){
	    		float x = vertnormals[3*i]/count[i];
	    		float y = vertnormals[3*i+1]/count[i];
	    		float z = vertnormals[3*i+2]/count[i];
	    		float len = calclen(x, y, z);
	    		vertnormals[3*i] = x/len;
	    		vertnormals[3*i+1] = y/len;
	    		vertnormals[3*i+2] = z/len;
	    		
	    	}
	        
	    setVertices(vertices);
	    

		//
		//setTexture(texture);

        setNormals(vertnormals);
        setIndices(indices);

		
	}

	float[] calcSurfaceNormal (float[] v1, float[] v2, float[] v3)
	// gets the normal of a face
	
	{
		   float norm[] = {0, 0, 0};
		   
		   norm[0] = norm[0] + (v1[1] - v2[1]) * (v1[2] + v2[2]);
		   norm[1] = norm[1] + (v1[2] - v2[2]) * (v1[0] + v2[0]);
		   norm[2] = norm[2] + (v1[0] - v2[0]) * (v1[1] + v2[1]);
		   
		   norm[0] = norm[0] + (v2[1] - v3[1]) * (v2[2] + v3[2]);
		   norm[1] = norm[1] + (v2[2] - v3[2]) * (v2[0] + v3[0]);
		   norm[2] = norm[2] + (v2[0] - v3[0]) * (v2[1] + v3[1]);
		   
		   norm[0] = norm[0] + (v3[1] - v1[1]) * (v3[2] + v1[2]);
		   norm[1] = norm[1] + (v3[2] - v1[2]) * (v3[0] + v1[0]);
		   norm[2] = norm[2] + (v3[0] - v1[0]) * (v3[1] + v1[1]);
	
		   return norm;
		   
	}
	
	float calclen(float x, float y, float z){
	    // calculate the length of the vector
	    float len = (float)(Math.sqrt((x * x) + (y * y) + (z * z)));

	    // avoid division by 0
	    if (len == 0.0f)
	        len = 1.0f;

	    return len;
		
	}
	
	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 * 
	 * @param gl - The GL Context
	 * @param filter - Which texture filter to be used
	 */

	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		//Get the texture from the Android resource directory
		InputStream is = context.getResources().openRawResource(R.drawable.sky1);
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
		gl.glGenTextures(3, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		/*
		 * This is a change to the original tutorial, as buildMipMap does not exist anymore
		 * in the Android SDK.
		 * 
		 * We check if the GL context is version 1.1 and generate MipMaps by flag.
		 * Otherwise we call our own buildMipMap implementation
		 */
		if(gl instanceof GL11) {
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
		//
		} else {
			buildMipmap(gl, bitmap);
		}		
		
		//Clean up
		bitmap.recycle();
	}
	
	/**
	 * Our own MipMap generation implementation.
	 * Scale the original bitmap down, always by factor two,
	 * and set it as new mipmap level.
	 * 
	 * Thanks to Mike Miller (with minor changes)!
	 * 
	 * @param gl - The GL Context
	 * @param bitmap - The bitmap to mipmap
	 */
	private void buildMipmap(GL10 gl, Bitmap bitmap) {
		//
		int level = 0;
		//
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		//
		while(height >= 1 || width >= 1) {
			//First of all, generate the texture from our bitmap and set it to the according level
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			
			//
			if(height == 1 || width == 1) {
				break;
			}

			//Increase the mipmap level
			level++;

			//
			height /= 2;
			width /= 2;
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			
			//Clean up
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}
	
	
	public Object clone(){
		return super.clone();
	}
}
