package emotodrome.user;

import javax.microedition.khronos.opengles.GL10;

import emotodrome.mesh.Mesh;
import emotodrome.mesh.Vec3;

/**
 * Class to hold the user's position and the avatar that will represent that user
 */
public class User {

	private Vec3 userVector;
	private Mesh userAvatar;
	
	public User(Vec3 userVector){
		this.userVector = userVector;
	}
	
	public Mesh setUserAvatar(Mesh userAvatar){
		this.userAvatar = userAvatar;
		return this.userAvatar;
	}
	
	public Mesh getUserAvatar(){
		return userAvatar;
	}
	
	public void setUserVector(Vec3 userVector){
		this.userVector = userVector;
	}
	
	public Vec3 getUserVector(){
		return userVector;
	}
	
	public void draw(GL10 gl){
		userAvatar.draw(gl);
	}
}
