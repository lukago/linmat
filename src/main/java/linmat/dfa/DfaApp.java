package linmat.dfa;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static linmat.dfa.Alphabet.BB;
import static linmat.dfa.Alphabet.BB2;
import static linmat.dfa.Alphabet.BS;
import static linmat.dfa.Alphabet.BS2;
import static linmat.dfa.Alphabet.FIVE;
import static linmat.dfa.Alphabet.ONE;
import static linmat.dfa.Alphabet.R;
import static linmat.dfa.Alphabet.TWO;
import static linmat.dfa.Utils.dfaToString;
import static linmat.dfa.Utils.loadSet;
import static linmat.dfa.Utils.loadMovesTable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

  static Alphabet valueOfStr(String value) {
    switch (value) {
      case "1": return ONE;
      case "2": return TWO;
      case "5": return FIVE;
      default: return valueOf(value);
    }
  }
}

class DFA {
  final Map<String, Map<Alphabet, String>> movesTable;
  final Set<String> acceptanceStates;
  final Set<String> states;
  final String startState;

  private String currentState;

  DFA(Map<String, Map<Alphabet, String>> movesTable,
      Set<String> states,
      Set<String> acceptanceStates,
      String startState) {
    this.movesTable = movesTable;
    this.acceptanceStates = acceptanceStates;
    this.startState = startState;
    this.currentState = startState;
    this.states = states;
  }

  void move(Alphabet input) {
    currentState = movesTable.get(currentState).get(input);
  }

  boolean isAccepted() {
    return acceptanceStates.contains(currentState);
  }

  String getCurrentState() {
    return currentState;
  }
}

public class DfaApp {

  public static void main(String[] args) throws Exception {
    Map<String, Map<Alphabet, String>> table = loadMovesTable(args[0]);
    Set<String> states = loadSet(args[1]);
    Set<String> accepts = loadSet(args[2]);
    String startState = args[3];

    DFA dfa = new DFA(table, states, accepts, startState);
    System.out.println(dfaToString(dfa));

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      dfa.move(Alphabet.valueOfStr(scanner.next()));
      System.out.println("Current state: " + dfa.getCurrentState());
      System.out.println("Available input: " + Arrays.toString(Alphabet.values()) + "\n");
      System.out.println("Accepted: " + dfa.isAccepted());
    }
    scanner.close();
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

  static Set<String> loadSet(String path) throws Exception {
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
    StringBuilder moves = new StringBuilder("\"transitions\": [");
    StringBuilder states = new StringBuilder("\"states\": [");
    StringBuilder acceptance = new StringBuilder("\"accepting_states\": [");
    StringBuilder alphabet = new StringBuilder("{\"alphabet\": [");
    String initialState = "\"initial_state\":\"" + dfa.startState + "\",";

    for (var wrd : Alphabet.values()) {
      alphabet.append(String.format("\"%s\",", wrd));
    }
    alphabet.append("],");

    for (var state : dfa.states) {
      states.append(String.format("\"%s\",", state));
    }
    states.append("],");

    for (var acc : dfa.acceptanceStates) {
      acceptance.append(String.format("\"%s\",", acc));
    }
    acceptance.append("],");


    for (var row : dfa.movesTable.entrySet()) {
      Map<String, String> destinations = new HashMap<>();

      for (var entry : row.getValue().entrySet()) {
        if (destinations.containsKey(entry.getValue())) {
          destinations.put(entry.getValue(), destinations.get(entry.getValue()) + "," + entry.getKey());
        } else {
          destinations.put(entry.getValue(), entry.getKey().toString());
        }
      }

      for (var entry : row.getValue().entrySet()) {
        moves.append(String.format("[\"%s\", \"%s\", \"%s\"],",
            row.getKey(), destinations.get(entry.getValue()), entry.getValue()));
      }
    }
    moves.append("]}");

    return alphabet.toString() + states.toString() + initialState + acceptance.toString() + moves.toString();
  }
}