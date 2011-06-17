/* Shapes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 27, 2010 5:43:13 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.canvas.util;

import org.zkoss.canvas.Path;

/**
 * Predefined Shapes
 * @author simon
 */
public class Shapes {
	
	// TODO: add more shapes like circle, etc
	
	public static Path nGon(double r, int n){
		Path p = new Path().moveTo(r, 0);
		for(int i=1; i<n+1; i++){
			double arg = Math.PI * (1.5 + (2.0*i)/n);
			p.lineTo(r + r * Math.cos(arg), r + r * Math.sin(arg));
		}
		p.closePath();
		return p;
	}
	
	// TODO: add nStar given by two radius
	
	public static Path nStar(double r, int n, double theta){
		// TODO: fix algorithm
		Path p = new Path().moveTo(r, 0);
		double r2 = r * Math.sin(Math.PI*theta/360) / Math.sin(Math.PI*(theta/360 + 2.0/n));
		
		for(int i=1; i<n+1; i++){
			double arg1 = Math.PI * (1.5 + (2.0*i)/n);
			double arg2 = arg1 - Math.PI/n;
			p.lineTo(r + r2 * Math.cos(arg2), r + r2 * Math.sin(arg2));
			p.lineTo(r + r * Math.cos(arg1), r + r * Math.sin(arg1));
		}
		
		p.closePath();
		return p;
	}
	
	public static Path heart(double size){
		double ctrp1X = 0.1 * size;
		double ctrp1Y = -0.2 * size;
		double ctrp2X = -0.04 * size;
		double ctrp2Y = 0.2 * size;
		double ctrp3X = 0.08 * size;
		double ctrp3Y = -0.4 * size;
		double ctrp4X = 0;
		double ctrp4Y = -0.34 * size;
		
		double midY = 0.4 * size;
		double midYC = 0.34 * size;
		
		double p1X = 0.5 * size;
		double p1Y = size;
		double p2X = 0.96 * size;
		double p2Y = midY;
		double p3X = 0.5 * size;
		double p3Y = midYC;
		double p4X = 0.04 * size;
		double p4Y = midY;
		
		Path p = new Path().moveTo(p1X, p1Y)
			.curveTo(p1X + ctrp1X, p1Y + ctrp1Y, p2X + ctrp2X, p2Y + ctrp2Y, p2X, p2Y)
			.curveTo(p2X + ctrp3X, p2Y + ctrp3Y, p3X + ctrp4X, p3Y + ctrp4Y, p3X, p3Y)
			.curveTo(p3X - ctrp4X, p3Y + ctrp4Y, p4X - ctrp3X, p4Y + ctrp3Y, p4X, p4Y)
			.curveTo(p4X - ctrp2X, p4Y + ctrp2Y, p1X - ctrp1X, p1Y + ctrp1Y, p1X, p1Y)
			.closePath();
		
		return p;
	}
	
}
