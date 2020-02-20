/* AmbientLight class created by Yuxin Sun to set up the light source of ambient
 * 
 */

public class AmbientLight extends Light{
	public Vector3D direction,position;
	public ColorType color;
	
	public AmbientLight(ColorType color, Vector3D direction) {
		this.color = color;
		this.direction = direction;
		super.light_on = true;
	}
	
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D p) {
		ColorType res = new ColorType();
		
		if (mat.ambient) {
			res.r = (float) (color.r * mat.ka.r);
			res.g = (float) (color.g * mat.ka.g);
			res.b = (float) (color.b * mat.ka.b);
		}

		// Clamp
		res.clamp();
		return(res);
	}
	
	public void toggleLight() {
		light_on = !light_on;
	}
}