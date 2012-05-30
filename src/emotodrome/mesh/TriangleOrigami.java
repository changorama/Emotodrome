package emotodrome.mesh;
import java.util.*;

import javax.microedition.khronos.opengles.GL10;

public class TriangleOrigami extends Mesh implements Gadget
{
	public static final long TIME = 1000;
	public static final int FOLDTIME = 2, GROWTIME = 2;
	public static final float FOLDSPEED = 0.2f;
	
	private Vec3 id;
	private Stack<PhysicsTriangle> stack;
	private Vec3 loc, dir;
	private Physics.AnchoredBone bone;
	private long lastTime;
	private float newLength, oldLength;
	private Random rand;
	private int state = 0, lastEnd;
	private final int MAX_TRIANGLES = 30;
	private int num_triangles = 0;
							/*
							* State 0 means we haven't finished constructing the object yet.
							* State 1 means we are cloning a triangle and choosing a fold vector and bone length.
							* State 2 means we are folding the triangle.
							* State 3 means we are growing or shrinking the triangle - DOES NOT DISCONNECT ENDPOINTS.
							* lastEnd = 0, 1, 2 corresponds to which point was last chosen as the bone's endpoint
							*/
	
	public TriangleOrigami(Vec3 v1, Vec3 v2, Vec3 v3, Random r)
	{
		stack = new Stack<PhysicsTriangle>();
		stack.push(new PhysicsTriangle(v1, v2, v3));
		loc = new Vec3();
		dir = new Vec3();
		bone = new Physics.AnchoredBone();
		rand = r;
		state = 1;
		lastEnd = (int) (rand.nextFloat() * 3);
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				update();
			}
		}).start();
	}
	
	private void update() {
		while(num_triangles < MAX_TRIANGLES){
			switch(state)
			{
			case 0:
				throw new IllegalStateException("TriangleOrigami object not properly initialized.");
			case 1:
				num_triangles++;
				stack.push((PhysicsTriangle) stack.peek().clone());
				Vec3 pin1 = new Vec3(), pin2 = new Vec3();
				
				lastEnd = (lastEnd + ((int) (rand.nextFloat() * 2) + 1)) % 3;
				switch(lastEnd)
				{
				case 0:
					bone.end = stack.peek().p0.pos;
					pin1.set(stack.peek().p1.pos);
					pin2.set(stack.peek().p2.pos);
					break;
				case 1:
					bone.end = stack.peek().p1.pos;
					pin1.set(stack.peek().p2.pos);
					pin2.set(stack.peek().p0.pos);
					break;
				case 2:
					bone.end = stack.peek().p2.pos;
					pin1.set(stack.peek().p0.pos);
					pin2.set(stack.peek().p1.pos);
					break;
				}
				loc.set(bone.end);
				
				// (A . B) / (B . B) * B
				bone.anchor = pin2.add(pin1.sub(pin2).scale(pin1.sub(pin2).dot(loc.sub(pin2)) / pin1.sub(pin2).dot(pin1.sub(pin2))));
				bone.length = (float) bone.anchor.distance(bone.end);
				
				newLength = bone.length * (rand.nextFloat() + 0.5f); //MAGIC NUMBER ALERT
				oldLength = bone.length;
				
				// Might want to refine this for less furious folding.  Consider choosing near-orthogonal to the bone.
				//DEBUG
				/*dir.x = rand.nextFloat() - 0.5f;
				dir.y = rand.nextFloat() - 0.5f;
				dir.z = rand.nextFloat() - 0.5f;
				dir.normalize();*/
				dir.set(stack.peek().p1.pos.sub(stack.peek().p0.pos).cross(stack.peek().p2.pos.sub(stack.peek().p0.pos)).normalize());
				if(dir.y < 0)
					dir.setToNegate();
				
				lastTime = System.currentTimeMillis();
				state = 2;
			case 2:
				if(System.currentTimeMillis() - lastTime >= TIME * FOLDTIME)
				{
					lastTime = System.currentTimeMillis();
					state = 3;
				}
				else
				{
					loc.setToAdd(dir.scale(FOLDSPEED));
					bone.end.set(loc);
					bone.end.set(Physics.bonePull(bone));
				}
				break;
			case 3:
				if(System.currentTimeMillis() - lastTime >= TIME * GROWTIME)
					state = 1;
				else
				{
					bone.length = (oldLength * (1f - (System.currentTimeMillis() - lastTime) / (TIME * GROWTIME))) + 
							(newLength * ((System.currentTimeMillis() - lastTime) / (TIME * GROWTIME)));
					bone.end.set(Physics.bonePull(bone));
				}
				break;
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void draw(GL10 gl)
	{
		for(Object t :stack.toArray())
			((PhysicsTriangle) t).draw(gl);
		return;
	}
	
	public void setLocation(Vec3 loc){
		this.loc = loc;
	}

	public Vec3 getId() {
		return id;
	}

	public void setId(Vec3 id) {
		this.id = id;
	}

}
