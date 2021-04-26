package kos.evolutionterraingenerator.util;

import net.minecraft.util.math.MathHelper;

public class Rectangle 
{
	double minX;
	double minY;
	double maxX;
	double maxY;
	
	public Rectangle(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
	    i = 31 * i + (int)(l ^ l >>> 32);
	    l = Double.doubleToLongBits(this.maxY);
	    i = 31 * i + (int)(l ^ l >>> 32);
	    return i;
	}

	public boolean contains(Vec2d vec) {
		return this.contains(vec.x, vec.y);
	}

	public boolean contains(double x, double y) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY;
	}

	public double getXLength() {
		return this.maxX - this.minX;
	}

	public double getYLength() {
		return this.maxY - this.minY;
	}
    
	public double distanceTo(Vec2d v)
    {
    	if (this.contains(v))
    		return 0.0;
    	
    	double xd = 0.0;
    	double yd = 0.0;
    	double zd = 0.0;
    	Vec2d c = this.getCenter();
    	
    	if (c.x < v.x)
			xd = v.x - this.maxX;
    	else
			xd = v.x - this.minX;
    	
		if (c.y < v.y)
			yd = v.y - this.maxY;
		else
			yd = v.y - this.minY;
		
    	return xd * xd + yd * yd + zd * zd;
    }

    public Vec2d getCenter() {
       return new Vec2d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY));
    }
}
