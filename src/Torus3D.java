//****************************************************************************
//      Sphere class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//

public class Torus3D
{
	private Vector3D center;
	private float r, r_axial;
	private int rings,nsides;
	public Mesh3D mesh;
	
	public Torus3D(float _x, float _y, float _z, float _r, float _r_axial, int _nsides, int _rings)
	{	
		center = new Vector3D(_x,_y,_z);
		r = _r;
		r_axial = _r_axial;
		rings = _rings;
		nsides = _nsides;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_nsides(int _nsides)
	{
		nsides = _nsides;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_rings(int _rings)
	{
		rings = _rings;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return nsides;
	}
	
	public int get_m()
	{
		return rings;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(rings, nsides);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		// ****************Implement Code here*******************//
		int i, j;
		float theta, phi;
		float d_theta = (float)(2*Math.PI)/(float)(rings-1);
		float d_phi = (float)(2*Math.PI)/(float)(nsides-1);
		float cos_theta, sin_theta;
		float cos_phi, sin_phi;
		Vector3D du = new Vector3D();
		Vector3D dv = new Vector3D();
		
		for(i=0, theta=(float)(-Math.PI); i<rings; i++, theta+=d_theta) {
			cos_theta = (float)Math.cos(theta);
			sin_theta = (float)Math.sin(theta);
			
			for(j=0, phi=(float)(-Math.PI); j<nsides; j++, phi+=d_phi) {
				cos_phi = (float)Math.cos(phi);
				sin_phi = (float)Math.sin(phi);
				
				mesh.v[i][j].x = center.x+(r_axial+r*cos_phi)*cos_theta;
				mesh.v[i][j].y = center.y+(r_axial+r*cos_phi)*sin_theta;
				mesh.v[i][j].z = center.z+r*sin_phi;
				
				// normals
				du.x = (-r_axial+r*cos_phi)*sin_theta;
				du.y = (r_axial+r*cos_phi)*cos_theta;
				du.z = 0;
				
				dv.x = -r*sin_phi*cos_theta;
				dv.y = -r*sin_phi*sin_theta;
				dv.z = r*cos_phi;
				
				du.crossProduct(dv, mesh.n[i][j]);
				mesh.n[i][j].normalize();
			}
		}
	}
}