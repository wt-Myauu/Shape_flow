import java.util.Scanner;

/**
 * 콘솔 입력 담당 (C의 read_int, read_double, read_point, ask_restart 대응).
 */
public final class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt) {
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (Exception e) {
                System.out.println("정수를 입력하세요.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String line = scanner.nextLine();
            try {
                return Double.parseDouble(line.trim());
            } catch (Exception e) {
                System.out.println("실수를 입력하세요.");
            }
        }
    }

    public Point readPoint(int index0Based) {
        while (true) {
            System.out.printf("%d번째 점의 좌표를 입력하세요 (x,y): ", index0Based + 1);
            String line = scanner.nextLine().trim();
            String[] parts = line.split(",");
            if (parts.length != 2) {
                System.out.println("형식은 x,y 입니다. 예: 0,0");
                continue;
            }
            try {
                double x = Double.parseDouble(parts[0].trim());
                double y = Double.parseDouble(parts[1].trim());
                return new Point(x, y);
            } catch (Exception e) {
                System.out.println("형식은 x,y 입니다. 예: 0,0");
            }
        }
    }

    public boolean askRestart() {
        while (true) {
            System.out.print("다시 계산하시겠습니까? (y/n): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("y 또는 n을 입력하세요.");
                continue;
            }
            char ch = line.charAt(0);
            if (ch == 'y' || ch == 'Y') {
                return true;
            }
            if (ch == 'n' || ch == 'N') {
                return false;
            }
            System.out.println("y 또는 n을 입력하세요.");
        }
    }
}
