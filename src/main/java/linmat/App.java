package linmat;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static linmat.UserInput.*;
import static java.util.Map.entry;

enum UserInput {
    ONE,
    TWO,
    FIVE,
    BB,
    BB2,
    BS,
    BS2,
    R,
}

class DFA {
    private final Map<String, Map<UserInput, String>> movesTable;

    private String currentState;
    private boolean finished;

    DFA(Map<String, Map<UserInput, String>> movesTable, String startState) {
        this.movesTable = movesTable;
        this.currentState = startState;
        this.finished = true;
    }

    void move(UserInput input) {
        Optional.ofNullable(movesTable.get(currentState).get(input))
                .ifPresentOrElse(state -> currentState = state, () -> finished = true);
    }

    String getCurrentState() {
        return currentState;
    }

    boolean isFinished() {
        return finished;
    }
}

public class App {


    private static final Map<String, Map<UserInput, String>> table = Map.ofEntries(
            entry("q0", Map.ofEntries(entry(ONE, "q1"), entry(TWO, "q2"), entry(FIVE, "q5"),
                    entry(R, "r0"), entry(BB, "q0"), entry(BB2, "q0"), entry(BS, "q0"), entry(BS2, "q0"))),
            entry("q1", Map.ofEntries(entry(ONE, "q2"), entry(TWO, "q3"), entry(FIVE, "q6"),
                    entry(R, "r1"), entry(BB, "q1"), entry(BB2, "q1"), entry(BS, "q1"), entry(BS2, "q1"))),
            entry("q2", Map.ofEntries(entry(ONE, "q3"), entry(TWO, "q4"), entry(FIVE, "q7"),
                    entry(R, "r2"), entry(BB, "q2"), entry(BB2, "q2"), entry(BS, "q2"), entry(BS2, "q2"))),
            entry("q3", Map.ofEntries(entry(ONE, "q4"), entry(TWO, "q5"), entry(FIVE, "q8"),
                    entry(R, "r3"), entry(BB, "q3"), entry(BB2, "q3"), entry(BS, "q3"), entry(BS2, "q3"))),
            entry("q4", Map.ofEntries(entry(ONE, "q5"), entry(TWO, "q6"), entry(FIVE, "q9"),
                    entry(R, "r4"), entry(BB, "q4"), entry(BB2, "q4"), entry(BS, "q4"), entry(BS2, "q4"))),
            entry("q5", Map.ofEntries(entry(ONE, "q6"), entry(TWO, "q7"), entry(FIVE, "q10"),
                    entry(R, "r5"), entry(BB, "q5"), entry(BB2, "q5"), entry(BS, "q5"), entry(BS2, "q5"))),
            entry("q6", Map.ofEntries(entry(ONE, "q7"), entry(TWO, "q8"), entry(FIVE, "q11"),
                    entry(R, "r6"), entry(BB, "q6"), entry(BB2, "q6"), entry(BS, "q6"), entry(BS2, "q6"))),
            entry("q7", Map.ofEntries(entry(ONE, "q8"), entry(TWO, "q9"), entry(FIVE, "q12"),
                    entry(R, "r7"), entry(BB, "q7"), entry(BB2, "q7"), entry(BS, "q7"), entry(BS2, "q7"))),
            entry("q8", Map.ofEntries(entry(ONE, "q9"), entry(TWO, "q10"), entry(FIVE, "q13"),
                    entry(R, "r8"), entry(BB, "q8"), entry(BB2, "q8"), entry(BS, "q8"), entry(BS2, "q8"))),
            entry("q9", Map.ofEntries(entry(ONE, "q10"), entry(TWO, "q11"), entry(FIVE, "q14"),
                    entry(R, "r9"), entry(BB, "bbr0"), entry(BB2, "q9"), entry(BS, "q9"), entry(BS2, "q9"))),
            entry("q10", Map.ofEntries(entry(ONE, "q11"), entry(TWO, "q12"), entry(FIVE, "q15"),
                    entry(R, "r10"), entry(BB, "bbr1"), entry(BB2, "q10"), entry(BS, "q10"), entry(BS2, "q10"))),
            entry("q11", Map.ofEntries(entry(ONE, "q12"), entry(TWO, "q13"), entry(FIVE, "q16"),
                    entry(R, "r11"), entry(BB, "bbr2"), entry(BB2, "q11"), entry(BS, "q11"), entry(BS2, "q11"))),
            entry("q12", Map.ofEntries(entry(ONE, "q13"), entry(TWO, "q14"), entry(FIVE, "q17"),
                    entry(R, "r12"), entry(BB, "bbr3"), entry(BB2, "q12"), entry(BS, "bsr0"), entry(BS2, "q12"))),
            entry("q13", Map.ofEntries(entry(ONE, "q14"), entry(TWO, "q15"), entry(FIVE, "q18"),
                    entry(R, "r13"), entry(BB, "bbr4"), entry(BB2, "q13"), entry(BS, "bsr1"), entry(BS2, "q13"))),
            entry("q14", Map.ofEntries(entry(ONE, "q15"), entry(TWO, "q16"), entry(FIVE, "q19"),
                    entry(R, "r14"), entry(BB, "bbr5"), entry(BB2, "q14"), entry(BS, "bsr2"), entry(BS2, "q14"))),
            entry("q15", Map.ofEntries(entry(ONE, "q16"), entry(TWO, "q17"), entry(FIVE, "q20"),
                    entry(R, "r15"), entry(BB, "bbr6"), entry(BB2, "bb2r0"), entry(BS, "bsr3"), entry(BS2, "q15"))),
            entry("q16", Map.ofEntries(entry(ONE, "q17"), entry(TWO, "q18"), entry(FIVE, "q21"),
                    entry(R, "r16"), entry(BB, "bbr7"), entry(BB2, "bb2r1"), entry(BS, "bsr4"), entry(BS2, "q16"))),
            entry("q17", Map.ofEntries(entry(ONE, "q18"), entry(TWO, "q19"), entry(FIVE, "q22"),
                    entry(R, "r17"), entry(BB, "bbr8"), entry(BB2, "bb2r2"), entry(BS, "bsr5"), entry(BS2, "q17"))),
            entry("q18", Map.ofEntries(entry(ONE, "q19"), entry(TWO, "q20"), entry(FIVE, "q23"),
                    entry(R, "r18"), entry(BB, "bbr9"), entry(BB2, "bb2r3"), entry(BS, "bsr6"), entry(BS2, "q18"))),
            entry("q19", Map.ofEntries(entry(ONE, "q20"), entry(TWO, "q21"), entry(FIVE, "q24"),
                    entry(R, "r19"), entry(BB, "bbr10"), entry(BB2, "bb2r4"), entry(BS, "bsr7"), entry(BS2, "q19"))),
            entry("q20", Map.ofEntries(entry(ONE, "q20"), entry(TWO, "q20"), entry(FIVE, "q20"),
                    entry(R, "r20"), entry(BB, "bbr11"), entry(BB2, "bb2r5"), entry(BS, "bsr8"), entry(BS2, "bs2r0"))),
            entry("q21", Map.ofEntries(entry(ONE, "q21"), entry(TWO, "q21"), entry(FIVE, "q21"),
                    entry(R, "r21"), entry(BB, "bbr12"), entry(BB2, "bb2r6"), entry(BS, "bsr9"), entry(BS2, "bs2r1"))),
            entry("q22", Map.ofEntries(entry(ONE, "q22"), entry(TWO, "q22"), entry(FIVE, "q22"),
                    entry(R, "r22"), entry(BB, "bbr13"), entry(BB2, "bb2r7"), entry(BS, "bsr10"), entry(BS2, "bs2r2"))),
            entry("q23", Map.ofEntries(entry(ONE, "q23"), entry(TWO, "q23"), entry(FIVE, "q23"),
                    entry(R, "r23"), entry(BB, "bbr14"), entry(BB2, "bb2r8"), entry(BS, "bsr11"), entry(BS2, "bs2r3"))),
            entry("q24", Map.ofEntries(entry(ONE, "q24"), entry(TWO, "q24"), entry(FIVE, "q24"),
                    entry(R, "r24"), entry(BB, "bbr15"), entry(BB2, "bb2r9"), entry(BS, "bsr12"), entry(BS2, "bs2r4")))
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DFA dfa = new DFA(null, "q0");

        while (!dfa.isFinished()) {
            dfa.move(UserInput.valueOf(scanner.next()));
        }

        System.out.println(dfa.getCurrentState());
    }
}
