//****************************************************************************
// SketchBase.  

//****************************************************************************
// Comments : 
//   Subroutines to manage and draw points, lines an triangles
//
// History :
//   Aug 2014 Created by Jianming Zhang (jimmie33@gmail.com) based on code by
//   Stan Sclaroff (from CS480 '06 poly.c)
//	Edited by Yuxin Sun: Created the DrawPointWithPhong, DrawLineWithPhong and DrawTriangleWithPhong functions

import java.awt.image.BufferedImage;

public class SketchBase 
{
	public SketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, Point3D p, float[][]depthBuffer)
	{
				if (p.x >= 0 && p.x < buff.getWidth() && p.y >=0 && p.y < buff.getHeight()) {
					if (depthBuffer[p.x][p.y] < p.z) {
						buff.setRGB((int)p.x, buff.getHeight()-(int)p.y-1, p.c.getRGB_int());	
						depthBuffer[(int)p.x][(int)p.y] = (int)p.z;	
					}
				}
	}
	
	public static void drawPointWithPhong(BufferedImage buff, Point3D p) {
		buff.setRGB((int)p.x, buff.getHeight()-(int)p.y-1, p.c.getRGB_int());	
	}
	
	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static void drawLine(BufferedImage buff, Point3D p1, Point3D p2, float[][] depthBuffer)
	{
	    int x0=p1.x, y0=p1.y;
	    int xEnd=p2.x, yEnd=p2.y;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0);

	    float dz = p2.z - p1.z;
	    
	    float steps = Math.max(Math.abs(dx), Math.abs(dy));
	    
	    float zInc = dz / steps;
	    
	    if(dx==0 && dy==0)
	    {
	    	drawPoint(buff,p1, depthBuffer);
	    	return;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {
	    	x0=(int)p1.y; 
	    	y0=p1.x;
	    	xEnd=p2.y; 
	    	yEnd=p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    Point3D pk = new Point3D(p1.x, p1.y, p1.z, p1.c);
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0;
	    
	    int z = pk.z;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    int r0=p1.c.getR_int(), rEnd=p2.c.getR_int();
	    int g0=p1.c.getG_int(), gEnd=p2.c.getG_int();
	    int b0=p1.c.getB_int(), bEnd=p2.c.getB_int();
	    
	    // compute the change in r,g,b 
	    int dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    int whole_step_r = step_r*(dr/dx);
	    int whole_step_g = step_g*(dg/dx);
	    int whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    int p_r = 2 * dr - dx;
	    int twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    int  r=r0;
	    
	    int p_g = 2 * dg - dx;
	    int twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    int g=g0;
	    
	    int p_b = 2 * db - dx;
	    int twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    int b=b0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
	    		if(depthBuffer[(int)y][(int)x] < z) {
	    			buff.setRGB((int)y, buff.getHeight()-(int)x-1, ((int)r<<16) | ((int)g<<8) | (int)b);
	    			depthBuffer[(int)y][(int)x] = z;
	    		}
	    	}
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()) {
	    		if(depthBuffer[(int)x][(int)y] < z) {
	    			buff.setRGB((int)x, buff.getHeight()-(int)y-1, ((int)r<<16) | ((int)g<<8) | (int)b);
	    			depthBuffer[(int)x][(int)y] = z;
	    		}
	    	}
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x and y
	    	x+=step_x;
	    	z+=zInc;
	    	pk.z = Math.round(z);
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y+=step_y;
	    		p += twoDyMinusDx;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()) {
	    			if (depthBuffer[(int)y][(int)x] < z) {
	    				buff.setRGB((int)y, buff.getHeight()-(int)x-1, ((int)r<<16) | ((int)g<<8) | (int)b);
	    				depthBuffer[(int)y][(int)x] = z;
	    			}
	    		}
	    		
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()) {
	    			if(depthBuffer[(int)x][(int)y] < z) {
	    				buff.setRGB((int)x, buff.getHeight()-(int)y-1, ((int)r<<16) | ((int)g<<8) | (int)b);
	    				depthBuffer[(int)x][(int)y] = z;
	    			}
	    		}
	    			
	    	}
	    }
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static void drawTriangle(BufferedImage buff, Point3D p1, Point3D p2, Point3D p3, boolean do_smooth, float[][] depthBuffer)
	{
	    // sort the triangle vertices by ascending x value
	    Point3D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
	    
	    if(!do_smooth)
	    {
	    	side_a.c = new ColorType(p1.c);
	    	side_b.c = new ColorType(p1.c);
	    }
	    
	    y_b = p[0].y;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for segment b
	    	dr_b = ((float)(p[2].c.r - p[0].c.r))/(p[2].x - p[0].x);
	    	dg_b = ((float)(p[2].c.g - p[0].c.g))/(p[2].x - p[0].x);
	    	db_b = ((float)(p[2].c.b - p[0].c.b))/(p[2].x - p[0].x);
	    }
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);
		    
	    	if(do_smooth)
	    	{
	    		// calculate slopes in r, g, b for segment a
	    		dr_a = ((float)(p[1].c.r - p[0].c.r))/(p[1].x - p[0].x);
	    		dg_a = ((float)(p[1].c.g - p[0].c.g))/(p[1].x - p[0].x);
	    		db_a = ((float)(p[1].c.b - p[0].c.b))/(p[1].x - p[0].x);
	    	}
		    
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = (int)p[0].x; x < p[1].x; ++x)
		    {
		    	drawLine(buff, side_a, side_b, depthBuffer);

		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;
		    	if(do_smooth)
		    	{
		    		side_a.c.r +=dr_a;
		    		side_b.c.r +=dr_b;
		    		side_a.c.g +=dg_a;
		    		side_b.c.g +=dg_b;
		    		side_a.c.b +=db_a;
		    		side_b.c.b +=db_b;
		    	}
		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new Point3D(p[1]);
	    if(!do_smooth)
	    	side_a.c =new ColorType(p1.c);
	    
	    y_a = p[1].y;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);
	    if(do_smooth)
	    {
	    	// calculate slopes in r, g, b for replacement for segment a
	    	dr_a = ((float)(p[2].c.r - p[1].c.r))/(p[2].x - p[1].x);
	    	dg_a = ((float)(p[2].c.g - p[1].c.g))/(p[2].x - p[1].x);
	    	db_a = ((float)(p[2].c.b - p[1].c.b))/(p[2].x - p[1].x);
	    }

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = (int)p[1].x; x <= p[2].x; ++x)
	    {
	    	drawLine(buff, side_a, side_b, depthBuffer);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;
	    	if(do_smooth)
	    	{
	    		side_a.c.r +=dr_a;
	    		side_b.c.r +=dr_b;
	    		side_a.c.g +=dg_a;
	    		side_b.c.g +=dg_b;
	    		side_a.c.b +=db_a;
	    		side_b.c.b +=db_b;
	    	}
	    }
	}
	
	public static void drawLineWithPhong(BufferedImage buff, float[][] depthBuff,
			Point3D p1, Point3D p2, Light light, Material mat, Vector3D v) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		float dz = p2.z - p1.z;
		float dnx = p2.n.x - p1.n.x;
		float dny = p2.n.y - p1.n.y;
		float dnz = p2.n.z - p1.n.z;

		float steps;

		if (Math.abs(dx) > Math.abs(dy)) {
			steps = Math.abs(dx);
		} else {
			steps = Math.abs(dy);
		}

		float xInc = dx / steps;
		float yInc = dy / steps;
		float zInc = dz / steps;

		Point3D pk = new Point3D(p1.x, p1.y, p1.z, p1.c);
		pk.n = new Vector3D(p1.n);

		float slope_nx, slope_ny, slope_nz;

		slope_nx = dnx / steps;
		slope_ny = dny / steps;
		slope_nz = dnz / steps;
		
		Vector3D new_n = new Vector3D(pk.n);
		pk.c = light.applyLight(mat, v, new_n, pk.toVector());

		if (pk.y >= 0 && pk.y < buff.getHeight() && pk.x >= 0
				&& pk.x < buff.getWidth()
				&& pk.z > depthBuff[(int) pk.x][(int) pk.y]) {
			depthBuff[(int) pk.x][(int) pk.y] = pk.z;
			drawPointWithPhong(buff, pk);
		}

		float x = pk.x, y = pk.y, z = pk.z;

		if (p1.x == p2.x) { // Line is vertical
			for (int k = 0; k < steps; k++) {
				y += yInc;
				z += zInc;

				pk.y = Math.round(y);
				pk.z = Math.round(z);

				new_n.x += slope_nx;
				new_n.y += slope_ny;
				new_n.z += slope_nz;
				
				ColorType result = light.applyLight(mat, v, new_n, pk.toVector());

				int rgb = (int) ((Math.round(result.getR_int()) << 16)
						| (Math.round(result.getG_int()) << 8) | Math.round(result.getB_int()));
				if (y >= 0 && y < buff.getHeight() && x >= 0
						&& x < buff.getWidth()
						&& z > depthBuff[(int) pk.x][(int) pk.y]) {
					depthBuff[(int) pk.x][(int) pk.y] = pk.z;
					buff.setRGB(pk.x, buff.getHeight() - pk.y - 1, rgb);
				}

			}
		} else {
			for (int k = 0; k < steps; k++) {
				x += xInc;
				y += yInc;
				z += zInc;

				pk.x = Math.round(x);
				pk.y = Math.round(y);
				pk.z = Math.round(z);

				new_n.x += slope_nx;
				new_n.y += slope_ny;
				new_n.z += slope_nz;
				
				ColorType result = light.applyLight(mat, v, new_n, pk.toVector());

				int rgb = (int) ((Math.round(result.getR_int()) << 16)
						| (Math.round(result.getG_int()) << 8) | Math.round(result.getB_int()));

				if (y >= 0 && y < buff.getHeight() && x >= 0
						&& x < buff.getWidth()
						&& z > depthBuff[(int) pk.x][(int) pk.y]) {
					depthBuff[(int) pk.x][(int) pk.y] = pk.z;
					buff.setRGB(pk.x, buff.getHeight() - pk.y - 1, rgb);
				}

			}
		}

	}
	
	public static void drawTriangleWithPhong(BufferedImage buff,float[][] depthBuff, Point3D p1, Point3D p2, Point3D p3,
			Vector3D n1, Vector3D n2, Vector3D n3, Light light, Material mat, Vector3D v) {
		// sort the triangle vertices by ascending x value
		p1.n = n1;
		p2.n = n2;
		p3.n = n3;
		Point3D p[] = sortTriangleVerts(p1, p2, p3);
		int x;
		float y_a, y_b;
		float z_a, z_b;

		Point3D side_a = new Point3D(p[0]), side_b = new Point3D(p[0]);
		side_a.n = new Vector3D(p[0].n);
		side_b.n = new Vector3D(p[0].n);
		side_a.c = new ColorType(p1.c);
		side_b.c = new ColorType(p1.c);

		y_b = p[0].y;
		z_b = p[0].z;
		float dy_b = ((float) (p[2].y - p[0].y)) / (p[2].x - p[0].x);
		float dz_b = ((float) (p[2].z - p[0].z)) / (p[2].x - p[0].x);

		float dy_a, dz_a, dnx_a = 0, dny_a = 0, dnz_a = 0, dnx_b = 0, dny_b = 0, dnz_b = 0;
		// calculate slopes in r, g, b for segment b
		dnx_b = ((float) (p[2].n.x - p[0].n.x)) / (p[2].x - p[0].x);
		dny_b = ((float) (p[2].n.y - p[0].n.y)) / (p[2].x - p[0].x);
		dnz_b = ((float) (p[2].n.z - p[0].n.z)) / (p[2].x - p[0].x);

		// if there is a right-hand part to the triangle then fill it
		if (p[0].x != p[1].x) {
			y_a = p[0].y;
			z_a = p[0].z;
			dy_a = ((float) (p[1].y - p[0].y)) / (p[1].x - p[0].x);
			dz_a = ((float) (p[1].z - p[0].z)) / (p[1].x - p[0].x);

			dnx_a = ((float) (p[1].n.x - p[0].n.x)) / (p[1].x - p[0].x);
			dny_a = ((float) (p[1].n.y - p[0].n.y)) / (p[1].x - p[0].x);
			dnz_a = ((float) (p[1].n.z - p[0].n.z)) / (p[1].x - p[0].x);

			// loop over the columns for right-hand part of triangle
			// filling from side a to side b of the span
			for (x = p[0].x; x < p[1].x; ++x) {
				drawLineWithPhong(buff, depthBuff, side_a, side_b, light, mat, v);

				++side_a.x;
				++side_b.x;
				y_a += dy_a;
				y_b += dy_b;
				z_a += dz_a;
				z_b += dz_b;
				side_a.y = (int) y_a;
				side_b.y = (int) y_b;
				side_a.z = (int) z_a;
				side_b.z = (int) z_b;
				
				// Phong normal interpolation
				side_a.n.x += dnx_a;
				side_b.n.x += dnx_b;
				side_a.n.y += dny_a;
				side_b.n.y += dny_b;
				side_a.n.z += dnz_a;
				side_b.n.z += dnz_b;
				
				side_a.c = light.applyLight(mat, v, side_a.n, side_a.toVector());
				side_b.c = light.applyLight(mat, v, side_b.n, side_b.toVector());
			}
		}

		// there is no left-hand part of triangle
		if (p[1].x == p[2].x)
			return;

		// set up to fill the left-hand part of triangle
		// replace segment a
		side_a = new Point3D(p[1]);
		side_a.c = new ColorType(p1.c);
		side_a.n = new Vector3D(p[1].n);

		y_a = p[1].y;
		z_a = p[1].z;
		dy_a = ((float) (p[2].y - p[1].y)) / (p[2].x - p[1].x);
		dz_a = ((float) (p[2].z - p[1].z)) / (p[2].x - p[1].x);
		// calculate slopes in r, g, b for replacement for segment a
		dnx_a = ((float) (p[2].n.x - p[1].n.x)) / (p[2].x - p[1].x);
		dny_a = ((float) (p[2].n.y - p[1].n.y)) / (p[2].x - p[1].x);
		dnz_a = ((float) (p[2].n.z - p[1].n.z)) / (p[2].x - p[1].x);

		// loop over the columns for left-hand part of triangle
		// filling from side a to side b of the span
		for (x = p[1].x; x <= p[2].x; ++x) {
			drawLineWithPhong(buff, depthBuff, side_a, side_b, light, mat, v);

			++side_a.x;
			++side_b.x;
			y_a += dy_a;
			y_b += dy_b;
			z_a += dz_a;
			z_b += dz_b;
			side_a.y = (int) y_a;
			side_b.y = (int) y_b;
			side_a.z = (int) z_a;
			side_b.z = (int) z_b;
			
			// Phong normal interpolation
			side_a.n.x += dnx_a;
			side_b.n.x += dnx_b;
			side_a.n.y += dny_a;
			side_b.n.y += dny_b;
			side_a.n.z += dnz_a;
			side_b.n.z += dnz_b;
			
			side_a.c = light.applyLight(mat, v, side_a.n, side_a.toVector());
			side_b.c = light.applyLight(mat, v, side_b.n, side_b.toVector());
		}
	}
	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static Point3D[] sortTriangleVerts(Point3D p1, Point3D p2, Point3D p3)
	{
	    Point3D pts[] = {p1, p2, p3};
	    Point3D tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}


}