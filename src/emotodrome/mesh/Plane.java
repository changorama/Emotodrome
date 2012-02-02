package emotodrome.mesh;

public class Plane extends Mesh {
	private float texture[];
	
	public Plane() {
		this(1, 1, 1, 1);
	}

	public Plane(float width, float height) {
		this(width, height, 1, 1);
		texture = new float[]{
				  0.0f, 0.0f, 
				  0.0f, 1.0f, 
				  1.0f, 0.0f, 
				  1.0f, 1.0f
				  };
	}

	public Plane(float width, float height, int widthSegments,
			int heightSegments) {
		float[] vertices = new float[(widthSegments + 1) * (heightSegments + 1)
				* 3];
		float[] normals = new float[(widthSegments + 1) * (heightSegments + 1)
		        * 3];
		short[] indices = new short[(widthSegments + 1) * (heightSegments + 1)
				* 6];
		texture = new float[widthSegments * heightSegments * 8];
		for (int i = 0; i < texture.length; i+=8){
			texture[i] = 0.0f;
			texture[i + 1] = 0.0f; 
			texture[i + 2] = 0.0f; 
			texture[i + 3] = 1.0f; 
			texture[i + 4] = 1.0f; 
			texture[i + 5] = 0.0f; 
			texture[i + 6] = 1.0f;
			texture[i + 7] = 1.0f;
		}
		
		float xOffset = width / -2;
		float yOffset = height / -2;
		float xWidth = width / (widthSegments);
		float yHeight = height / (heightSegments);
		int currentVertex = 0;
		int currentIndex = 0;
		short w = (short) (widthSegments + 1);
		for (int y = 0; y < heightSegments + 1; y++) {
			for (int x = 0; x < widthSegments + 1; x++) {
				vertices[currentVertex] = xOffset + x * xWidth;
				normals[currentVertex] = 0;
				vertices[currentVertex + 1] = yOffset + y * yHeight;
				normals[currentVertex + 1] = 0;
				vertices[currentVertex + 2] = 0;
				normals[currentVertex + 2] = 1;
				currentVertex += 3;

				int n = y * (widthSegments + 1) + x;

				if (y < heightSegments && x < widthSegments) {
					// Face one
					indices[currentIndex] = (short) n;
					indices[currentIndex + 1] = (short) (n + 1);
					indices[currentIndex + 2] = (short) (n + w);
					// Face two
					indices[currentIndex + 3] = (short) (n + 1);
					indices[currentIndex + 4] = (short) (n + 1 + w);
					indices[currentIndex + 5] = (short) (n + 1 + w - 1);

					currentIndex += 6;
				}
				
			}
		}

		setIndices(indices);
		setVertices(vertices);
		setTexture(texture);
		setNormals(normals);
		
		
	}
	
}
