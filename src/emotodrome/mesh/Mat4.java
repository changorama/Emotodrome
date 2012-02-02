package emotodrome.mesh;

/**
 * A 4x4 matrix useful when defining transformations.
 * @author fabio
 */
public class Mat4 {
    /**
     * Components
     */
    public float       d[][];
    
    /**
     * Default constrctor - creates an identiy matrix, not a zero one!
     */
    public Mat4() {
        d = new float[4][4];
        d[0][0] = 1;
        d[1][1] = 1;
        d[2][2] = 1;
        d[3][3] = 1;
    }
    
    /**
     * Flatten data in row major order.
     */
    public float[] getFlatDataRowMajor() {
        float r[] = new float[16];
        r[ 0] = d[0][0]; r[ 1] = d[0][1]; r[ 2] = d[0][2]; r[ 3] = d[0][3];  
        r[ 4] = d[1][0]; r[ 5] = d[1][1]; r[ 6] = d[1][2]; r[ 7] = d[1][3];  
        r[ 8] = d[2][0]; r[ 9] = d[2][1]; r[10] = d[2][2]; r[11] = d[2][3];  
        r[12] = d[3][0]; r[13] = d[3][1]; r[14] = d[3][2]; r[15] = d[3][3];  
        return r;
    }
    
    /**
     * Flatten data ni colum major order.
     */
    public float[] getFlatDataColumnMajor() {
        float c[] = new float[16];
        c[0] = d[0][0]; c[4] = d[0][1]; c[ 8] = d[0][2]; c[12] = d[0][3];  
        c[1] = d[1][0]; c[5] = d[1][1]; c[ 9] = d[1][2]; c[13] = d[1][3];  
        c[2] = d[2][0]; c[6] = d[2][1]; c[10] = d[2][2]; c[14] = d[2][3];  
        c[3] = d[3][0]; c[7] = d[3][1]; c[11] = d[3][2]; c[15] = d[3][3];
        return c;
    }
    
    /**
     * Transform a point. Ignores w component (use project for perspective tranforms).
     */
    public Vec3 transform(Vec3 p) {
        return new Vec3(
        		p.x * d[0][0] + p.y * d[0][1] + p.z * d[0][2] + d[0][3],
        		p.x * d[1][0] + p.y * d[1][1] + p.z * d[1][2] + d[1][3],
        		p.x * d[2][0] + p.y * d[2][1] + p.z * d[2][2] + d[2][3]
        );
    }
    
    /**
     * Grab the unnormalized 3x3 inverse transpose to trasnform normals.
     * Normls are not normalized after this!
     * See Shirley p. 180
     */
    public Mat4 normalTransform() {
    	Mat4 m = new Mat4();
    	m.d[0][0] = d[1][1] * d[2][2] - d[1][2] * d[2][1];
        m.d[0][1] = d[0][1] * d[2][2] - d[2][1] * d[0][2];
        m.d[0][2] = d[0][1] * d[1][2] - d[1][1] * d[0][2];
        m.d[0][3] = 0;
        m.d[1][0] = d[1][0] * d[2][2] - d[2][0] * d[1][2];
        m.d[1][1] = d[0][0] * d[2][2] - d[2][0] * d[0][2];
        m.d[1][2] = d[0][0] * d[1][2] - d[1][0] * d[0][2];
        m.d[1][3] = 0;
        m.d[2][0] = d[0][0] * d[2][1] - d[2][0] * d[0][1];
        m.d[2][1] = d[1][0] * d[2][1] - d[2][0] * d[1][1];
        m.d[2][2] = d[0][0] * d[1][1] - d[1][0] * d[0][1];
        m.d[2][3] = 0;
        m.d[3][0] = 0;
        m.d[3][1] = 0;
        m.d[3][2] = 0;
        m.d[3][3] = 1;
        return m;
    }
    
