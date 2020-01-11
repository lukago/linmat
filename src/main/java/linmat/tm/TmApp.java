package linmat.tm;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static linmat.tm.Utils.loadTape;
import static linmat.tm.Utils.loadMovesTable;
import static linmat.tm.Utils.loadSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

class Instruction {

  public Instruction(String[] csv) {
    this.nextState = csv[0];
    this.toWrite = Optional.of(csv[1]).map($ -> $.equals("-") ? null : $);
    this.nextMove = Optional.of(csv[2]).map($ -> $.equals("R") ? 1 : -1);
  }

  String nextState;
  Optional<String> toWrite;
  Optional<Integer> nextMove;
}

class TM {
  private final Map<String, Map<String, Instruction>> movesTable;
  private final Set<String> states;
  private final List<String> tape;
  private final List<String> tapeLog;
  private final List<String> statesLog;
  private final String startState;

  private String currentState;
  private int position;

  TM(Map<String, Map<String, Instruction>> movesTable,
      Set<String> states,
      List<String> tape,
      String startState) {
    this.tape = tape;
    this.movesTable = movesTable;
    this.startState = startState;
    this.currentState = this.startState;
    this.states = states;
    this.tapeLog = new ArrayList<>();
    this.statesLog = new ArrayList<>();
    this.statesLog.add(this.startState);
    this.position = 0;
  }

  void tick() {
    Instruction instruction = movesTable.get(currentState).get(tape.get(position));
    instruction.toWrite.ifPresent(newData -> tape.set(this.position, newData));
    this.position += instruction.nextMove.orElse(0);
    this.currentState = instruction.nextState;

    this.statesLog.add(currentState);
    this.tapeLog.add(tape.get(position));
  }

  String getCurrentState() {
    return currentState;
  }

  boolean finished() {
    return tapeLog.contains("null");
  }

  String getLog() {
    StringBuilder log = new StringBuilder();
    for (int i = 0; i < tapeLog.size(); i++) {
      log.append(statesLog.get(i));
      log.append("---");
      log.append(tapeLog.get(i));
      log.append("--->");
    }
    return log.toString();
  }

  String printTape() {
    StringBuilder ret = new StringBuilder();
    for (int i = 0; i < tape.size(); i++) {
      if (position == i) {
        ret.append(String.format(">%s< ", tape.get(i)));
      } else {
        ret.append(tape.get(i)).append(" ");
      }

    }
    return ret.toString();
  }
}

public class TmApp {

  public static void main(String[] args) throws Exception {
    Map<String, Map<String, Instruction>> table = loadMovesTable("src/main/resources/tm/tm.txt");
    Set<String> states = loadSet("src/main/resources/tm/states.txt");
    List<String> tape = loadTape("src/main/resources/tm/tape.txt");
    String startState = "q0";

    TM tm = new TM(table, states, tape, startState);

    while (!tm.finished()) {
      tm.tick();
      System.out.println("Current state: " + tm.getCurrentState());
      System.out.println("Tape: " + tm.printTape());
    }

    System.out.println("Log: " + tm.getLog());
  }
}

class Utils {
  static Map<String, Map<String, Instruction>> loadMovesTable(String path) throws Exception {
    Scanner tableScanner = new Scanner(new File(path));
    Map<String, Map<String, Instruction>> table = new HashMap<>();
    while (tableScanner.hasNext()) {
      String[] csv = tableScanner.nextLine().split(",");
      table.put(csv[0], ofEntries(
          entry("a", new Instruction(csv[1].split(";"))),
          entry("b", new Instruction(csv[2].split(";")))
      ));
    }
    tableScanner.close();
    return table;
  }

  static Set<String> loadSet(String path) throws Exception {
    Scanner scanner = new Scanner(new File(path));
    Set<String> accepts = new HashSet<>();
    while (scanner.hasNext()) {
      accepts = Arrays.stream(scanner.nextLine().split(",")).collect(Collectors.toSet());
    }
    scanner.close();
    return accepts;
  }

  static List<String> loadTape(String path) throws Exception {
    Scanner scanner = new Scanner(new File(path));
    List<String> accepts = new ArrayList<>();
    while (scanner.hasNext()) {
      accepts = Arrays.stream(scanner.nextLine().split(",")).collect(Collectors.toList());
    }
    scanner.close();
    return accepts;
  }
}