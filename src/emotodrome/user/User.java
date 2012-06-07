package emotodrome.user;

import javax.microedition.khronos.opengles.GL10;

import emotodrome.mesh.Cube;
import emotodrome.mesh.Mesh;
import emotodrome.mesh.Vec3;

/**
 * Class to hold the user's position and the avatar that will represent that user
 */
public class User {

	private Mesh userAvatar;
	private Mesh userPlacemarker;
	private Vec3 latLon;
	private Vec3 moveLaterVector;
	private int id;
	public boolean discovered = false;
	
	public User(Vec3 userVector, int id){
		this.latLon = new Vec3(userVector.x, 0, userVector.z);
		this.id = id;
		moveLaterVector = new Vec3(0, 0, 0);
		setUserPlacemarker(new Cube(.5f, .5f, .5f));
	}
	
	public Mesh setUserAvatar(Mesh userAvatar){
		this.userAvatar = userAvatar;
		return this.userAvatar;
	}
	
	public Mesh getUserAvatar(){
		return userAvatar;
	}

	public void adjustUserVector(Vec3 moveVector){
		if (userAvatar != null){
			if (moveLaterVector != null){
				moveVector.setToAdd(moveLaterVector);
				moveLaterVector = null;
			}
			userAvatar.x += moveVector.x;
			userAvatar.y += moveVector.y;
			userAvatar.z += moveVector.z;
			System.out.println(userAvatar.x + "," + userAvatar.y + "," + userAvatar.z);
		}
		else {
			moveLaterVector.setToAdd(moveVector);
		}
	}
	
	public void draw(GL10 gl){
		if (discovered){
			userAvatar.draw(gl);
		}
	}

	public Vec3 getLatLon() {
		return latLon;
	}

	public void setLatLon(Vec3 latLon) {
		this.latLon = latLon;
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