    /**
     * Multiply this matrix by m0. m0 is on the right.
     */
    public Mat4 multiply(Mat4 m0) {
    	Mat4 m = new Mat4();
        m.d[0][0] = d[0][0] * m0.d[0][0] + d[0][1] * m0.d[1][0] + d[0][2] * m0.d[2][0] + d[0][3] * m0.d[3][0];  
        m.d[0][1] = d[0][0] * m0.d[0][1] + d[0][1] * m0.d[1][1] + d[0][2] * m0.d[2][1] + d[0][3] * m0.d[3][1];
        m.d[0][2] = d[0][0] * m0.d[0][2] + d[0][1] * m0.d[1][2] + d[0][2] * m0.d[2][2] + d[0][3] * m0.d[3][2];
        m.d[0][3] = d[0][0] * m0.d[0][3] + d[0][1] * m0.d[1][3] + d[0][2] * m0.d[2][3] + d[0][3] * m0.d[3][3];
        m.d[1][0] = d[1][0] * m0.d[0][0] + d[1][1] * m0.d[1][0] + d[1][2] * m0.d[2][0] + d[1][3] * m0.d[3][0];
        m.d[1][1] = d[1][0] * m0.d[0][1] + d[1][1] * m0.d[1][1] + d[1][2] * m0.d[2][1] + d[1][3] * m0.d[3][1];
        m.d[1][2] = d[1][0] * m0.d[0][2] + d[1][1] * m0.d[1][2] + d[1][2] * m0.d[2][2] + d[1][3] * m0.d[3][2];
        m.d[1][3] = d[1][0] * m0.d[0][3] + d[1][1] * m0.d[1][3] + d[1][2] * m0.d[2][3] + d[1][3] * m0.d[3][3];
        m.d[2][0] = d[2][0] * m0.d[0][0] + d[2][1] * m0.d[1][0] + d[2][2] * m0.d[2][0] + d[2][3] * m0.d[3][0];
        m.d[2][1] = d[2][0] * m0.d[0][1] + d[2][1] * m0.d[1][1] + d[2][2] * m0.d[2][1] + d[2][3] * m0.d[3][1];
        m.d[2][2] = d[2][0] * m0.d[0][2] + d[2][1] * m0.d[1][2] + d[2][2] * m0.d[2][2] + d[2][3] * m0.d[3][2];
        m.d[2][3] = d[2][0] * m0.d[0][3] + d[2][1] * m0.d[1][3] + d[2][2] * m0.d[2][3] + d[2][3] * m0.d[3][3];
        m.d[3][0] = d[3][0] * m0.d[0][0] + d[3][1] * m0.d[1][0] + d[3][2] * m0.d[2][0] + d[3][3] * m0.d[3][0];
        m.d[3][1] = d[3][0] * m0.d[0][1] + d[3][1] * m0.d[1][1] + d[3][2] * m0.d[2][1] + d[3][3] * m0.d[3][1];
        m.d[3][2] = d[3][0] * m0.d[0][2] + d[3][1] * m0.d[1][2] + d[3][2] * m0.d[2][2] + d[3][3] * m0.d[3][2];
        m.d[3][3] = d[3][0] * m0.d[0][3] + d[3][1] * m0.d[1][3] + d[3][2] * m0.d[2][3] + d[3][3] * m0.d[3][3];
        return m;
    }
    
    /**
     * transpose
     */
    public Mat4 transpose() {
    	Mat4 m = new Mat4();
        for(int a = 0; a < 4; a++)
        {
        	for(int b = a; b < 4; b++)
        	{
        		m.d[a][b] = d[b][a];
        		m.d[b][a] = d[a][b];
        	}
        }
        return m;
    }
    
    /**
     * Create translation matrix
     */
    public static Mat4 translationMatrix(Vec3 t) {
        Mat4 m = new Mat4();
        m.d[0][3] = t.x;
        m.d[1][3] = t.y;
        m.d[2][3] = t.z;
        return m;
    }

    /**
     * Create rotation matrix around x
     */
    public static Mat4 xrotationMatrix(float r) {
        Mat4 m = new Mat4();
        m.d[0][0] = (float) Math.cos(r);
        m.d[0][1] = (float) -Math.sin(r);
        m.d[1][0] = -m.d[0][1];
        m.d[1][1] = m.d[0][0];
        return m;
    }
    
    /**
     * Create rotation matrix around y
     */
    public static Mat4 yrotationMatrix(float r) {
    	Mat4 m = new Mat4();
        m.d[0][0] = (float) Math.cos(r);
        m.d[0][2] = (float) -Math.sin(r);
        m.d[2][0] = -m.d[0][2];
        m.d[2][2] = m.d[0][0];
        return m;
    }
    
    /**
     * Create rotation matrix around z
     */
    public static Mat4 zrotationMatrix(float r) {
    	Mat4 m = new Mat4();
        m.d[1][1] = (float) Math.cos(r);
        m.d[1][2] = (float) -Math.sin(r);
        m.d[2][1] = -m.d[0][1];
        m.d[2][2] = m.d[0][0];
        return m;
    }
    
    /**
     * Create scaling matrix
     */
    public static Mat4 scalingMatrix(Vec3 s) {
    	Mat4 m = new Mat4();
        m.d[0][0] = s.x;
        m.d[1][1] = s.y;
        m.d[2][2] = s.z;
        return m;
    }
    
    /**
     * creates a copy of the current trasnform and return it
     */
    public Mat4 copy() {
        Mat4 m = new Mat4();
        for(int i = 0; i < 4; i ++) {
            for(int j = 0; j < 4; j ++) {
                m.d[i][j] = d[i][j];
            }
        }
        return m;        
    }
}