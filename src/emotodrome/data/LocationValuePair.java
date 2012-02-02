package emotodrome.data;

import emotodrome.mesh.Vec3;

public class LocationValuePair {
	private Vec3 location;
	private float value;
	
	public LocationValuePair(Vec3 location, float value){
		this.location = location;
		this.value = value;
	}
	
	public Vec3 getLocation() {
		return location;
	}
	public float getValue() {
		return value;
	}
}
