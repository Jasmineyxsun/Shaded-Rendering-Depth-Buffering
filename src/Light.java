import java.util.*;

//Combined light module to calculate attenuations and other light sources

public class Light {
	//List of lights
	public List<Light> lights;
	public boolean light_on;
	
	public Light() {
		lights = new ArrayList<Light>();
		light_on = true;
	}
	
	public void addLight(Light newLight) {
		lights.add(newLight);
	}
	
	public void removeLight(Light l) {
		lights.remove(l);
	}
	
	public int size(){
		return lights.size();
	}
	
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, Vector3D p) {
		ColorType res = new ColorType();
		ColorType summ = new ColorType();
		ColorType amb = new ColorType();
		for (Light l : lights) {
			if (l instanceof AmbientLight) {
				if (l.light_on) amb = l.applyLight(mat, v, n, p);
			}else {
				if (l.light_on) {
					summ = l.applyLight(mat, v, n, p);
			
					res.r += summ.r;
					res.g += summ.g;
					res.b += summ.b;
				}
			}
		}
		
		//Combined lighting module
		res.r = amb.r + res.r;
		res.g = amb.g + res.g;
		res.b = amb.b + res.b;
		
		// Clamp
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		
		return res;
		}
		public void toggleLight() {
			light_on = !light_on;
		}
	
}
