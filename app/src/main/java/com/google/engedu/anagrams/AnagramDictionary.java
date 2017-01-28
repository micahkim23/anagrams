/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private int wordLength;
    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private HashMap<Integer, ArrayList<String>> sizeToWords;
    private Random random = new Random();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        wordLength = DEFAULT_WORD_LENGTH;
        wordList = new ArrayList<>();
        wordSet = new HashSet<>();
        lettersToWord = new HashMap<>();
        sizeToWords = new HashMap<>();
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            String sorted = sortLetters(word);
            ArrayList<String> stringList = lettersToWord.get(sorted);
            if (stringList != null) {
                stringList.add(word);
            }
            else {
                stringList = new ArrayList<>();
                stringList.add(word);
                lettersToWord.put(sorted, stringList);
            }
            Integer num = word.length();
            ArrayList<String> numStringList = sizeToWords.get(num);
            if (numStringList != null) {
                numStringList.add(word);
            }
            else {
                numStringList = new ArrayList<>();
                numStringList.add(word);
                sizeToWords.put(num, numStringList);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {
        if (wordSet.contains(word) && !word.contains(base)) {
            return true;
        }
        return false;
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String sorted = sortLetters(targetWord);
        for (int i = 0; i < wordList.size(); i++) {
            if (sorted.equals(sortLetters(wordList.get(i)))) {
                result.add(wordList.get(i));
            }
        }
        return result;
    }

    public String sortLetters(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 97; i < 123; i++) {
            char lower = (char)i;
            String sorted = sortLetters(word + lower);
            if (lettersToWord.containsKey(sorted)) {
                ArrayList<String> stringsToAdd = lettersToWord.get(sorted);
                for (int j = 0; j < stringsToAdd.size(); j++) {
                    if (isGoodWord(stringsToAdd.get(j), word)) {
                        result.add(stringsToAdd.get(j));
                    }
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> words = sizeToWords.get(wordLength);
        int index = random.nextInt(words.size());
        String word = words.get(index);
        while (getAnagramsWithOneMoreLetter(word).size() < MIN_NUM_ANAGRAMS) {
            index = random.nextInt(words.size());
            word = words.get(index);
        }
        if (wordLength < MAX_WORD_LENGTH) {
            wordLength++;
        }
        return word;
    }
}
