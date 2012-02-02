package emotodrome.mesh;

import java.nio.*;

import javax.microedition.khronos.opengles.GL10;

public class AnchoredBezier
{
    public static final float LENGTH = 1, ELASTICITY = 0.95f, MASS = 1, SPEED = 1;

    public float x, y, z, upx, upy, upz;
    private float[] controls; // The first of these is the anchor.
    private float[] inertia; // Inertia for P[1,3]
    private int tesselation;

   public AnchoredBezier(float x, float y, float z, float upx, float upy, float upz, int tesselation)
    {
		this.x = x;
		this.y = y;
		this.z = z;
		this.upx = upx;
		this.upy = upy;
		this.upz = upz;
		this.tesselation = tesselation;
	
		this.controls = new float[12];
		this.inertia = new float[12];
	
		controls[0] = x;
		controls[1] = y;
		controls[2] = z;
	
		double uplength = Math.sqrt(upx * upx + upy * upy + upz * upz);
		this.upx /= uplength;
		this.upy /= uplength;
		this.upz /= uplength;
	
		for(int i = 1; i < 4; i++)
		{
		    controls[i * 3] = x + upx * LENGTH * i;
		    controls[i * 3 + 1] = y + upy * LENGTH * i;
		    controls[i * 3 + 2] = z + upz * LENGTH * i;
		}
		for(int i = 0; i < 12; i++)
		    inertia[i] = 0;
    }

    public void draw(GL10 gl)
	    {
		if(controls[0] != this.x ||
		   controls[1] != this.y ||
		   controls[2] != this.z)
	        {
		    controls[0] = this.x;
		    controls[1] = this.y;
		    controls[2] = this.z;
		}
	
	    float[] forces = new float[12];
		float d1, d2, d3, y1, y2, y3;
		double len;
		
		// Compute spring forces
		for(int i = 0; i < 9; i += 3)
		{
		    d1 = controls[i] - controls[i + 3];
		    d2 = controls[i + 1] - controls[i + 4];
		    d3 = controls[i + 2] - controls[i + 5];
		    len = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
		    y1 = (float) (d1 - d1 / len * LENGTH);
	        y2 = (float) (d2 - d2 / len * LENGTH);
		    y3 = (float) (d3 - d3 / len * LENGTH);
		    forces[i] = y1;
		    forces[i + 1] = y2;
		    forces[i + 2] = y3;
		    if(i < 6)
		    {
			d1 = controls[i + 6] - controls[i + 3];
			d2 = controls[i + 7] - controls[i + 4];
			d3 = controls[i + 8] - controls[i + 5];
			len = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
			y1 = (float) (d1 - d1 / len * LENGTH);
			y2 = (float) (d2 - d2 / len * LENGTH);
			y3 = (float) (d3 - d3 / len * LENGTH);
			forces[i] += y1;
			forces[i + 1] += y2;
			forces[i + 2] += y3;
		    }
		    forces[i + 3] = (float) Math.sqrt(forces[i] * forces[i] + 
					      forces[i + 1] * forces[i + 1] + 
					      forces[i + 2] * forces[i + 2]);
		    forces[i + 0] /= (forces[i + 3] == 0 ? 1 : forces[i + 3]);
		    forces[i + 1] /= (forces[i + 3] == 0 ? 1 : forces[i + 3]);
		    forces[i + 2] /= (forces[i + 3] == 0 ? 1 : forces[i + 3]);
		}
	
		// Combine spring force with inertia
		for(int i = 0; i < 12; i += 4)
		{
		    inertia[i] = inertia[i] * inertia[i + 3] + forces[i] * forces[i + 3];
		    inertia[i + 1] = inertia[i + 1] * inertia[i + 3] + forces[i + 1] * forces[i + 3];
		    inertia[i + 2] = inertia[i + 2] * inertia[i + 3] + forces[i + 2] * forces[i + 3];
		    inertia[i + 3] = (float) Math.sqrt(inertia[i] * inertia[i] + 
					       inertia[i + 1] * inertia[i + 1] + 
					       inertia[i + 2] * inertia[i + 2]);
		    inertia[i + 0] /= (inertia[i + 3] == 0 ? 1 : forces[i + 3]);
		    inertia[i + 1] /= (inertia[i + 3] == 0 ? 1 : forces[i + 3]);
		    inertia[i + 2] /= (inertia[i + 3] == 0 ? 1 : forces[i + 3]);
		}
		
		//MOVE
		controls[3] += inertia[3] * inertia[0] * SPEED;
		controls[4] += inertia[3] * inertia[1] * SPEED;
		controls[5] += inertia[3] * inertia[2] * SPEED;
		controls[6] += inertia[7] * inertia[4] * SPEED;
		controls[7] += inertia[7] * inertia[5] * SPEED;
		controls[8] += inertia[7] * inertia[6] * SPEED;
		controls[9] += inertia[11] * inertia[8] * SPEED;
		controls[10] += inertia[11] * inertia[9] * SPEED;
		controls[11] += inertia[11] * inertia[10] * SPEED;
	
		// Dampen
		inertia[3] *= ELASTICITY;
		inertia[7] *= ELASTICITY;
		inertia[11] *= ELASTICITY;
	
		// Draw the Bezier curve
		
		FloatBuffer verticesBuffer;
		float[] verts = {0, 0, 0, controls[0], controls[1], controls[2]};
		
		short[] indices = {0, 1};
		ShortBuffer indicesBuffer;
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		int numOfIndices = indices.length;
		
		double t;
		
		for(int i = 0; i <= tesselation; i++)
		{
		    verts[0] = verts[3];
		    verts[1] = verts[4];
		    verts[2] = verts[5];
		    t = 1.0 * i / tesselation;
		    verts[3] = (float) (Math.pow(1 - t, 3) * controls[0] + 
			3 * Math.pow(1 - t, 2) * t * controls[3] + 
			3 * (1 - t) * t * t * controls[6] + 
			t * t * t * controls[9]);
		    verts[4] = (float) (Math.pow(1 - t, 3) * controls[1] + 
			3 * Math.pow(1 - t, 2) * t * controls[4] + 
			3 * (1 - t) * t * t * controls[7] + 
			t * t * t * controls[10]);
		    verts[5] = (float) (Math.pow(1 - t, 3) * controls[2] + 
			3 * Math.pow(1 - t, 2) * t * controls[5] + 
			3 * (1 - t) * t * t * controls[8] + 
			t * t * t * controls[11]);
		    
		    System.out.println("v0: " + verts[0] + " v1: " + verts[1] + " v2: " + verts[2] + " v3: " + verts[3] + " v4: " + verts[4] + " v5: " + verts[5]);
		    for (int j = 0; j < 12; j+=3){
		    	System.out.print("x"+j + ": " + controls[j] + " ");
		    	System.out.print("y"+j + ": " + controls[j+1] + " ");
		    	System.out.println("z"+j + ": " + controls[j+2]);
		    	
		    }
		    
			ByteBuffer vbb = ByteBuffer.allocateDirect(verts.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			verticesBuffer = vbb.asFloatBuffer();
			verticesBuffer.put(verts);
			verticesBuffer.position(0);
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
		    gl.glLineWidth(2.0f);
		    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		    gl.glDrawElements(GL10.GL_LINES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);
		    
		    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	
		//B(t) = (1 - t)^3 * P0 + 3 * (1 - t)^2 * t * P1 + 3 * (1 - t) * t^2 * P2 + t^3 * P3
		return;
    }
}