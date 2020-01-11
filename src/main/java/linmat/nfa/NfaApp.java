package linmat.nfa;

import static java.util.Map.entry;
import static linmat.nfa.Utils.nfaToString;
import static linmat.nfa.Utils.loadMovesTable;
import static linmat.nfa.Utils.loadSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class NFA {
  final Map<String, Map<String, Set<String>>> movesTable;
  final Set<String> acceptanceStates;
  final Set<String> states;
  final Set<String> startStates;

  private final List<String> inputsLog;
  private final List<String> statesLog;
  private Set<String> currentStates;

  NFA(Map<String, Map<String, Set<String>>> movesTable,
      Set<String> states,
      Set<String> acceptanceStates,
      Set<String> startStates) {
    this.movesTable = movesTable;
    this.acceptanceStates = acceptanceStates;
    this.startStates = startStates;
    this.currentStates = startStates;
    this.states = states;
    this.inputsLog = new ArrayList<>();
    this.statesLog = new ArrayList<>();
    this.statesLog.addAll(startStates);
  }

  void move(String input) {
      currentStates = currentStates.stream()
          .filter(state -> movesTable.get(state) != null)
          .filter(state -> movesTable.get(state).get(input) != null)
          .flatMap(state -> movesTable.get(state).get(input).stream())
          .collect(Collectors.toSet());

      this.statesLog.add(Arrays.toString(currentStates.toArray()));
      this.inputsLog.add(input);
  }

  boolean isAccepted() {
    return currentStates.stream().anyMatch(acceptanceStates::contains);
  }

  Set<String> getCurrentStates() {
    return currentStates;
  }

  String getLog() {
    StringBuilder log = new StringBuilder();
    for (int i = 0; i < inputsLog.size(); i++) {
      log.append(statesLog.get(i));
      log.append("---");
      log.append(inputsLog.get(i));
      log.append("--->");
    }
    return log.toString();
  }
}

public class NfaApp {

  public static void main(String[] args) throws Exception {
    Map<String, Map<String, Set<String>>> table = loadMovesTable("src/main/resources/nfa/nfa.txt");
    Set<String> states = loadSet("src/main/resources/nfa/states.txt");
    Set<String> accepts = loadSet("src/main/resources/nfa/accept.txt");
    Set<String> startStates = Set.of("q0");

    NFA nfa = new NFA(table, states, accepts, startStates);
    System.out.println(nfaToString(nfa));

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      nfa.move(scanner.next());
      System.out.println("Current states: " + nfa.getCurrentStates());
      System.out.println("Available input: 0, 1, 2, 3, 4, a, b, c, d, e");
      System.out.println("Log: " + nfa.getLog());
      System.out.println("Valid: " + nfa.isAccepted());
    }
    scanner.close();
  }
}

class Utils {
  static Map<String, Map<String, Set<String>>> loadMovesTable(String path) throws Exception {
    Scanner scanner = new Scanner(new File(path));
    Map<String, Map<String, Set<String>>> table = new HashMap<>();
    while (scanner.hasNext()) {
      String[] csv = scanner.nextLine().split(",");
      Map<String, Set<String>> entries = Stream.of(
          entry("0", csv[1]), entry("1", csv[2]), entry("2", csv[3]), entry("3", csv[4]),
          entry("4", csv[5]), entry("a", csv[6]), entry("b", csv[7]), entry("c", csv[8]),
          entry("d", csv[9]), entry("e", csv[10]))
          .filter(entry -> !entry.getValue().equals("-"))
          .collect(Collectors.toMap(
              Entry::getKey,
              $ -> Set.of($.getValue().split(";"))
          ));
      table.put(csv[0], entries);
    }
    scanner.close();
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

  static String nfaToString(NFA nfa) {
    StringBuilder moves = new StringBuilder("\"transitions\": [");
    StringBuilder states = new StringBuilder("\"states\": [");
    StringBuilder acceptance = new StringBuilder("\"accepting_states\": [");
    StringBuilder alphabet = new StringBuilder("{\"alphabet\": [");
    StringBuilder initialStates = new StringBuilder("\"initial_states\": [");

    for (var acc : nfa.startStates) {
      initialStates.append(String.format("\"%s\",", acc));
    }
    initialStates.append("],");

    for (var wrd : new String[]{"0", "1", "2", "3", "4", "a", "b", "c", "d", "e"}) {
      alphabet.append(String.format("\"%s\",", wrd));
    }
    alphabet.append("],");

    for (var state : nfa.states) {
      states.append(String.format("\"%s\",", state));
    }
    states.append("],");

    for (var acc : nfa.acceptanceStates) {
      acceptance.append(String.format("\"%s\",", acc));
    }
    acceptance.append("],");


    for (var row : nfa.movesTable.entrySet()) {
      Map<String, String> destinations = new HashMap<>();

      for (var entry : row.getValue().entrySet()) {
        for (var val : entry.getValue()) {
          if (destinations.containsKey(val)) {
            destinations
                .put(val, destinations.get(val) + "," + entry.getKey());
          } else {
            destinations.put(val, entry.getKey());
          }
        }
      }

      for (var entry : row.getValue().entrySet()) {
        for (var val : entry.getValue()) {
          moves.append(String.format("[\"%s\", \"%s\", \"%s\"],",
              row.getKey(), destinations.get(val), val));
        }
      }
    }
    moves.append("]}");

    return alphabet.toString()
        + states.toString()
        + initialStates.toString()
        + acceptance.toString()
        + moves.toString();
  }
}