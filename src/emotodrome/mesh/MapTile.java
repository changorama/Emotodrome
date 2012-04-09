package emotodrome.mesh;

public class MapTile extends Plane {

	private float westLat;
	private float eastLat;
	private float southLon;
	private float northLon;
	
	public MapTile(float width, float height){
		super(width, height);
		this.setWestLat(0);
		this.setEastLat(0);
		this.setSouthLon(0);
		this.setNorthLon(0);
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
	
}
