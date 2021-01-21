package kos.evolutionterraingenerator.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;

public class Vec2d  implements Position {
	   public static final Vec2d ZERO = new Vec2d(0.0D, 0.0D);
	   public final double x;
	   public final double y;

	   public Vec2d(double x, double y) {
	      this.x = x;
	      this.y = y;
	   }

	   public Vec2d reverseSubtract(Vec2d vec) {
	      return new Vec2d(vec.x - this.x, vec.y - this.y);
	   }

	   public Vec2d normalize() {
	      double d = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y);
	      return d < 1.0E-4D ? ZERO : new Vec2d(this.x / d, this.y / d);
	   }

	   public double dotProduct(Vec2d vec) {
	      return this.x * vec.x + this.y * vec.y;
	   }

	   public Vec2d subtract(Vec2d vec) {
	      return this.subtract(vec.x, vec.y);
	   }

	   public Vec2d subtract(double x, double y) {
	      return this.add(-x, -y);
	   }

	   public Vec2d add(Vec2d vec) {
	      return this.add(vec.x, vec.y);
	   }

	   public Vec2d add(double x, double y) {
	      return new Vec2d(this.x + x, this.y + y);
	   }

	   public boolean isInRange(Position pos, double radius) {
	      return this.squaredDistanceTo(pos.getX(), pos.getY()) < radius * radius;
	   }

	   public double distanceTo(Vec2d vec) {
	      double d = vec.x - this.x;
	      double e = vec.y - this.y;
	      return (double)MathHelper.sqrt(d * d + e * e );
	   }

	   public double squaredDistanceTo(Vec2d vec) {
	      double d = vec.x - this.x;
	      double e = vec.y - this.y;
	      return d * d + e * e;
	   }

	   public double squaredDistanceTo(double x, double y) {
	      double d = x - this.x;
	      double e = y - this.y;
	      return d * d + e * e;
	   }

	   public Vec2d multiply(double mult) {
	      return this.multiply(mult, mult);
	   }

	   public Vec2d negate() {
	      return this.multiply(-1.0D);
	   }

	   public Vec2d multiply(Vec2d mult) {
	      return this.multiply(mult.x, mult.y);
	   }

	   public Vec2d multiply(double multX, double multY) {
	      return new Vec2d(this.x * multX, this.y * multY);
	   }

	   public double length() {
	      return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y);
	   }

	   public double lengthSquared() {
	      return this.x * this.x + this.y * this.y;
	   }

	   public boolean equals(Object o) {
	      if (this == o) {
	         return true;
	      } else if (!(o instanceof Vec2d)) {
	         return false;
	      } else {
	         Vec2d vec3d = (Vec2d)o;
	         if (Double.compare(vec3d.x, this.x) != 0) {
	            return false;
	         } else {
	            return Double.compare(vec3d.y, this.y) == 0;
	         }
	      }
	   }

	   public int hashCode() {
	      long l = Double.doubleToLongBits(this.x);
	      int i = (int)(l ^ l >>> 32);
	      l = Double.doubleToLongBits(this.y);
	      i = 31 * i + (int)(l ^ l >>> 32);
	      return i;
	   }

	   public String toString() {
	      return "(" + this.x + ", " + this.y + ")";
	   }
	   
	   public final double getX() {
	      return this.x;
	   }

	   public final double getY() {
	      return this.y;
	   }

	public double getZ() {
		return 0;
	}
}
