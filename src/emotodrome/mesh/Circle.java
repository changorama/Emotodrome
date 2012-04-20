package emotodrome.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle extends Mesh{
	
	private final int NUM_POINTS = 90;
	private float[] rgba;
	private float initRadius;
	private float scale;

    /**
     * @param outerRadius - the radius of the outer circle
     * @param innerRadius - the radius of the inner circle
     * 
     */

    public Circle(float outerRadius, float innerRadius, float[] rgba) {
    	float[] vertices = new float[2 * NUM_POINTS * 3];
    	short[] indices = new short[NUM_POINTS * 4 + 1];
    	float[] normals = new float[NUM_POINTS * 6];
    	//verticesBuffer = ByteBuffer.allocateDirect(2 * NUM_POINTS * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); 
    	for (int i = 0; i < NUM_POINTS * 6; i += 6) {
    		vertices[i] = (float) Math.cos(Math.PI * i * (360/NUM_POINTS) / 180) * outerRadius;
    	    vertices[i+1] = 0f;
    	    vertices[i+2] = (float) Math.sin(Math.PI * i * (360/NUM_POINTS) / 180) * outerRadius;
    		vertices[i+3] = (float) Math.cos(Math.PI * i * (360/NUM_POINTS) / 180) * innerRadius;
    	    vertices[i+4] = 0f;
    	    vertices[i+5] = (float) Math.sin(Math.PI * i * (360/NUM_POINTS)/ 180) * innerRadius;
    	    
    	    normals[i] = 0;
    	    normals[i+1] = 1;
    	    normals[i+2] = 0;
    	    normals[i+3] = 0;
    	    normals[i+4] = 1;
    	    normals[i+5] = 0;
//    		verticesBuffer.put(i, (float) Math.cos(Math.PI * i / 180) * outerRadius);
//    	    verticesBuffer.put(i+1, 0f);
//    	    verticesBuffer.put(i+2, (float) Math.sin(Math.PI * i / 180) * outerRadius);
//    		verticesBuffer.put(i+3, (float) Math.cos(Math.PI * i / 180) * innerRadius);
//    	    verticesBuffer.put(i+4, 0f);
//    	    verticesBuffer.put(i+5, (float) Math.sin(Math.PI * i / 180) * innerRadius);
    	}
    	short index = 1;
    	for (int i = 0; i < NUM_POINTS * 4 - 1; i+=3){
    		indices[i] = (short) (index - 1);
    		indices[i+1] = index;
    		indices[i+2] = ++index;
    	}
		//verticesBuffer.position(0);
		this.setVertices(vertices);
		this.setIndices(indices);
		this.setNormals(normals);
		this.setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
		this.rgba = rgba;
		this.initRadius = outerRadius;
    }

//    public void draw(GL10 gl) {
//		gl.glTranslatef(x, y, z);
//		gl.glRotatef(rx, 1, 0, 0);
//		gl.glRotatef(ry, 0, 1, 0);
//		gl.glRotatef(rz, 0, 0, 1);
//		gl.glScalef(scale, 1, scale);
//		
//    	gl.glDisable(GL10.GL_CULL_FACE);
//    	gl.glDisable(GL10.GL_TEXTURE_2D);
//    	
//    	gl.glLineWidth(lineWidth);
//    	gl.glColor4f(rgba[0], rgba[1], rgba[2],rgba[3]);
//    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
//        //gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
//        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, NUM_POINTS * 6);
//        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//        gl.glLineWidth(1);
//    	
//        gl.glEnable(GL10.GL_TEXTURE_2D);
//        gl.glEnable(GL10.GL_CULL_FACE);
//    }
    
    public void setRadius(float radius){
    	scalex = radius/initRadius;
    	scalez = scalex;
    }

}
