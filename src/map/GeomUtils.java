package map;

import java.util.*;

public class GeomUtils {

    public static final Comparator<Point> POINT_COMPARATOR_LR = new Comparator<Point>() {

        public int compare(Point p0, Point p1) {

            if ((p0.x > p1.x) || ((p0.x == p1.x) && (p0.y > p1.y)))
                return 1;

            if ((p0.x < p1.x) || ((p0.x == p1.x) && (p0.y < p1.y)))
                return -1;

            return 0;
        }
    };

    public static Polygon convexHull(List<Point> points) {

        Polygon hull = new Polygon();

        if ((points == null) || points.isEmpty())
            return hull;

        int n = points.size();

        if (n < 3) {
            for (Point p : points)
                hull.addVertex(p);
            hull.close();
            return hull;
        }

        // sort the points from left to right

        Collections.sort(points, POINT_COMPARATOR_LR);

        // remove duplicates

        ListIterator<Point> it = points.listIterator();

        Point prev = it.next();

        while (it.hasNext()) {
            Point next = it.next();
            if (prev.equals(next))
                it.remove();
            else
                prev = next;
        }

        // make upper hull in CW order

        LinkedList<Point> halfHull = new LinkedList<Point>();

        it = points.listIterator();

        // add the first two sorted points
        halfHull.add(it.next());
        halfHull.add(it.next());

        while (it.hasNext()) {

            // add a new sorted point
            halfHull.add(it.next());

            // fixup
            while (halfHull.size() > 2) {

                if (rightTurn(halfHull.get(halfHull.size() - 3), halfHull.get(halfHull.size() - 2),
                        halfHull.get(halfHull.size() - 1)))
                    break; // right turn, halfHull is ok

                // left turn; delete the middle of the last three points from
                // halfHull
                // and continue checking
                halfHull.remove(halfHull.size() - 2);
            }
        }

        // store the upper hull in CCW order
        for (it = halfHull.listIterator(halfHull.size()); it.hasPrevious();)
            hull.addVertex(it.previous());

        // make lower hull in CW order

        halfHull.clear();

        it = points.listIterator(points.size());

        // add the first two sorted points
        halfHull.add(it.previous());
        halfHull.add(it.previous());

        while (it.hasPrevious()) {

            // add a new sorted point
            halfHull.add(it.previous());

            // fixup
            while (halfHull.size() > 2) {

                if (rightTurn(halfHull.get(halfHull.size() - 3), halfHull.get(halfHull.size() - 2),
                        halfHull.get(halfHull.size() - 1)))
                    break; // right turn, halfHull is ok

                // left turn; delete the middle of the last three points from
                // halfHull
                // and continue checking
                halfHull.remove(halfHull.size() - 2);
            }
        }

        // remove the first and the last point from lower hull to avoid
        // duplication
        // where the upper and lower hulls meet
        halfHull.removeFirst();
        halfHull.removeLast();

        // store the lower hull in CCW order
        for (it = halfHull.listIterator(halfHull.size()); it.hasPrevious();)
            hull.addVertex(it.previous());

        hull.close();

        return hull;
    }

    /**
     * <p>
     * Check whether three ordered points make a right turn.
     * </p>
     * 
     * <p>
     * This is equivalent to asking if <code>p1</code> lies to the left of the
     * oriented line from <code>p0</code> to <code>p2</code>, which is
     * equivalent to asking if the z component of the cross product of the
     * vector from <code>p0</code> to <code>p2</code> with the vector from
     * <code>p0</code> to <code>p1</code> is positive.
     * </p>
     * 
     * @param p0
     *            the first point
     * @param p1
     *            the second point
     * @param p2
     *            the third point
     * 
     * @return true iff the ordered sequence <code>p0</code>, <code>p1</code>,
     *         <code>p2</code> makes a right turn
     **/
    public static boolean rightTurn(Point p0, Point p1, Point p2) {
        // vector from p0 to p2
        double p02x = p2.x - p0.x;
        double p02y = p2.y - p0.y;

        // vector from p0 to p1
        double p01x = p1.x - p0.x;
        double p01y = p1.y - p0.y;

        // return true iff the z component of the cross product p02 x p01 is
        // positive
        return ((p02x * p01y - p02y * p01x) > 0.0);
    }
}
