package search;

import java.util.HashSet;
import java.util.Map;

public class Finder {
    private SearchMethod method;

    public void setMethod(SearchMethod method) {
        this.method = method;
    }

    public Integer[] find(String[] persons, String[] targetWords, Map<String, HashSet<Integer>> invertedIndex) {
        return this.method.find(persons, targetWords, invertedIndex);
    }
}

interface SearchMethod {
    Integer[] find(String[] persons, String[] targetWords, Map<String, HashSet<Integer>> invertedIndex);
}

class AllSearchingMethod implements SearchMethod {
    @Override
    public Integer[] find(String[] persons, String[] targetWords, Map<String, HashSet<Integer>> invertedIndex) {
        HashSet<Integer> buffer = new HashSet<>();

        for (int i = 0; i < persons.length; i++) {
            buffer.add(i);
        }

        HashSet<Integer> neverIndex = new HashSet<>();
        neverIndex.add(-1);

        for (String word : targetWords) {
            buffer.retainAll(invertedIndex.getOrDefault(word, neverIndex));
        }
        return buffer.toArray(new Integer[0]);
    }
}

class AnySearchingMethod implements SearchMethod {
    @Override
    public Integer[] find(String[] persons, String[] targetWords, Map<String, HashSet<Integer>> invertedIndex) {
        HashSet<Integer> buffer = new HashSet<>();

        HashSet<Integer> neverIndex = new HashSet<>();
        neverIndex.add(-1);

        for (String word : targetWords) {
            for (var index : invertedIndex.getOrDefault(word, neverIndex)) {
                if (index != -1) {
                    buffer.add(index);
                }
            }
        }
        return buffer.toArray(new Integer[0]);
    }
}

class NoneSearchingMethod implements SearchMethod {
    @Override
    public Integer[] find(String[] persons, String[] targetWords, Map<String, HashSet<Integer>> invertedIndex) {
        HashSet<Integer> buffer = new HashSet<>();

        HashSet<Integer> neverIndex = new HashSet<>();
        neverIndex.add(-1);

        for (int i = 0; i < persons.length; i++) {
            buffer.add(i);
        }

        for (String word : targetWords) {
            buffer.removeAll(invertedIndex.getOrDefault(word, neverIndex));
        }

        return buffer.toArray(new Integer[0]);
    }
}
