package linmat.parser;
1
import java.util.List;
import java.util.Scanner;

class Parser {
    List<Character> CList = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    List<Character> OList = List.of('*', ':', '+', '-', '^');
    List<Character> firstZ = List.of('(', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    List<Character> firstWp = List.of('*', ':', '+', '-', '^');
    List<Character> firstRp = List.of('.');
    List<Character> firstLp = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    int currentIndex = 0;

    void startAnalyze(String userInput) {
        resolveS(userInput);
    }

    // S ::= W ; Z
    private void resolveS(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        resolveW(userInput);

        if (userInput.charAt(currentIndex) == ';') {
            currentIndex++;
        } else {
            throw new IllegalArgumentException();
        }

        resolveZ(userInput);
    }

    //Z ::= W ; Z | empty
    void resolveZ(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (!firstZ.contains(userInput.charAt(currentIndex))) {
            throw new IllegalArgumentException();
        }

        resolveW(userInput);

        if (userInput.charAt(currentIndex) == ';') {
            currentIndex++;
        } else {
            throw new IllegalArgumentException();
        }

        resolveZ(userInput);
    }

    // W ::= P W '
    void resolveW(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        resolveP(userInput);

        resolveWPrim(userInput);
    }

    // W ' ::= O W | empty
    void resolveWPrim(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (!firstWp.contains(userInput.charAt(currentIndex))) {
            return;
        }

        resolveO(userInput);

        resolveW(userInput);
    }

    // P ::= R | ( W )
    void resolveP(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (userInput.charAt(currentIndex) == '(') {
            currentIndex++;
            resolveW(userInput);
            if (userInput.charAt(currentIndex) == ')') {
                currentIndex++;
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            resolveR(userInput);
        }
    }

    // R ::= L R '
    void resolveR(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        resolveL(userInput);
        resolveRPrim(userInput);
    }

    // R ' ::= . L | empty
    void resolveRPrim(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (!firstRp.contains(userInput.charAt(currentIndex))) {
            return;
        } else {
            currentIndex++;
            resolveL(userInput);
        }
    }

    // L ::= C L '
    void resolveL(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        resolveC(userInput);
        resolveLPrim(userInput);
    }

    // Lp ::= L | empty
    void resolveLPrim(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (!firstLp.contains(userInput.charAt(currentIndex))) {
            return;
        }

        resolveL(userInput);
    }

    // C ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
    void resolveC(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (CList.contains(userInput.charAt(currentIndex))) {
            currentIndex++;
        } else {
            throw new IllegalArgumentException();
        }
    }

    // O ::= * | : | + | - | ^
    void resolveO(String userInput) {
        if (currentIndex > userInput.length() - 1) {
            return;
        }

        if (OList.contains(userInput.charAt(currentIndex))) {
            currentIndex++;
        } else {
            throw new IllegalArgumentException();
        }
    }
}

public class ParserApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            try {
                new Parser().startAnalyze(scanner.next());
                System.out.println("Valid");
            } catch (Exception e) {
                System.out.print("Not valid");
            }
        }
        scanner.close();
    }
}
