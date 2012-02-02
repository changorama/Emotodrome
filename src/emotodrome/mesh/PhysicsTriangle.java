package emotodrome.mesh;

import javax.microedition.khronos.opengles.GL10;

/**
 * This is the very basic building block of shapes, allowing us to easily handle
 * them from a Vec3-based perspective without having to worry about the 
 * complexities of either OpenGL or Mesh.
 */
public class PhysicsTriangle extends Mesh implements Cloneable
{
	public Physics.PhysicsPoint p0, p1, p2;
	
	public PhysicsTriangle(Physics.PhysicsPoint v1, Physics.PhysicsPoint v2, Physics.PhysicsPoint v3)
	{
		super();
		
		p0 = new Physics.PhysicsPoint(new Vec3(v1.pos), new Vec3(v1.velocity), v1.mass);
		p1 = new Physics.PhysicsPoint(new Vec3(v2.pos), new Vec3(v2.velocity), v2.mass);
		p2 = new Physics.PhysicsPoint(new Vec3(v3.pos), new Vec3(v3.velocity), v3.mass);
	}
	
	public PhysicsTriangle(Vec3 v1, Vec3 v2, Vec3 v3)
	{
		super();
		
		p0 = new Physics.PhysicsPoint(v1, null, 1);
		p1 = new Physics.PhysicsPoint(v2, null, 1);
		p2 = new Physics.PhysicsPoint(v3, null, 1);
	}
	
	public void draw(GL10 gl)
	{
        float vertices[] = { p0.pos.x, p0.pos.y, p0.pos.z, //0
        					 p1.pos.x, p1.pos.y, p1.pos.z, //1
        					 p2.pos.x, p2.pos.y, p2.pos.z, //2
        					 p0.pos.x, p0.pos.y, p0.pos.z, //3 = 0
        					 p1.pos.x, p1.pos.y, p1.pos.z, //4 = 1
        					 p2.pos.x, p2.pos.y, p2.pos.z }; //5 = 2
        short indices[] = { 0, 1, 2, 
        					5, 4, 3 };
        
        float vertnormals[] = new float[18];
        
        Vec3 norm = p0.pos.sub(p1.pos).normalize().cross(p2.pos.sub(p1.pos).normalize()).normalize().negate();
        vertnormals[0] = norm.x;
        vertnormals[1] = norm.y;
        vertnormals[2] = norm.z;
        vertnormals[3] = norm.x;
        vertnormals[4] = norm.y;
        vertnormals[5] = norm.z;
        vertnormals[6] = norm.x;
        vertnormals[7] = norm.y;
        vertnormals[8] = norm.z;
        norm.setToNegate();
        vertnormals[9] = norm.x;
        vertnormals[10] = norm.y;
        vertnormals[11] = norm.z;
        vertnormals[12] = norm.x;
        vertnormals[13] = norm.y;
        vertnormals[14] = norm.z;
        vertnormals[15] = norm.x;
        vertnormals[16] = norm.y;
        vertnormals[17] = norm.z;
        
        setVertices(vertices);
        setNormals(vertnormals);
        setIndices(indices);
    
		gl.glEnable(GL10.GL_CULL_FACE);
        super.draw(gl);
        gl.glDisable(GL10.GL_CULL_FACE);
	}
	
	public Object clone()
	{
		return new PhysicsTriangle(p0, p1, p2);
	}
}
