package emotodrome.mesh;

import javax.microedition.khronos.opengles.GL10;

public class MapTile extends Plane {

	private float westLat;
	private float eastLat;
	private float southLon;
	private float northLon;
	private Group ice;
	
	public MapTile(float width, float height){
		super(width, height);
		this.setWestLat(0);
		this.setEastLat(0);
		this.setSouthLon(0);
		this.setNorthLon(0);
		ice = new Group();
	}

	public float getWestLat() {
		return westLat;
	}

	public void setWestLat(float westLat) {
		this.westLat = westLat;
	}

	public float getEastLat() {
		return eastLat;
	}

	public void setEastLat(float eastLat) {
		this.eastLat = eastLat;
	}

	public float getSouthLon() {
		return southLon;
	}

	public void setSouthLon(float southLon) {
		this.southLon = southLon;
	}

	public float getNorthLon() {
		return northLon;
	}

	public void setNorthLon(float northLon) {
		this.northLon = northLon;
	}
	
	public void addIce(Mesh m){
		ice.add(m);
	}
	
	public void removeIce(Mesh m){
		ice.remove(m);
	}
	
	public void clearIce(Mesh m){
		ice.clear();
	}
	
	public void draw(GL10 gl){
		super.draw(gl);
		ice.draw(gl);
	}
}
