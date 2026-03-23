public final class QuadrilateralCalculator implements ShapeCalculator {
    @Override
    public void run(ConsoleInput input) {
        Point[] pts = new Point[4];

        while (true) {
            for (int i = 0; i < 4; i++) {
                pts[i] = input.readPoint(i);
            }
            if (GeometryUtils.hasDuplicatePoints(pts)) {
                System.out.println("중복된 점이 있어 사각형이 아닙니다. 다시 입력하세요.");
                continue;
            }
            if (GeometryUtils.quadHasAnyCollinearTriple(pts)) {
                System.out.println("세 점이 일직선상에 있어 사각형이 아닙니다. 다시 입력하세요.");
                continue;
            }
            if (!GeometryUtils.isValidPolygon(pts)) {
                System.out.println("도형이 성립하지 않습니다. 다시 입력하세요.");
                continue;
            }

            Point A = pts[0], B = pts[1], C = pts[2], D = pts[3];
            if (GeometryUtils.segmentsIntersect(A, B, C, D)
                    || GeometryUtils.segmentsIntersect(B, C, D, A)) {
                System.out.println("변이 서로 교차하여 유효한 사각형이 아닙니다. 점의 순서를 다시 입력하세요.");
                continue;
            }
            break;
        }

        double perimeter = GeometryUtils.polygonPerimeter(pts);

        Point A = pts[0], B = pts[1], C = pts[2], D = pts[3];
        boolean abCdParallel = GeometryUtils.isParallel(A, B, C, D);
        boolean bcDaParallel = GeometryUtils.isParallel(B, C, D, A);

        QuadrilateralKind kind;
        double area;

        if (abCdParallel && bcDaParallel) {
            kind = QuadrilateralKind.PARALLELOGRAM;
            area = Math.abs(GeometryUtils.cross(A, B, D));
        } else if (abCdParallel || bcDaParallel) {
            kind = QuadrilateralKind.TRAPEZOID;
            if (abCdParallel) {
                double base1 = GeometryUtils.distance(A, B);
                double base2 = GeometryUtils.distance(C, D);
                double h = Math.abs(GeometryUtils.cross(A, B, C)) / base1;
                area = (base1 + base2) * h * 0.5;
            } else {
                double base1 = GeometryUtils.distance(B, C);
                double base2 = GeometryUtils.distance(D, A);
                double h = Math.abs(GeometryUtils.cross(B, C, D)) / base1;
                area = (base1 + base2) * h * 0.5;
            }
        } else {
            kind = QuadrilateralKind.GENERAL;
            area = GeometryUtils.polygonArea(pts);
        }

        switch (kind) {
            case PARALLELOGRAM -> System.out.println("도형 분류: 평행사변형");
            case TRAPEZOID -> System.out.println("도형 분류: 사다리꼴");
            case GENERAL -> System.out.println("도형 분류: 일반 사각형");
        }

        System.out.printf("사각형의 둘레: %.4f, 면적: %.4f%n", perimeter, area);
    }
}
