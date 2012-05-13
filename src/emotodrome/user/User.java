package emotodrome.user;

import javax.microedition.khronos.opengles.GL10;

import emotodrome.mesh.Cube;
import emotodrome.mesh.MapTile;
import emotodrome.mesh.Mesh;
import emotodrome.mesh.Vec3;
import emotodrome.project.OpenGLRenderer;

/**
 * Class to hold the user's position and the avatar that will represent that user
 */
public class User {

	private Vec3 userVector;
	private Vec3 previousUserVector;
	private Mesh userAvatar;
	private Mesh userPlacemarker;
	private Vec3 latLon;
	private int id;
	
	public User(Vec3 userVector, int id){
		this.userVector = userVector;
		this.previousUserVector = userVector;
		this.latLon = new Vec3(userVector.x, 0, userVector.z);
		this.id = id;
		setUserPlacemarker(new Cube(.5f, .5f, .5f));
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
		userAvatar.setPosition(userVector);
	}
	
	public void adjustUserVector(Vec3 moveVector){
		userVector.setToAdd(moveVector);
		userAvatar.x += moveVector.x;
		userAvatar.y += moveVector.y;
		userAvatar.z += moveVector.z;
		System.out.println(userAvatar.x + "," + userAvatar.y + "," + userAvatar.z);
	}
	
	public Vec3 getUserVector(){
		return userVector;
	}
	
	public void draw(GL10 gl){
		userAvatar.draw(gl);
	}

	public Vec3 getLatLon() {
		return latLon;
	}

	public void setLatLon(Vec3 latLon) {
		this.latLon = latLon;
	}

	public Vec3 getMoveAmount(Vec3 ratio) {
		
		return new Vec3((userVector.x - previousUserVector.x)/ratio.x, (userVector.y - previousUserVector.y)/ratio.y, (userVector.z - previousUserVector.z)/ratio.z);
	}

	public Mesh getUserPlacemarker() {
		return userPlacemarker;
	}

	public void setUserPlacemarker(Mesh userPlacemarker) {
		this.userPlacemarker = userPlacemarker;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
