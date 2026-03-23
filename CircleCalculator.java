public final class CircleCalculator implements ShapeCalculator {
    @Override
    public void run(ConsoleInput input) {
        double r = input.readDouble("원의 반지름을 입력하세요: ");
        while (r <= 0.0) {
            System.out.println("반지름은 양수여야 합니다. 다시 입력하세요.");
            r = input.readDouble("원의 반지름을 입력하세요: ");
        }
        double perimeter = 2.0 * Math.PI * r;
        double area = Math.PI * r * r;
        System.out.printf("원의 둘레: %.4f, 면적: %.4f%n", perimeter, area);
    }
}
