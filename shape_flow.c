#include <stdio.h>
#include <math.h>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

#define MAX_VERTICES 8
#define EPS 1e-6

/* 도메인 모델링: 도형 유형 및 사각형 세부 유형 */
typedef enum {
    SHAPE_CIRCLE = 1,
    SHAPE_TRIANGLE = 2,
    SHAPE_QUADRILATERAL = 3,
    SHAPE_REGULAR_POLYGON_BY_SIDE = 4
} ShapeType;

typedef enum {
    QUAD_GENERAL,
    QUAD_PARALLELOGRAM,
    QUAD_TRAPEZOID
} QuadrilateralKind;

/* 요구사항 매핑
 * R1: 도형 선택 및 해당 도형에 맞는 입력
 *     - main: 메뉴 출력 및 사용자 선택 처리
 *     - handle_circle / handle_triangle / handle_quadrilateral / handle_regular_polygon_side
 *
 * R2: 좌표/반지름/변 길이 입력 처리
 *     - read_int, read_double, read_point
 *
 * R3: 도형 성립 불가능한 경우 검출
 *     - are_collinear, is_valid_polygon, handle_triangle, handle_quadrilateral
 *
 * R4: 정다각형 판별 기능
 *     - is_regular_polygon, handle_triangle(정삼각형 판별),
 *       handle_quadrilateral(정사각형 계열 판별),
 *       handle_regular_polygon_side(정다각형 공식 사용)
 *
 * R5: 정다각형에서 한 변만 입력했을 때 둘레/면적 계산
 *     - handle_regular_polygon_side
 *
 * R6: 연산 종료 후 y/n으로 재시작/종료 결정
 *     - ask_restart, main(루프 제어)
 */

typedef struct {
    double x;
    double y;
} Point;

int read_int(const char *prompt) {
    int value;
    int read;
    for (;;) {
        if (prompt) {
            printf("%s", prompt);
        }
        read = scanf_s("%d", &value);
        if (read == 1) {
            return value;
        }
        printf("정수를 입력하세요.\n");
        // 잘못된 입력 비우기
        int c;
        while ((c = getchar()) != '\n' && c != EOF) {
        }
    }
}

double read_double(const char *prompt) {
    double value;
    int read;
    for (;;) {
        if (prompt) {
            printf("%s", prompt);
        }
        read = scanf_s("%lf", &value);
        if (read == 1) {
            return value;
        }
        printf("실수를 입력하세요.\n");
        int c;
        while ((c = getchar()) != '\n' && c != EOF) {
        }
    }
}

void read_point(Point *p, int index) {
    for (;;) {
        printf("%d번째 점의 좌표를 입력하세요 (x,y): ", index + 1);
        if (scanf_s("%lf,%lf", &p->x, &p->y) == 2) {
            // 개행 문자 비우기
            int c;
            while ((c = getchar()) != '\n' && c != EOF) {
            }
            return;
        }
        printf("형식은 x,y 입니다. 예: 0,0\n");
        int c;
        while ((c = getchar()) != '\n' && c != EOF) {
        }
    }
}

double distance(Point a, Point b) {
    double dx = a.x - b.x;
    double dy = a.y - b.y;
    return sqrt(dx * dx + dy * dy);
}

double polygon_perimeter(Point *pts, int n) {
    double p = 0.0;
    for (int i = 0; i < n; ++i) {
        p += distance(pts[i], pts[(i + 1) % n]);
    }
    return p;
}

double polygon_area(Point *pts, int n) {
    double sum = 0.0;
    for (int i = 0; i < n; ++i) {
        int j = (i + 1) % n;
        sum += pts[i].x * pts[j].y - pts[j].x * pts[i].y;
    }
    return fabs(sum) * 0.5;
}

static double cross(Point a, Point b, Point c) {
    // (b - a) x (c - a)
    return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
}

/* 선분 교차 검사: P1P2 와 P3P4 가 내부에서 교차하는지 확인
 * (교차 사각형 / 모래시계 모양 검출에 사용) */
static int segments_intersect(Point p1, Point p2, Point p3, Point p4) {
    double c1 = cross(p1, p2, p3);
    double c2 = cross(p1, p2, p4);
    double c3 = cross(p3, p4, p1);
    double c4 = cross(p3, p4, p2);

    return (c1 * c2 < 0.0) && (c3 * c4 < 0.0);
}

