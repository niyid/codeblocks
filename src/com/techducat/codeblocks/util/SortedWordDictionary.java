package com.techducat.codeblocks.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * data structure to store the dictionary of words with sorted keys
 *
 * @author: Parth Parekh
 */
public class SortedWordDictionary {

    // below map will store string with sorted characters as key and all the anagrams of that string as value
    private final Map<String, Set<String>> sortedStringMap = new TreeMap<>();
    private boolean isDictionaryLoaded = false;

    /*
      * loads the words from wordlist file into map; it assumes the wordlist file contains words delimited by newline
      * i.e. \n
      *
      * @param dictFileName absolute file path of the wordlist (assuming it's in the classpath)
      */
    public void loadDictionary(String dictFileName) throws IOException {

        loadDictionaryWithSubsets(dictFileName, null, 0);
    }

    /*
      * loads only the words that are subsets of wordString from wordlist file into map;
      * it assumes the wordlist file contains words delimited by newline i.e. \n
      *
      * @param dictFileName absolute file path of the wordlist (assuming it's in the classpath)
      *
      * @param wordString string to check for subsets
      *
      * @param minWordSize minimum word size to load from dictionary
      */
    public void loadDictionaryWithSubsets(String dictFileName, String wordString,
                                          int minWordSize) throws IOException {

        if (dictFileName == null || dictFileName.isEmpty()) {
            throw new IllegalArgumentException("file path invalid");
        }

        try {
            InputStream stream = getClass().getResourceAsStream(dictFileName);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String word;
                while ((word = reader.readLine()) != null) {
                    assert word != null;
                    word = word.trim().toLowerCase();
                    String sortedWord = AnagramSolverHelper.sortWord(word);
                    if (sortedWord == null
                            || sortedWord.isEmpty()
                            || (wordString != null && !wordString.isEmpty() && (sortedWord
                                    .length() < minWordSize || !AnagramSolverHelper
                                            .isSubset(sortedWord.toCharArray(), wordString
                                                    .replaceAll("\\s", "").toLowerCase()
                                                    .toCharArray())))) {
                        // don't add the word to dictionary if word is empty or if
                        // word from word-list is not a subset of wordString or word
                        // is less than minWordSize
                        continue;
                    }
                    Set<String> wordSet = sortedStringMap.get(sortedWord);
                    if (wordSet != null) {
                        // add word to the existing wordset
                        wordSet.add(word);
                    } else {
                        wordSet = new TreeSet<>();
                        wordSet.add(word);
                        sortedStringMap.put(sortedWord, wordSet);
                    }
                }
            }
            isDictionaryLoaded = true;
        } catch (IOException ioException) {
            throw ioException;
        }
    }

    /*
      * adds word to dictionary
      *
      * @param wordString adds wordString to current dictionary
      *
      * @return true if the word is successfully added, false otherwise
      */
    public boolean addWord(String wordString) {

        if (wordString.isEmpty()) {
            return false;
        }

        String sortedWord = AnagramSolverHelper.sortWord(wordString);
        Set<String> wordSet = sortedStringMap.get(sortedWord);
        if (wordSet != null) {
            // add word to the existing words set
            wordSet.add(wordString);
        } else {
            // add create new words set
            wordSet = new TreeSet<>();
            wordSet.add(wordString);
            sortedStringMap.put(sortedWord, wordSet);
        }

        return true;
    }

    /*
      * finds all the anagrams of the word in the dictionary
      *
      * @param wordString word for which anagrams are to be found
      *
      * @return set of single word anagrams for given string
      */
    public Set<String> findSingleWordAnagrams(String wordString) {

        if (!isDictionaryLoaded) {
            throw new IllegalStateException("dictionary not loaded");
        } else {

            if (wordString == null || wordString.isEmpty()) {
                throw new IllegalStateException("word string invalid");
            }
            return sortedStringMap
                    .get(AnagramSolverHelper.sortWord(wordString));
        }
    }

    /*
      * get list for all the keys in dictionary
      *
      * @return returns the list of all the keys
      */
    public List<String> getDictionaryKeyList() {
        assert sortedStringMap != null;
        return new ArrayList<>(sortedStringMap.keySet());
    }

    public boolean isDictionaryLoaded() {
        return isDictionaryLoaded;
    }

    @Override
    public String toString() {
        return "isDictionaryLoaded?: " + isDictionaryLoaded + "\nDictionary: " + sortedStringMap;
    }
}
