package emotodrome.user;

import javax.microedition.khronos.opengles.GL10;

import emotodrome.mesh.Mesh;
import emotodrome.mesh.Vec3;

/**
 * Class to hold the user's position and the avatar that will represent that user
 */
public class User {

	private Vec3 userVector;
	private Vec3 previousUserVector;
	private Mesh userAvatar;
	private float lat;
	private float lon;
	
	public User(Vec3 userVector){
		this.userVector = userVector;
		this.previousUserVector = userVector;
	}
	
	public Mesh setUserAvatar(Mesh userAvatar){
		this.userAvatar = userAvatar;
		return this.userAvatar;
	}
	
	public Mesh getUserAvatar(){
		return userAvatar;
	}
	
	public void setUserVector(Vec3 userVector){
		previousUserVector = this.userVector;
		this.userVector = userVector;
	}
	
	public Vec3 getUserVector(){
		return userVector;
	}
	
	public void draw(GL10 gl){
		userAvatar.draw(gl);
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public Vec3 getMoveAmount(Vec3 ratio) {
		
		return new Vec3((userVector.x - previousUserVector.x)/ratio.x, (userVector.y - previousUserVector.y)/ratio.y, (userVector.z - previousUserVector.z)/ratio.z);
	}
}
