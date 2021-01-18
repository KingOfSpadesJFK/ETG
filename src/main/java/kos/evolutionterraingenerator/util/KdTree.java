package kos.evolutionterraingenerator.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 2d-tree implementation. A mutable data type that uses a 2d-tree to implement
 * the same API (but replace {@code PointSET} with {@code KdTree}). A 2d-tree is
 * a generalization of a BST to two-dimensional keys. The idea is to build a BST
 * with points in the nodes, using the x- and y-coordinates of the points as
 * keys in strictly alternating sequence.
 *
 * @author Mincong Huang
 */
public class KdTree<V> {

    private enum Separator { LEFT_RIGHT, UP_DOWN, FORWARD_BACK }
    private Node<V> root;
    private int size;

    private static class Node<V> {

        private final Separator sepr;
        private final Box rect;
        private final Point p;
        private Node<V> leftBottom;
        private Node<V> rightTop;
        private V value;

        Node(Point p, Separator sepr, Box rect, V value) {
            this.p = p;
            this.sepr = sepr;
            this.rect = rect;
            this.value = value;
        }

        public Separator nextSepr() {
            return sepr == Separator.LEFT_RIGHT ? Separator.UP_DOWN : 
                    	(sepr == Separator.UP_DOWN ? Separator.FORWARD_BACK : Separator.LEFT_RIGHT);
        }

