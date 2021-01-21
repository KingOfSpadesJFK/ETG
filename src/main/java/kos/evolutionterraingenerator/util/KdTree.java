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
        private final Rectangle rect;
        private final Vec2d p;
        private boolean ranged;
        private Vec2d range;
        private Node<V> leftBottom;
        private Node<V> rightTop;
        private V value;
        
        Node(Vec2d p, Vec2d range, Separator sepr, Rectangle rect, V value) {
            this.p = p;
            this.sepr = sepr;
            this.rect = rect;
            this.value = value;
        	if (range == null) {
        		this.ranged = false;
        	} else {
        		this.ranged = true;
        		this.range = range;
        	}
        }

        public Separator nextSepr() {
            return sepr == Separator.LEFT_RIGHT ? Separator.UP_DOWN : Separator.LEFT_RIGHT;
        }

        public Rectangle rectLB() {
            return sepr == Separator.LEFT_RIGHT
                    ? new Rectangle(rect.minX, rect.minY, p.x, rect.maxY)
                    	: new Rectangle(rect.minX, rect.minY, rect.maxX, p.y);
        }

        public Rectangle rectRT() {
            return sepr == Separator.LEFT_RIGHT
                    ? new Rectangle(p.x, rect.minY, rect.maxX, rect.maxY)
                    	: new Rectangle(rect.minX, p.y, rect.maxX, rect.maxY);
        }

        public boolean isRightOrTopOf(Vec2d p2) {
            return (sepr == Separator.UP_DOWN && p.y > p2.y)
                    || (sepr == Separator.LEFT_RIGHT && p.x > p2.x);
        }
        
        public boolean inRange(Vec2d p2)
        {
        	if (!ranged)
        		return true;
        	double xd = Math.abs(p2.x - p.x);
        	double yd = Math.abs(p2.y - p.y);
        	return xd <= range.x && yd <= range.y;
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
    public void insert(double x, double y, V value) {
    	insert(new Vec2d(x, y), null, value);
    }
    
    public void insert(double x, double y, double r, V value) {
    	insert(new Vec2d(x, y), Double.isNaN(r) ? null : new Vec2d(1, r), value);
    }
    
    public void insert(Vec2d p, Vec2d range, V value) {
        checkNull(p);
        if (root == null) {
            root = new Node<V>(p, range, Separator.LEFT_RIGHT, new Rectangle(0, 0, 1, 1), value);
            size++;
            return;
        }

        // find node position for insertion
        Node<V> prev = null;
        Node<V> curr = root;
        do {
            if (curr.p.equals(p)) {
            	Random rand = new Random(1000L);
            	p.add(rand.nextDouble() * 0.001, rand.nextDouble() * 0.001);
            }
            prev = curr;
            curr = curr.isRightOrTopOf(p) ? curr.leftBottom : curr.rightTop; 
        } while (curr != null);

        // prepare new node and insert
        if (prev.isRightOrTopOf(p)) {
            prev.leftBottom = new Node<V>(p, range, prev.nextSepr(), prev.rectLB(), value);
        } else {
            prev.rightTop = new Node<V>(p, range, prev.nextSepr(), prev.rectRT(), value);
        }
        size++;
    }

    /**
     * Does the set contain point p?
     */
    public boolean contains(Vec2d p) {
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
    public Iterable<Vec2d> range(Rectangle rect) {
        checkNull(rect);
        List<Vec2d> results = new LinkedList<>();
        addAll(root, rect, results);
        return results;
    }

    /**
     * Add all points under target node using DFS.
     */
    private void addAll(Node<V> node, Rectangle rect, List<Vec2d> results) {
        if (node == null) {
            return;
        }
        if (rect.contains(node.p)) {
            results.add(node.p);
            addAll(node.leftBottom, rect, results);
            addAll(node.rightTop, rect, results);
            return;
        }
        if (node.isRightOrTopOf(new Vec2d(rect.minX, rect.minY))) {
            addAll(node.leftBottom, rect, results);
        }
        if (!node.isRightOrTopOf(new Vec2d(rect.maxX, rect.maxY))) {
            addAll(node.rightTop, rect, results);
        }
    }

    /**
     * A nearest neighbor in the set to point p; null if the set is empty
     */
    public V nearest(double x, double y) {
    	return nearest(new Vec2d(x, y));
    }
    
    public V nearest(Vec2d p) {
        checkNull(p);
        return isEmpty() ? null : nearest(p, root, root).value;
    }

    private Node<V> nearest(Vec2d target, Node<V>  closest, Node<V> node) {
        if (node == null) {
            return closest;
        }
        // Recursively search left/bottom or right/top
        // if it could contain a closer point
        double closestDist = closest.p.squaredDistanceTo(target);
        if (node.rect.distanceTo(target) < closestDist) {
            double nodeDist = node.p.squaredDistanceTo(target);
            if (nodeDist < closestDist && node.inRange(target)) {
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

	private void checkNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}
