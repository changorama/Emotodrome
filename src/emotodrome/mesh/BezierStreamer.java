package emotodrome.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class BezierStreamer 
{
	public static final float LENGTH = 1, SPRING_CONSTANT = 0.5f, MASS = 1, SPEED = 1;

    public Vec3 grav;
    private Physics.PhysicsPoint[] controls;
    private Physics.AnchoredSpring[] springs;
    private int tesselation;
    
    //DEBUG
    private Physics.RopeTie[] ropes;

    public BezierStreamer(float x, float y, float z, float upx, float upy, float upz, int tesselation)
    {
		grav = new Vec3(upx, upy, upz);
		grav.normalize();
		this.tesselation = tesselation;
	
		this.controls = new Physics.PhysicsPoint[4];
		controls[0] = new Physics.PhysicsPoint(new Vec3(x, y, z), null, 1);
		double uplength = Math.sqrt(upx * upx + upy * upy + upz * upz);
		for(int i = 1; i < 4; i++)
		{
		    controls[i] = new Physics.PhysicsPoint(new Vec3(x + upx * LENGTH * i, y + upy * LENGTH * i, z + upz * LENGTH * i), null, 1);
		}
		
		springs = new Physics.AnchoredSpring[3];
		for(int i = 0; i < 3; i++)
		{
			springs[i] = new Physics.AnchoredSpring();
			springs[i].k = SPRING_CONSTANT;
			springs[i].equilibrium = (float) uplength;
		}
		
		//DEBUG
		ropes = new Physics.RopeTie[3];
		for(int i = 0; i < 3; i++)
		{
			ropes[i] = new Physics.RopeTie();
			ropes[i].length = (float) uplength;
		}
    }
   
    public void moveBy(Vec3 v)
    {
    	controls[0].pos.setToAdd(v);
    }

    public void draw(GL10 gl)
	{	
		// Compute spring forces
		for(int i = 0; i < 3; i++)
		{
			//DEBUG
			/*
			springs[i].anchor = controls[i].pos;
			springs[i].end = controls[i + 1].pos;
			Physics.acceleratePoint(controls[i + 1], Physics.anchoredSpringForce(springs[i]));
			Physics.translatePhysicsPoint(controls[i + 1], 1f / 32);
			*/
			Physics.translatePhysicsPoint(controls[i + 1], 1f / 32);
			ropes[i].anchor = controls[i].pos;
			ropes[i].end = controls[i + 1].pos;
			controls[i + 1].pos.set(Physics.ropePull(ropes[i]));
		}
	
		// Draw the Bezier curve
		
		FloatBuffer verticesBuffer;
		float[] verts = {0, 0, 0, controls[0].pos.x, controls[0].pos.y, controls[0].pos.z};
		
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
		    verts[3] = (float) (Math.pow(1 - t, 3) * controls[0].pos.x + 
			3 * Math.pow(1 - t, 2) * t * controls[1].pos.x + 
			3 * (1 - t) * t * t * controls[2].pos.x + 
			t * t * t * controls[3].pos.x);
		    verts[4] = (float) (Math.pow(1 - t, 3) * controls[0].pos.y + 
			3 * Math.pow(1 - t, 2) * t * controls[1].pos.y + 
			3 * (1 - t) * t * t * controls[2].pos.y + 
			t * t * t * controls[3].pos.y);
		    verts[5] = (float) (Math.pow(1 - t, 3) * controls[0].pos.z + 
			3 * Math.pow(1 - t, 2) * t * controls[1].pos.z + 
			3 * (1 - t) * t * t * controls[2].pos.z + 
			t * t * t * controls[3].pos.z);
		    
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
