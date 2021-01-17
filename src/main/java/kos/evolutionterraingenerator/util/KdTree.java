package kos.evolutionterraingenerator.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

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

    private enum Separator { VERTICAL, HORIZONTAL }
    private Node<V> root;
    private int size;

    private static class Node<V> {

        private final Separator sepr;
        private final Rectangle2D rect;
        private final Point2D p;
        private Node<V> leftBottom;
        private Node<V> rightTop;
        private V value;

        Node(Point2D p, Separator sepr, Rectangle2D rect, V value) {
            this.p = p;
            this.sepr = sepr;
            this.rect = rect;
            this.value = value;
        }

        public Separator nextSepr() {
            return sepr == Separator.VERTICAL ?
                    Separator.HORIZONTAL : Separator.VERTICAL;
        }

        public Rectangle2D rectLB() {
            return sepr == Separator.VERTICAL
                    ? new Rectangle2D.Double(rect.getMinX(), rect.getMinY(), p.getX(), rect.getMaxY())
                    : new Rectangle2D.Double(rect.getMinX(), rect.getMinY(), rect.getMaxX(), p.getY());
        }

        public Rectangle2D rectRT() {
            return sepr == Separator.VERTICAL
                    ? new Rectangle2D.Double(p.getX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY())
                    : new Rectangle2D.Double(rect.getMinX(), p.getY(), rect.getMaxX(), rect.getMaxY());
        }

        public boolean isRightOrTopOf(Point2D q) {
            return (sepr == Separator.HORIZONTAL && p.getY() > q.getY())
                    || (sepr == Separator.VERTICAL && p.getX() > q.getX());
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
    public void insert(Point2D p, V value) {
        checkNull(p);
        if (root == null) {
            root = new Node<V>(p, Separator.VERTICAL, new Rectangle2D.Double(0, 0, 1, 1), value);
            size++;
            return;
        }

        // find node position for insertion
        Node<V> prev = null;
        Node<V> curr = root;
        do {
            if (curr.p.equals(p)) {
                return;
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
    public boolean contains(Point2D p) {
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
     * Draw all points to standard draw
     */
    public void draw() {
        // TODO
    }

    /**
     * All points that are inside the rectangle
     */
    public Iterable<Point2D> range(Rectangle2D rect) {
        checkNull(rect);
        List<Point2D> results = new LinkedList<>();
        addAll(root, rect, results);
        return results;
    }

    /**
     * Add all points under target node using DFS.
     */
    private void addAll(Node<V> node, Rectangle2D rect, List<Point2D> results) {
        if (node == null) {
            return;
        }
        if (rect.contains(node.p)) {
            results.add(node.p);
            addAll(node.leftBottom, rect, results);
            addAll(node.rightTop, rect, results);
            return;
        }
        if (node.isRightOrTopOf(new Point2D.Double(rect.getMinX(), rect.getMinY()))) {
            addAll(node.leftBottom, rect, results);
        }
        if (!node.isRightOrTopOf(new Point2D.Double(rect.getMaxX(), rect.getMaxY()))) {
            addAll(node.rightTop, rect, results);
        }
    }

    /**
     * A nearest neighbor in the set to point p; null if the set is empty
     */
    public V nearest(double x, double y) {
    	return nearest(new Point2D.Double(x, y));
    }
    
    public V nearest(Point2D p) {
        checkNull(p);
        return isEmpty() ? null : nearest(p, root.p, root);
    }

    private V nearest(Point2D target, Point2D closest, Node<V> node) {
        if (node == null) {
            return null;
        }
        // Recursively search left/bottom or right/top
        // if it could contain a closer point
        V value = node.value;
        double closestDist = closest.distance(target);
        if (distanceTo(node.rect, target) < closestDist) {
            double nodeDist = node.p.distance(target);
            if (nodeDist < closestDist) {
                closest = node.p;
            }
            if (node.isRightOrTopOf(target)) {
            	value = nearest(target, closest, node.leftBottom);
            	value = nearest(target, closest, node.rightTop);
            } else {
            	value = nearest(target, closest, node.rightTop);
            	value = nearest(target, closest, node.leftBottom);
            }
            
            if (value == null)
            	value = node.value;
        }
        return value;
    }

    private double distanceTo(Rectangle2D rect, Point2D target) 
    {
    	Point2D p = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
		return p.distance(target);
	}

	private void checkNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}