        public Box rectLB() {
            return sepr == Separator.LEFT_RIGHT
                    ? new Box(rect.getMinX(), rect.getMinY(), rect.getMinZ(), p.x, rect.getMaxY(), rect.getMaxZ())
                    	: (sepr == Separator.UP_DOWN 
                    		? new Box(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), p.y, rect.getMaxZ())
                    		: new Box(rect.getMinX(), rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), p.z));
        }

        public Box rectRT() {
            return sepr == Separator.LEFT_RIGHT
                    ? new Box(p.x, rect.getMinY(), rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ())
                    	: (sepr == Separator.UP_DOWN 
                    		? new Box(rect.getMinX(), p.y, rect.getMinZ(), rect.getMaxX(), rect.getMaxY(), rect.getMaxZ())
                    		: new Box(rect.getMinX(), rect.getMinY(), p.z, rect.getMaxX(), rect.getMaxY(), rect.getMaxZ()));
        }

        public boolean isRightOrTopOf(Point q) {
            return (sepr == Separator.FORWARD_BACK && p.z > q.z)
            		|| (sepr == Separator.UP_DOWN && p.y > q.y)
                    || (sepr == Separator.LEFT_RIGHT && p.x > q.x);
        }
    }

    /**
     * Construct an empty set of points
     */
    public KdTree() {
        root = null;
        size = 0;
    }

    /**
     * Is the set empty?
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Number of points in the set
     */
    public int size() {
        return size;
    }

    /**
     * Add the point to the set (if it is not already in the set)
     */
    public void insert(double x, double y, double z, V value) {
    	insert(new Point(x, y, z), value);
    }
    
    public void insert(Point p, V value) {
        checkNull(p);
        if (root == null) {
            root = new Node<V>(p, Separator.LEFT_RIGHT, new Box(0, 0, 0, 1, 1, 1), value);
            size++;
            return;
        }

        // find node position for insertion
        Node<V> prev = null;
        Node<V> curr = root;
        do {
            if (curr.p.equals(p)) {
            	Random rand = new Random(1000L);
            	p.x += rand.nextDouble() * 0.001;
            	p.y += rand.nextDouble() * 0.001;
            	p.z += rand.nextDouble() * 0.001;
            }
            prev = curr;
            curr = curr.isRightOrTopOf(p) ? curr.leftBottom : curr.rightTop; 
        } while (curr != null);

        // prepare new node and insert
        if (prev.isRightOrTopOf(p)) {
            prev.leftBottom = new Node<V>(p, prev.nextSepr(), prev.rectLB(), value);
        } else {
            prev.rightTop = new Node<V>(p, prev.nextSepr(), prev.rectRT(), value);
        }
        size++;
    }

    /**
     * Does the set contain point p?
     */
    public boolean contains(Point p) {
        checkNull(p);
        Node<V> node = root;
        while (node != null) {
            if (node.p.equals(p)) {
                return true;
            }
            node = node.isRightOrTopOf(p) ? node.leftBottom : node.rightTop;
        }
        return false;
    }
    
    /**
     * All points that are inside the rectangle
     */
    public Iterable<Point> range(Box rect) {
        checkNull(rect);
        List<Point> results = new LinkedList<>();
        addAll(root, rect, results);
        return results;
    }

    /**
     * Add all points under target node using DFS.
     */
    private void addAll(Node<V> node, Box rect, List<Point> results) {
        if (node == null) {
            return;
        }
        if (rect.contains(node.p)) {
            results.add(node.p);
            addAll(node.leftBottom, rect, results);
            addAll(node.rightTop, rect, results);
            return;
        }
        if (node.isRightOrTopOf(new Point(rect.getMinX(), rect.getMinY(), rect.getMinZ()))) {
            addAll(node.leftBottom, rect, results);
        }
        if (!node.isRightOrTopOf(new Point(rect.getMaxX(), rect.getMaxY(), rect.getMinZ()))) {
            addAll(node.rightTop, rect, results);
        }
    }

    /**
     * A nearest neighbor in the set to point p; null if the set is empty
     */
    public V nearest(double x, double y, double z) {
    	return nearest(new Point(x, y, z));
    }
    
    public V nearest(Point p) {
        checkNull(p);
        return isEmpty() ? null : nearest(p, root, root).value;
    }

    private Node<V> nearest(Point target, Node<V>  closest, Node<V> node) {
        if (node == null) {
            return closest;
        }
        // Recursively search left/bottom or right/top
        // if it could contain a closer point
        double closestDist = closest.p.distance(target);
        if (distanceTo(node.rect, target) < closestDist) {
            double nodeDist = node.p.distance(target);
            if (nodeDist < closestDist) {
                closest = node;
            }
            if (node.isRightOrTopOf(target)) {
                closest = nearest(target, closest, node.leftBottom);
                closest = nearest(target, closest, node.rightTop);
            } else {
                closest = nearest(target, closest, node.rightTop);
                closest = nearest(target, closest, node.leftBottom);
            }
        }
        return closest;
    }

    private double distanceTo(Box rect, Point target) 
    {
    	Point p = new Point(rect.getCenterX(), rect.getCenterY(), rect.getCenterZ());
		return p.distance(target);
	}

	private void checkNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
	
	public static class Point
	{
		double x;
		double y;
		double z;
		
		protected Point(double x, double y, double z) {
			setLocation(x, y, z);
		}
		
		protected void setLocation(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		protected double distance(Point p) {
			return distance(this.x, this.y, this.z, p.x, p.y, p.z);
		}
		
		protected double distanceSq(Point p) {
			return distanceSq(this.x, this.y, this.z, p.x, p.y, p.z);
		}
		
		protected double distance(double x, double y, double z) {
			return distance(this.x, this.y, this.z, x, y, z);
		}
		
		protected double distanceSq(double x, double y, double z) {
			return distanceSq(this.x, this.y, this.z, x, y, z);
		}
		
		protected double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
			return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
		}
		
		protected double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2)
		{
			double xDist = x2 - x1;
			double yDist = y2 - y1;
			double zDist = z2 - z1;
			return xDist * xDist + yDist * yDist + zDist * zDist;
		}
	}
	
	public static class Box
	{
		double minX;
		double minY;
		double minZ;
		double maxX;
		double maxY;
		double maxZ;
		
		protected Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}
		
		protected double getMinX() {
			return minX;
		}
		protected double getMinY() {
			return minY;
		}
		protected double getMinZ() {
			return minZ;
		}
		protected double getMaxX() {
			return maxX;
		}
		protected double getMaxY() {
			return maxY;
		}
		protected double getMaxZ() {
			return minZ;
		}
		
		protected double getCenterX() {
			return getMinX() + Math.abs(getMaxX() - getMinX()) / 2.0;
		}
		protected double getCenterY() {
			return getMinY() + Math.abs(getMaxY() - getMinY()) / 2.0;
		}
		protected double getCenterZ() {
			return getMinZ() + Math.abs(getMaxZ() - getMinZ()) / 2.0;
		}
		
		protected boolean contains(double x, double y, double z) {
			return x >= this.getMinX()
					&& x <= this.getMaxX()
					&& y >= this.getMinY()
					&& y <= this.getMaxY()
					&& z >= this.getMinZ()
					&& z <= this.getMaxZ();
		}
		
		protected boolean contains(Point p) {
			return contains(p.x, p.y, p.z);
		}
	}
}
