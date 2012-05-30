package emotodrome.mesh;

import java.util.Random;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

public class Pyrite extends Mesh
{
    private long time = 5000, last;
    public float distance;
    private Cube current;
    private final int MAX_CUBES = 15;
    private int num_cubes = 0;
    private float x, y, z, rotX, rotY, rotZ;
    private Stack<Cube> stack;
    private Random rand;
    
    public Pyrite(float width, float height, float depth, Random r){
    	stack = new Stack<Cube>();
    	current = new Cube(width, height, depth);
    	current.x = 0f;
    	current.y = 0f;
    	current.z = 0f;
    	current.rx = 0f;
    	current.ry = 0f;
    	current.rz = 0f;
    	distance = Math.min(width, height);
    	this.rand = r;
    	
    	postulate();
    }
    
    public Pyrite(float x, float y, float z, float rotX, float rotY, float rotZ, float width, float height, float depth, Random r)
    {
	stack = new Stack<Cube>();
	current = new Cube(width, height, depth);
	current.x = x;
	current.y = y;
	current.z = z;
	current.rx = rotX;
	current.ry = rotY;
	current.rz = rotZ;
	this.rand = r;
	
	postulate();
    }

    private void postulate()
    {
		float xx = rand.nextFloat() - 0.5f;
		float yy = rand.nextFloat();
		float zz = rand.nextFloat() - 0.5f;
		//float scale = rand.nextFloat();
		double len = Math.sqrt(xx * xx + yy * yy + zz * zz);
		xx *=  distance / len;
		yy *=  distance / len;
		zz *=  distance / len;
		x = current.x + xx;
		y = current.y + yy;
		z = current.z + zz;
		rotX = rand.nextFloat() * 180;
		rotY = rand.nextFloat() * 180;
		rotZ = rand.nextFloat() * 180;
		
		stack.push(current);
		current = (Cube) current.clone();
		last = System.currentTimeMillis();
		num_cubes++;
    }

    public void draw(GL10 gl)
    {
    	if(System.currentTimeMillis() - last >= time && num_cubes < MAX_CUBES)
    		postulate();

    	for(Object c : stack.toArray())
    	{
    		gl.glPushMatrix();
    		((Cube) c).draw(gl);
    		gl.glPopMatrix();
    	}
    	if (num_cubes < MAX_CUBES){
	    	float t = 1.0f * (System.currentTimeMillis() - last) / time;
	    	current.x = stack.peek().x * (1 - t) + t * x;
			current.y = stack.peek().y * (1 - t) + t * y;
			current.z = stack.peek().z * (1 - t) + t * z;
			current.rx = stack.peek().rx * (1 - t) + t * rotX;
			current.ry = stack.peek().ry * (1 - t) + t * rotY;
			current.rz = stack.peek().rz * (1 - t) + t * rotZ;
    	}
    	
		gl.glPushMatrix();
		current.draw(gl);
		gl.glPopMatrix();
		return;
    }
}