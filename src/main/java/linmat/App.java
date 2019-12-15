package linmat;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static linmat.Alphabet.BB;
import static linmat.Alphabet.BB2;
import static linmat.Alphabet.BS;
import static linmat.Alphabet.BS2;
import static linmat.Alphabet.FIVE;
import static linmat.Alphabet.ONE;
import static linmat.Alphabet.R;
import static linmat.Alphabet.TWO;
import static linmat.Utils.loadAccepts;
import static linmat.Utils.loadInputs;
import static linmat.Utils.loadMovesTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

enum Alphabet {
  ONE("Insert 1 PLN"),
  TWO("Insert 2 PLN"),
  FIVE("Insert 5 PLN"),
  BB("Buy ticket for swimming pool"),
  BB2("Buy ticket for swimming pool for 2 hours"),
  BS("Buy ticket for sauna"),
  BS2("Buy ticket for sauna for 2 hours"),
  R("Revert and get all inserted money");

  final String description;

  Alphabet(String description) {
    this.description = description;
  }
}

class DFA {
  final Map<String, Map<Alphabet, String>> movesTable;
  final Set<String> acceptanceStates;
  private String currentState;

  DFA(Map<String, Map<Alphabet, String>> movesTable,
      Set<String> acceptanceStates,
      String startState) {
    this.movesTable = movesTable;
    this.acceptanceStates = acceptanceStates;
    this.currentState = startState;
  }

  void move(Alphabet input) {
    currentState = movesTable.get(currentState).get(input);
  }

  String getCurrentState() {
    return currentState;
  }

  boolean isAccepted() {
    return acceptanceStates.contains(currentState);
  }
}

public class App {

  public static void main(String[] args) throws Exception {
    Map<String, Map<Alphabet, String>> table = loadMovesTable(args[0]);
    List<Alphabet> inputs = loadInputs(args[1]);
    Set<String> accepts = loadAccepts(args[2]);
    String startState = args[3];

    DFA dfa = new DFA(table, accepts, startState);
    inputs.forEach(in -> {
      dfa.move(in);
      System.out.println(dfa.getCurrentState());
    });
    System.out.println("VALID: " + dfa.isAccepted());
  }
}

class Utils {
  static Map<String, Map<Alphabet, String>> loadMovesTable(String path) throws Exception {
    Scanner tableScanner = new Scanner(new File(path));
    Map<String, Map<Alphabet, String>> table = new HashMap<>();
    while (tableScanner.hasNext()) {
      String[] csv = tableScanner.nextLine().split(",");
      table.put(csv[0], ofEntries(
          entry(ONE, csv[1]), entry(TWO, csv[2]), entry(FIVE, csv[3]), entry(R, csv[4]),
          entry(BB, csv[5]), entry(BB2, csv[6]), entry(BS, csv[7]), entry(BS2, csv[8])));
    }
    tableScanner.close();
    return table;
  }

  static List<Alphabet> loadInputs(String path) throws Exception {
    Scanner inputScanner = new Scanner(new File(path));
    List<Alphabet> inputs = new ArrayList<>();
    while (inputScanner.hasNext()) {
      inputs = Arrays.stream(inputScanner.nextLine().split(","))
          .map(Alphabet::valueOf)
          .collect(Collectors.toList());
    }
    inputScanner.close();
    return inputs;
  }

  static Set<String> loadAccepts(String path) throws Exception {
    Scanner scanner = new Scanner(new File(path));
    Set<String> accepts = new HashSet<>();
    while (scanner.hasNext()) {
      accepts = Arrays.stream(scanner.nextLine().split(","))
          .collect(Collectors.toSet());
    }
    scanner.close();
    return accepts;
  }

  static String dfaToString(DFA dfa) {
    StringBuilder moves = new StringBuilder("\nmoves:\n");
    StringBuilder states = new StringBuilder("\nstates:\n");
    StringBuilder acceptance = new StringBuilder("\nacceptance:\n");
    StringBuilder alphabet = new StringBuilder("\nalphabet:\n");

    for (var wrd : Alphabet.values()) {
      alphabet.append(String.format("\"%s\",\n", wrd));
    }

    for (var acc : dfa.acceptanceStates) {
      acceptance.append(String.format("\"%s\",\n", acc));
    }

    for (var row : dfa.movesTable.entrySet()) {
      states.append(String.format("\"%s\",\n", row.getKey()));
      for (var entry : row.getValue().entrySet()) {
        moves.append(String.format("[\"%s\", \"%s\", \"%s\"],\n",
            row.getKey(), entry.getKey(), entry.getValue()));
      }
    }

    return alphabet.toString() + states.toString() + acceptance.toString() + moves.toString();
  }
}