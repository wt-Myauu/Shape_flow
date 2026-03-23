/**
 * 메뉴에서 선택하는 도형 유형 (C의 ShapeType enum 대응).
 */
public enum ShapeType {
    CIRCLE(1),
    TRIANGLE(2),
    QUADRILATERAL(3),
    REGULAR_POLYGON_BY_SIDE(4);

    private final int choice;

    ShapeType(int choice) {
        this.choice = choice;
    }

    public int getChoice() {
        return choice;
    }

    public static ShapeType fromChoice(int choice) {
        for (ShapeType t : values()) {
            if (t.choice == choice) {
                return t;
            }
        }
        return null;
    }
}
