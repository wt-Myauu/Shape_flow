public final class TriangleCalculator implements ShapeCalculator {
    @Override
    public void run(ConsoleInput input) {
        Point[] pts = new Point[3];
        while (true) {
            for (int i = 0; i < 3; i++) {
                pts[i] = input.readPoint(i);
            }
            if (GeometryUtils.areCollinear(pts[0], pts[1], pts[2])) {
                System.out.println("세 점이 일직선상에 있어 삼각형이 아닙니다. 다시 입력하세요.");
            } else {
                break;
            }
        }
        double perimeter = GeometryUtils.polygonPerimeter(pts);
        double area = GeometryUtils.polygonArea(pts);
        System.out.printf("삼각형의 둘레: %.4f, 면적: %.4f%n", perimeter, area);

        if (GeometryUtils.isRegularPolygon(pts)) {
            System.out.println("이 도형은 정삼각형(정다각형)입니다.");
        } else {
            System.out.println("이 도형은 일반 삼각형입니다.");
        }
    }
}
