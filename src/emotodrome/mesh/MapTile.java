package emotodrome.mesh;

import javax.microedition.khronos.opengles.GL10;

public class MapTile extends Plane {

	private float westLon;
	private float eastLon;
	private float southLat;
	private float northLat;
	private Group ice;
	
	public MapTile(float width, float height){
		super(width, height);
		this.setWestLon(0);
		this.setEastLon(0);
		this.setSouthLat(0);
		this.setNorthLat(0);
		ice = new Group();
	}
	
	public void addIce(Mesh m){
		ice.add(m);
	}
	
	public void removeIce(Mesh m){
		ice.remove(m);
	}
	
	public Group getIce(){
		return ice;
	}
	
	public void clearIce(Mesh m){
		ice.clear();
	}
	
	public void draw(GL10 gl){
		gl.glPushMatrix();
		super.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		ice.draw(gl);
		gl.glPopMatrix();
	}

	public float getWestLon() {
		return westLon;
	}

	public void setWestLon(float westLon) {
		this.westLon = westLon + 180;
	}

	public float getEastLon() {
		return eastLon;
	}

	public void setEastLon(float eastLon) {
		this.eastLon = eastLon + 180;
	}

	public float getSouthLat() {
		return southLat;
	}

	public void setSouthLat(float southLat) {
		this.southLat = southLat;
	}

	public float getNorthLat() {
		return northLat;
	}

	public void setNorthLat(float northLat) {
		this.northLat = northLat;
	}
}
