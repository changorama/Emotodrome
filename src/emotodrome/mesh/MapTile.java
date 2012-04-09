package emotodrome.mesh;

public class MapTile extends Plane {

	private float westLat;
	private float eastLat;
	private float southLon;
	private float northLon;
	
	public MapTile(float width, float height, float westLat, float eastLat, float southLon, float northLon){
		super(width, height);
		this.westLat = westLat;
		this.eastLat = eastLat;
		this.southLon = southLon;
		this.northLon = northLon;
	}
}