static int is_parallel(Point a, Point b, Point c, Point d) {
    // (b - a) 와 (d - c)가 평행인지 확인 (외적 == 0)
    double vx1 = b.x - a.x;
    double vy1 = b.y - a.y;
    double vx2 = d.x - c.x;
    double vy2 = d.y - c.y;
    double cr = vx1 * vy2 - vy1 * vx2;
    return fabs(cr) < EPS;
}

int are_collinear(Point a, Point b, Point c) {
    double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    return fabs(area2) < EPS;
}

int is_valid_polygon(Point *pts, int n) {
    if (n < 3) {
        return 0;
    }
    double area = polygon_area(pts, n);
    return area > EPS;
}

int is_regular_polygon(Point *pts, int n) {
    if (!is_valid_polygon(pts, n)) {
        return 0;
    }

    // 중심(단순 평균) 계산
    Point center = {0.0, 0.0};
    for (int i = 0; i < n; ++i) {
        center.x += pts[i].x;
        center.y += pts[i].y;
    }
    center.x /= n;
    center.y /= n;

    // 모든 점이 중심에서 같은 거리인지 확인
    double r0 = distance(center, pts[0]);
    if (r0 < EPS) {
        return 0;
    }
    for (int i = 1; i < n; ++i) {
        double r = distance(center, pts[i]);
        if (fabs(r - r0) > 1e-3) {
            return 0;
        }
    }

    // 모든 변의 길이가 같은지 확인
    double side0 = distance(pts[0], pts[1]);
    if (side0 < EPS) {
        return 0;
    }
    for (int i = 1; i < n; ++i) {
        double side = distance(pts[i], pts[(i + 1) % n]);
        if (fabs(side - side0) > 1e-3) {
            return 0;
        }
    }
    return 1;
}

void handle_circle(void) {
    double r = read_double("원의 반지름을 입력하세요: ");
    while (r <= 0.0) {
        printf("반지름은 양수여야 합니다. 다시 입력하세요.\n");
        r = read_double("원의 반지름을 입력하세요: ");
    }
    double perimeter = 2.0 * M_PI * r;
    double area = M_PI * r * r;
    printf("원의 둘레: %.4f, 면적: %.4f\n", perimeter, area);
}

/* R1, R2, R3, R4: 삼각형 선택 → 좌표 입력 → 성립 여부 검사 → 정삼각형 여부 판별 */
void handle_triangle(void) {
    Point pts[3];
    for (;;) {
        for (int i = 0; i < 3; ++i) {
            read_point(&pts[i], i);
        }
        if (are_collinear(pts[0], pts[1], pts[2])) {
            printf("세 점이 일직선상에 있어 삼각형이 아닙니다. 다시 입력하세요.\n");
        } else {
            break;
        }
    }
    double perimeter = polygon_perimeter(pts, 3);
    double area = polygon_area(pts, 3);
    printf("삼각형의 둘레: %.4f, 면적: %.4f\n", perimeter, area);

    if (is_regular_polygon(pts, 3)) {
        printf("이 도형은 정삼각형(정다각형)입니다.\n");
    } else {
        printf("이 도형은 일반 삼각형입니다.\n");
    }
}

/* R1, R2, R3, R4: 사각형 선택 → 좌표 입력 → 성립 여부 검사 →
 * 평행사변형/사다리꼴/일반 사각형 분류 및 면적 공식 선택 */
