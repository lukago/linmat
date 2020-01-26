package linmat.rpn;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

class Rpn {
    private Deque<Character> stack = new LinkedList<>();

    public String calc(String input) {
        StringBuilder outQueue = new StringBuilder();
        for (char currentChar : input.toCharArray()) {
            switch (currentChar) {
                case '+':
                case '-': {
                    while (!stack.isEmpty() && (stack.peek() == '*' || stack.peek() == '/')) {
                        outQueue.append(" ");
                        outQueue.append(stack.pop());
                    }
                    outQueue.append(" ");
                    stack.push(currentChar);
                    break;
                }
                case '*':
                case '/': {
                    outQueue.append(" ");
                    stack.push(currentChar);
                    break;
                }
                case '(': {
                    stack.push(currentChar);
                    break;
                }
                case ')': {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        outQueue.append(" ");
                        outQueue.append(stack.pop());
                    }
                    stack.pop();
                    break;
                }
                default: {
                    outQueue.append(currentChar);
                    break;
                }
            }
        }

        while (!stack.isEmpty()) {
            outQueue.append(" ").append(stack.pop());
        }

        return outQueue.toString();
    }
}


public class RpnApp {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println(new Rpn().calc(scanner.next()));
        }
        scanner.close();
    }
}

