package emotodrome.mesh;

public class LocatorLine extends Mesh{
	private final float locatorThickness = .2f;
	private final float locatorHeight = -.8f;
	
	public LocatorLine(Vec3 p1, Vec3 p2){
		float[] vertices = {p1.x - locatorThickness/2, locatorHeight, p1.z - locatorThickness/2, p1.x + locatorThickness/2, locatorHeight, p1.z + locatorThickness/2,
				p2.x - locatorThickness/2, locatorHeight, p2.z - locatorThickness/2, p2.x + locatorThickness/2, locatorHeight, p2.z + locatorThickness/2};
		short[] indices = {0, 1, 3, 0, 3, 2};
		float[] normals = {0,1,0,0,1,0,0,1,0,0,1,0};
	    setVertices(vertices);
        setIndices(indices);
        setNormals(normals);
	}
	
}
