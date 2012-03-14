package emotodrome.mesh;

/**
 * 3D vector (can also be used to represent a 3d point).
 * @author fabio
 */
public class Vec3{
	/**
	 * x coordinate.
	 */
	public float x;
	/**
	 * y coordinate.
	 */
	public float y;
	/**
	 * z coordinate.
	 */
	public float z;
	
	/**
	 * Default contructor.
	 */
	public Vec3() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	/**
	 * Construct and initialize to given values.
	 * @param nx x
	 * @param ny y
	 * @param nz z
	 */
	public Vec3(float nx, float ny, float nz) {
		x = nx;
		y = ny;
		z = nz;
	}
	
	/**
	 * Copy constructor.
	 */
	public Vec3(Vec3 v) {
		set(v);
	}
	
	/**
	 * Assignment.
	 */
	public void set(Vec3 v) {
		x = v.x;
		y = v.y;
		z = v.z;				
	}

	/**
	 * Vector negatation.
	 * Assigns the value to this vector.
	 */
	public void setToNegate() {
		setToScale(-1);
	}
	
	/**
	 * Vector negatation.
	 */	
	public Vec3 negate() {
		return scale(-1);
	}
	
    /**
     * Vector invert.
     * Assigns the value to this vector.
     */
    public void setToInvert() {
        x = 1/x;
        y = 1/y;
        z = 1/z;
    }
    
    /**
     * Vector invert.
     */ 
    public Vec3 invert() {
        return new Vec3(1/x,1/y,1/z);
    }
    
	/**
	 * Vector scalar multiply.
	 * Assigns the value to this vector.
	 */
	public void setToScale(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	/**
	 * Vector scalar multiply.
	 */
	public Vec3 scale(float s) {
		return new Vec3(x * s,
				 		y * s,
				 		z * s);
	}

	/**
	 * Vector component-wise addition.
	 * Assigns the value to this vector.
	 */
	public void setToAdd(Vec3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	public void addX(float x){
		this.x += x;
	}
	
	public void addY(float y){
		this.y += y;
	}
	
	public void addZ(float z){
		this.z += z;
	}
	/**
	 * Vector component-wise addition.
	 */
	public Vec3 add(Vec3 v) {
		return new Vec3(x + v.x,
						y + v.y,
						z + v.z);
	}
	
	/**
	 * Vector component-wise subtraction.
	 * Assigns the value to this vector.
	 */
	public void setToSub(Vec3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	/**
	 * Vector component-wise subtraction.
	 */
	public Vec3 sub(Vec3 v) {
		return new Vec3(x - v.x,
						y - v.y,
						z - v.z);
	}
	
	/**
	 * Vector dot product.
	 */
	public float dot(Vec3 v) {
		return x * v.x +
		       y * v.y +
		       z * v.z;
	}	
	
	/**
	 * Vector length.
	 */
	public float length() {
		return (float) Math.sqrt(dot(this));
	}
	
	/**
	 * Vector normalization.
	 * Assigns the value to this vector.
	 */
	public void setToNormalize() {
		float l = length();
		if(l > 0) {
			setToScale(1/l);
		}		
	}
	
	/**
	 * Vector normalization.
	 */
	public Vec3 normalize() {
		float l = length();
		if(l > 0) {
			return scale(1/l);
		} else {
			return new Vec3(0,0,0);
		}
	}

	/**
	 * Vector cross product.
	 * Assigns the value to this vector.
	 */
	public void setToCross(Vec3 v) {
		// just make a copy for now
		set(cross(v));
	}
	
	/**
	 * Vector cross product.
	 */
	public Vec3 cross(Vec3 v) {
        return new Vec3(y*v.z - z*v.y,
                		z*v.x - x*v.z,
                		x*v.y - y*v.x);
	}

	/**
	 * Reflect this vector around the normal n.
	 * This vector is intended as pointing toward the surface.
	 */
	public Vec3 reflect(Vec3 n) {
		return n.scale(-2*n.dot(this)).add(this);		
	}
    
	public double distance(Vec3 v)
	{
		return Math.sqrt((this.x - v.x) * (this.x - v.x) + (this.y - v.y) * (this.y - v.y) + (this.z - v.z) * (this.z - v.z));
	}
    /**
     * Converts to a string
     */
    public String toString() {
        return new String("["+x+","+y+","+z+"]");
    }
    
    public boolean equals(Object v){
    	return ((Vec3)v).x == x && ((Vec3)v).y == y && ((Vec3)v).z == z; 
    }
    
    public int hashCode(){
		return (int) (x * 360 + z);
    	
    }

}
