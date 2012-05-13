package emotodrome.mesh;

import javax.microedition.khronos.opengles.GL10;

public class MapTile extends Plane {

	private float westLon;
	private float eastLon;
	private float southLat;
	private float northLat;
	private float width;
	private float height;
	private Vec3 center;
	private Vec3 glToDegreeRatio;
	private Group ice;
	private Group users;
	private boolean[] visible_users;
	
	public MapTile(float width, float height){
		super(width, height);
		this.width = width;
		this.height = height;
		center = new Vec3();
		glToDegreeRatio = new Vec3(1, 1, 1);
		this.setWestLon(0);
		this.setEastLon(0);
		this.setSouthLat(0);
		this.setNorthLat(0);
		visible_users = new boolean[1000];
		ice = new Group();
		users = new Group();
	}
	
	public void addIce(Mesh m){
		ice.add(m);
	}
	
	public void removeIce(Mesh m){
		ice.remove(m);
	}
	
	public void addMarker(Mesh m){
		users.add(m);
	}
	
	public void removeMarker(Mesh m){
		users.remove(m);
	}
	
	public Group getIce(){
		return ice;
	}
	
	public void clearIce(Mesh m){
		ice.clear();
		users.clear();
	}
	
	public void draw(GL10 gl){
		gl.glPushMatrix();
		super.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		ice.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		users.draw(gl);
		gl.glPopMatrix();
	}

	public float getWestLon() {
		return westLon;
	}

	public void setWestLon(float westLon) {
		this.westLon = westLon + 180;
		center.x = (this.westLon + this.eastLon)/2;
		glToDegreeRatio.x = Math.abs(this.westLon - this.eastLon)/width;
	}

	public float getEastLon() {
		return eastLon;
	}

	public void setEastLon(float eastLon) {
		this.eastLon = eastLon + 180;
		center.x = (this.eastLon + this.westLon)/2;
		glToDegreeRatio.x = Math.abs(this.westLon - this.eastLon)/width;
	}

	public float getSouthLat() {
		return southLat;
	}

	public void setSouthLat(float southLat) {
		this.southLat = southLat;
		center.z = (this.southLat + this.northLat)/2;
		glToDegreeRatio.z = Math.abs(this.southLat - this.northLat)/height;
	}

	public float getNorthLat() {
		return northLat;
	}

	public void setNorthLat(float northLat) {
		this.northLat = northLat;
		center.z = (this.southLat + this.northLat)/2;
		glToDegreeRatio.z = Math.abs(this.southLat - this.northLat)/height;
	}

	public Vec3 getCenter() {
		return center;
	}
	
	public Vec3 getRatio(){
		return glToDegreeRatio;
		
	}
	
}
