package lekce206;

import java.util.ArrayDeque;
import java.util.Deque;

public class Zasobnik {
    static void main() {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(10);
        stack.push(20);
        System.out.println(stack.peek());
        stack.pop();
        System.out.println(stack.peek());
    }

}
