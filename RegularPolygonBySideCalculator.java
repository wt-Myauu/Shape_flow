public final class RegularPolygonBySideCalculator implements ShapeCalculator {
    @Override
    public void run(ConsoleInput input) {
        int n;
        while (true) {
            n = input.readInt("정다각형의 변의 개수(3~8)를 입력하세요: ");
            if (n >= 3 && n <= GeometryUtils.MAX_VERTICES) {
                break;
            }
            System.out.println("3 이상 8 이하의 값을 입력하세요.");
        }
        double s = input.readDouble("한 변의 길이를 입력하세요: ");
        while (s <= 0.0) {
            System.out.println("변의 길이는 양수여야 합니다. 다시 입력하세요.");
            s = input.readDouble("한 변의 길이를 입력하세요: ");
        }

        double perimeter = n * s;
        double area = (n * s * s) / (4.0 * Math.tan(Math.PI / n));
        System.out.printf("%d각형(정다각형)의 둘레: %.4f, 면적: %.4f%n", n, perimeter, area);
    }
}
