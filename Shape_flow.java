import java.util.Scanner;

/**
 * 도형 계산기 애플리케이션 진입점 (C의 main + 메뉴 루프).
 * <p>
 * 요구사항 매핑 (C 주석과 동일):
 * R1: 메뉴 및 도형별 입력 — main, ShapeCalculator 구현체
 * R2: 좌표/실수 입력 — ConsoleInput
 * R3: 성립 불가 검출 — GeometryUtils + Triangle/Quadrilateral 계산기
 * R4: 정다각형 판별 — GeometryUtils.isRegularPolygon 등
 * R5: 변 길이만으로 정다각형 둘레/면적 — RegularPolygonBySideCalculator
 * R6: 재시작 — ConsoleInput.askRestart
 */
public class Shape_flow {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ConsoleInput input = new ConsoleInput(sc);

        while (true) {
            System.out.println("===== 도형 계산기 (ShapeFlow) =====");
            System.out.println(ShapeType.CIRCLE.getChoice() + ". 원");
            System.out.println(ShapeType.TRIANGLE.getChoice() + ". 삼각형");
            System.out.println(ShapeType.QUADRILATERAL.getChoice() + ". 사각형");
            System.out.println(ShapeType.REGULAR_POLYGON_BY_SIDE.getChoice() + ". 정다각형 (한 변 길이로 입력)");

            int choice = input.readInt("원하는 도형의 번호를 입력하세요: ");
            ShapeType shapeType = ShapeType.fromChoice(choice);
            if (shapeType == null) {
                System.out.println("잘못된 선택입니다. 1~4 중에서 선택하세요.");
                continue;
            }

            ShapeCalculator calculator = switch (shapeType) {
                case CIRCLE -> new CircleCalculator();
                case TRIANGLE -> new TriangleCalculator();
                case QUADRILATERAL -> new QuadrilateralCalculator();
                case REGULAR_POLYGON_BY_SIDE -> new RegularPolygonBySideCalculator();
            };

            calculator.run(input);

            if (!input.askRestart()) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }
        }
    }
}