void handle_quadrilateral(void) {
    Point pts[4];
    for (;;) {
        for (int i = 0; i < 4; ++i) {
            read_point(&pts[i], i);
        }
        if (!is_valid_polygon(pts, 4)) {
            printf("도형이 성립하지 않습니다. 다시 입력하세요.\n");
            continue;
        }

        // 교차 사각형(모래시계 형태) 검출: 대각선이 아닌 변끼리의 교차 여부 확인
        Point A = pts[0], B = pts[1], C = pts[2], D = pts[3];
        if (segments_intersect(A, B, C, D) || segments_intersect(B, C, D, A)) {
            printf("변이 서로 교차하여 유효한 사각형이 아닙니다. 점의 순서를 다시 입력하세요.\n");
            continue;
        }

        break;
    }

    // 공통 둘레
    double perimeter = polygon_perimeter(pts, 4);

    // 변 벡터: AB, BC, CD, DA
    Point A = pts[0], B = pts[1], C = pts[2], D = pts[3];
    int ab_cd_parallel = is_parallel(A, B, C, D);
    int bc_da_parallel = is_parallel(B, C, D, A);

    QuadrilateralKind kind;
    double area;

    if (ab_cd_parallel && bc_da_parallel) {
        kind = QUAD_PARALLELOGRAM;
        // 평행사변형: |AB x AD| 사용 (두 변의 외적 크기)
        area = fabs(cross(A, B, D));
    } else if (ab_cd_parallel || bc_da_parallel) {
        kind = QUAD_TRAPEZOID;
        // 사다리꼴: 한 쌍의 대변만 평행
        if (ab_cd_parallel) {
            // AB, CD 가 평행인 경우: 높이는 선 AB 에서 점 C 까지의 거리
            double base1 = distance(A, B);
            double base2 = distance(C, D);
            double h = fabs(cross(A, B, C)) / base1;
            area = (base1 + base2) * h * 0.5;
        } else {
            // BC, DA 가 평행인 경우
            double base1 = distance(B, C);
            double base2 = distance(D, A);
            double h = fabs(cross(B, C, D)) / base1;
            area = (base1 + base2) * h * 0.5;
        }
    } else {
        // 일반 사각형: 신발끈 공식 그대로 사용
        kind = QUAD_GENERAL;
        area = polygon_area(pts, 4);
    }

    switch (kind) {
    case QUAD_PARALLELOGRAM:
        printf("도형 분류: 평행사변형\n");
        break;
    case QUAD_TRAPEZOID:
        printf("도형 분류: 사다리꼴\n");
        break;
    case QUAD_GENERAL:
    default:
        printf("도형 분류: 일반 사각형\n");
        break;
    }

    printf("사각형의 둘레: %.4f, 면적: %.4f\n", perimeter, area);
}

/* R1, R2, R4, R5: 정다각형(한 변 입력) 선택 → 변 개수/길이 입력 →
 * 정다각형 공식을 사용해 둘레/면적 계산 */
void handle_regular_polygon_side(void) {
    int n;
    for (;;) {
        n = read_int("정다각형의 변의 개수(3~8)를 입력하세요: ");
        if (n >= 3 && n <= MAX_VERTICES) {
            break;
        }
        printf("3 이상 8 이하의 값을 입력하세요.\n");
    }
    double s = read_double("한 변의 길이를 입력하세요: ");
    while (s <= 0.0) {
        printf("변의 길이는 양수여야 합니다. 다시 입력하세요.\n");
        s = read_double("한 변의 길이를 입력하세요: ");
    }

    double perimeter = n * s;
    double area = (n * s * s) / (4.0 * tan(M_PI / n));
    printf("%d각형(정다각형)의 둘레: %.4f, 면적: %.4f\n", n, perimeter, area);
}

int ask_restart(void) {
    char ch;
    for (;;) {
        printf("다시 계산하시겠습니까? (y/n): ");
        int read = scanf_s(" %c", &ch, 1);
        if (read == 1) {
            if (ch == 'y' || ch == 'Y') {
                return 1;
            }
            if (ch == 'n' || ch == 'N') {
                return 0;
            }
        }
        printf("y 또는 n을 입력하세요.\n");
        int c;
        while ((c = getchar()) != '\n' && c != EOF) {
        }
    }
}

/* R1, R6: 도형 메뉴 제공 및 프로그램 전체 흐름 제어 */
int main(void) {
    for (;;) {
        printf("===== 도형 계산기 (ShapeFlow) =====\n");
        printf("%d. 원\n", SHAPE_CIRCLE);
        printf("%d. 삼각형\n", SHAPE_TRIANGLE);
        printf("%d. 사각형\n", SHAPE_QUADRILATERAL);
        printf("%d. 정다각형 (한 변 길이로 입력)\n", SHAPE_REGULAR_POLYGON_BY_SIDE);

        int choice = read_int("원하는 도형의 번호를 입력하세요: ");
        ShapeType shape = (ShapeType)choice;

        switch (shape) {
        case SHAPE_CIRCLE:
            handle_circle();
            break;
        case SHAPE_TRIANGLE:
            handle_triangle();
            break;
        case SHAPE_QUADRILATERAL:
            handle_quadrilateral();
            break;
        case SHAPE_REGULAR_POLYGON_BY_SIDE:
            handle_regular_polygon_side();
            break;
        default:
            printf("잘못된 선택입니다. 1~4 중에서 선택하세요.\n");
            continue;
        }

        if (!ask_restart()) {
            printf("프로그램을 종료합니다.\n");
            break;
        }
    }

    return 0;
}

