package emotodrome.mesh;

import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
	private ArrayList<Mesh> children = new ArrayList<Mesh>();
	
	@Override
	public void draw(GL10 gl) {
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);
		
		int size = children.size();
		for( int i = 0; i < size; i++){
			gl.glPushMatrix();
			children.get(i).draw(gl);
			gl.glPopMatrix();
		}
	}

	/**
	 * @param location
	 * @param object
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	public void add(int location, Mesh object) {
		children.add(location, object);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(Mesh object) {
		return children.add(object);
	}

	public Mesh set(int location, Mesh object) {
		return children.set(location, object);
	}
	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		children.clear();
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#get(int)
	 */
	public Mesh get(int location) {
		return children.get(location);
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public Mesh remove(int location) {
		return children.remove(location);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return children.remove(object);
	}

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size() {
		return children.size();
	}
	
	
}
