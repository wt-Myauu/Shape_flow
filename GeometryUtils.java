/**
 * 기하 계산 유틸리티 (C의 distance, polygon_*, cross, segments_intersect 등).
 */
public final class GeometryUtils {
    public static final int MAX_VERTICES = 8;
    public static final double EPS = 1e-6;

    private GeometryUtils() {
    }

    public static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double polygonPerimeter(Point[] pts) {
        int n = pts.length;
        double p = 0.0;
        for (int i = 0; i < n; i++) {
            p += distance(pts[i], pts[(i + 1) % n]);
        }
        return p;
    }

    public static double polygonArea(Point[] pts) {
        int n = pts.length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            sum += pts[i].x * pts[j].y - pts[j].x * pts[i].y;
        }
        return Math.abs(sum) * 0.5;
    }

    /** (b - a) x (c - a) */
    public static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    /** P1P2 와 P3P4 가 내부에서 교차하는지 */
    public static boolean segmentsIntersect(Point p1, Point p2, Point p3, Point p4) {
        double c1 = cross(p1, p2, p3);
        double c2 = cross(p1, p2, p4);
        double c3 = cross(p3, p4, p1);
        double c4 = cross(p3, p4, p2);
        return (c1 * c2 < 0.0) && (c3 * c4 < 0.0);
    }

    public static boolean isParallel(Point a, Point b, Point c, Point d) {
        double vx1 = b.x - a.x;
        double vy1 = b.y - a.y;
        double vx2 = d.x - c.x;
        double vy2 = d.y - c.y;
        double cr = vx1 * vy2 - vy1 * vx2;
        return Math.abs(cr) < EPS;
    }

    public static boolean areCollinear(Point a, Point b, Point c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        return Math.abs(area2) < EPS;
    }

    public static boolean hasDuplicatePoints(Point[] pts) {
        int n = pts.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance(pts[i], pts[j]) < EPS) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean quadHasAnyCollinearTriple(Point[] pts) {
        Point A = pts[0], B = pts[1], C = pts[2], D = pts[3];
        return areCollinear(A, B, C) || areCollinear(A, B, D)
                || areCollinear(A, C, D) || areCollinear(B, C, D);
    }

    public static boolean isValidPolygon(Point[] pts) {
        int n = pts.length;
        if (n < 3) {
            return false;
        }
        double area = polygonArea(pts);
        return area > EPS;
    }

    public static boolean isRegularPolygon(Point[] pts) {
        int n = pts.length;
        if (!isValidPolygon(pts)) {
            return false;
        }

        double cx = 0.0;
        double cy = 0.0;
        for (Point p : pts) {
            cx += p.x;
            cy += p.y;
        }
        cx /= n;
        cy /= n;
        Point center = new Point(cx, cy);

        double r0 = distance(center, pts[0]);
        if (r0 < EPS) {
            return false;
        }
        for (int i = 1; i < n; i++) {
            double r = distance(center, pts[i]);
            if (Math.abs(r - r0) > 1e-3) {
                return false;
            }
        }

        double side0 = distance(pts[0], pts[1]);
        if (side0 < EPS) {
            return false;
        }
        for (int i = 1; i < n; i++) {
            double side = distance(pts[i], pts[(i + 1) % n]);
            if (Math.abs(side - side0) > 1e-3) {
                return false;
            }
        }
        return true;
    }
}
