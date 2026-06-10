package Test_8_6_2026;
import java.lang.System;
import java.util.Deque;
import java.util.Scanner;
public class TerminalniPapousek {
    static void main() {
        Scanner scanner = new java.util.Scanner(System.in);
        Deque<String> stack = new java.util.ArrayDeque<>();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            stack.push(line);
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
