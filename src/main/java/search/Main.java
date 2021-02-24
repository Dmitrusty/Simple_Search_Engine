package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        /* ********          Upload data to be searched          **********/
        String[] persons = getInputsFromFile(args);

        /* ********            Create Inverted Index            **********/
        Map<String, HashSet<Integer>> invertedIndex = new HashMap<>();
        for (int i = 0; i < persons.length; i++) {
            for (String word : persons[i].toLowerCase().split(" ")) {
                HashSet<Integer> buffer = new HashSet<>();

                if (invertedIndex.containsKey(word)) {
                    buffer = invertedIndex.get(word);
                }

                buffer.add(i);
                invertedIndex.put(word, buffer);
            }
        }

        /* ********                 Menu Options                **********/
        Finder finder = new Finder();

        MAIN_MENU:
        while (true) {
            System.out.print("\n=== Menu ===\n" +
                    "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit\n");


            switch (getInputInt(scanner)) {
                case 1: // 1. Find a person
                    selectStrategy(finder, scanner);

                    System.out.print("\nEnter a name or email to search all suitable people.\n");
                    String[] targetWords = getInputString(scanner).trim().toLowerCase().split("\\s+");

                    StringBuilder result = new StringBuilder();
                    for (int index : finder.find(persons, targetWords, invertedIndex)) {
                        result.append(persons[index]).append("\n");
                    }

                    System.out.println((result.length() != 0) ? result.toString() : "No matching people found.\n");
                    break;
                case 2: // 2. Print all people
                    PrintAllPeople(persons);
                    break;
                case 0: // 0. Exit
                    System.out.println("\nBye!\n");
                    break MAIN_MENU;
                default:
                    System.out.println("\nIncorrect option! Try again.");
            }
        }
    }

    private static String[] getInputsFromFile(String[] args) {
        String pathToFile;

        if (args[0].equals("--data")) {
            pathToFile = args[1];
        } else {
            System.out.println("No valid command line arguments!");
            return new String[0];
        }

        List<String> result = new ArrayList<>();
        try (Scanner ignored = new Scanner(Path.of(pathToFile))) {
            result = Files.readAllLines(Paths.get(pathToFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toArray(new String[0]);
    }

    private static int getInputInt(Scanner scanner) {
        int result = -1;
        try {
            result = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getInputString(Scanner scanner) {
        String result = "";
        try {
            result = scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void selectStrategy(Finder finder, Scanner scanner) {
        STRATEGY_INPUT:
        while (true) {
            System.out.print("\nSelect a matching strategy: ALL, ANY, NONE\n");
            switch (getInputString(scanner).trim().toUpperCase()) {
                case "ALL":
                    finder.setMethod(new AllSearchingMethod());
                    break STRATEGY_INPUT;
                case "ANY":
                    finder.setMethod(new AnySearchingMethod());
                    break STRATEGY_INPUT;
                case "NONE":
                    finder.setMethod(new NoneSearchingMethod());
                    break STRATEGY_INPUT;
                default:
                    System.out.println("\nIncorrect option! Try again.");
            }
        }
    }

    private static void PrintAllPeople(String[] persons) {
        System.out.print("\n=== List of people ===\n");

        for (String person : persons) {
            System.out.println(person);
        }
    }
}
