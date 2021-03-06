/* PointLight class created by Yuxin Sun to set up the light source of point
 * 
 */

public class PointLight extends Light {
	public Vector3D direction;
	public Vector3D lightPosition;
	public ColorType color;
	public Boolean radial = false;
	public Boolean angular = false;
	public float alpha;
	private float a0,a1,a2;
	
	public PointLight(ColorType color, Vector3D direction, Vector3D lightPosition) {
		this.color = color;
		this.direction = direction;
		this.lightPosition = lightPosition;
		a0 = 1;
		a1 = a2 = .000001f;
		alpha = 45;
		super.light_on = true;
	}
	
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D point) {
		ColorType res = new ColorType();
		
		Vector3D L = lightPosition.minus(point); // vobj
		L.normalize();
		double dot = L.dotProduct(n);
		if (dot > 0.0) {
			if (mat.diffuse) {
				res.r = (float)(dot * mat.get_k_diff().r * color.r);
				res.g = (float)(dot * mat.get_k_diff().g * color.g);
				res.b = (float)(dot * mat.get_k_diff().b * color.b);
			}
			
			if (mat.specular) {
				Vector3D r = L.reflect(n);
				dot = r.dotProduct(v);
				if (dot > 0.0) {
					res.r += (float)Math.pow((dot * mat.get_k_spec().r * color.r), mat.get_specular_exp());
					res.g += (float)Math.pow((dot * mat.get_k_spec().g * color.g), mat.get_specular_exp());
					res.r += (float)Math.pow((dot * mat.get_k_spec().b * color.b), mat.get_specular_exp());
				}
			}
			
			if (radial) {
				float d = (float) Math.sqrt(Math.pow(lightPosition.x - point.x, 2) + Math.pow(lightPosition.y - point.y, 2) + Math.pow(lightPosition.z - point.z, 2));
				float radialFactor = 1 / (a0 + a1 * d + a2 * (float)Math.pow(d, 2));
				res.r *= radialFactor;
				res.g *= radialFactor;
				res.b *= radialFactor;
			}
			
			if (angular) {
				dot = L.dotProduct(direction);
				if (dot < Math.cos(1.57079633)) {
					res.r *= Math.pow(dot, alpha);
					res.g *= Math.pow(dot, alpha);
					res.b *= Math.pow(dot, alpha);
				}
			}
			
			// Clamp
			res.clamp();
		}
		return(res);
	}
	
	public void toggleRadial() {
		radial = !radial;
	}
	
	public void toggleAngular() {
		angular = !angular;
	}
	
	public void toggleLight() {
		light_on = !light_on;
	}
}