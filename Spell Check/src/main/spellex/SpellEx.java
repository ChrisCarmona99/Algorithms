
package main.spellex;

import java.util.*;

public class SpellEx {



    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map 
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;


    
    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given
     * "dictionary" of words mapped to their frequencies found
     * in some corpus (with the higher counts being the more
     * prevalent, and thus, the more likely to be suggested)
     * @param words The map of words to their frequencies
     */
    public SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }
    
    /**
     * Returns the edit distance between the two input Strings
     * s0 and s1 based on the minimal number of insertions, deletions,
     * replacements, and transpositions required to transform s0
     * into s1
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance (String s0, String s1) {

        int minEditDistance;
        if (s0.equals(s1)) {
            minEditDistance = 0;
            return minEditDistance;
        }
        int[][] MemoTable = new int[s1.length() + 1][s0.length() + 1]; // Initializes memoization table as a two dimensional array: [row][col]
        String [] s0Array = new String[s0.length()];
        String [] s1Array = new String[s1.length()];

        for (int index = 0; index < s0.length(); index++) {
            s0Array[index] = String.valueOf(s0.charAt(index)); // Indexes letters from s0 into an array as strings
            MemoTable[0][index + 1] = index + 1; // Writes amount of insertions needed in each "gutter"... serves as our base cases for rest of algo
        }
        for (int index = 0; index < s1.length(); index++) {
            s1Array[index] = String.valueOf(s1.charAt(index)); // Indexes letters from s0 into an array as strings
            MemoTable[index + 1][0] = index + 1; // Writes amount of insertions needed in the "gutter"... serves as our base cases for rest of algo
        }

        int insertionCase;
        int deletionCase;
        int replacementCase;
        int transpositionCase = 0;

        for (int row = 1; row < s1.length() + 1; row++) {
            for (int col = 1; col < s0.length() + 1; col++) {

                MemoTable[row][col] = 0; // Initializes the current [row][col] to 0... will be replaced by the min of our 4 cases later...

                insertionCase = MemoTable[row][col - 1] + 1; //INSERTION
                deletionCase = MemoTable[row - 1][col] + 1; //DELETION

                int testReplace = 0;
                if (!s0Array[col - 1].equals(s1Array[row - 1])) { // NOTE: Must subtract 1 from "row" & "col" to account for row & col index starting at 1 instead of 0...
                    testReplace = 1;
                }
                replacementCase = MemoTable[row - 1][col - 1] + testReplace; // REPLACEMENT

                if (row >= 2 && col >= 2) {
                    if (s1Array[row - 1].equals(s0Array[col - 2]) && s0Array[col - 1].equals(s1Array[row - 2])) { // NOTE: must subtract an extra 1 from every "row" & "col" index for the same reason as in line 116
                        transpositionCase = MemoTable[row - 2][col - 2] + 1; // TRANSPOSITION
                    }
                }
                // THE FOLLOWING INPUTS THE SMALLEST VALUE OF OUR 4 CASES INTO THE CURR CELL WHILE CHECKING IF EACH CASE IS POSSIBLE:
                int minCase = 2147483647;
                if (row >= 1) {
                    if (insertionCase < minCase) {
                        minCase = insertionCase;
                    }
                }
                if (col >= 1) {
                    if (deletionCase < minCase) {
                        minCase = deletionCase;
                    }
                }
                if (row >= 1 && col >= 1) {
                    if (replacementCase < minCase) {
                        minCase = replacementCase;
                    }
                }
                if (row >= 2 && col >= 2) {
                    if (s1Array[row - 1].equals(s0Array[col - 2]) && s0Array[col - 1].equals(s1Array[row - 2])) {
                        if (transpositionCase < minCase) {
                            minCase = transpositionCase;
                        }
                    }
                }
                MemoTable[row][col] = minCase;
            }
        }
        minEditDistance = MemoTable[s1.length()][s0.length()];
        return minEditDistance;
    }



    /**
     * Returns the n closest words in the dictionary to the given word,
     * where "closest" is defined by:
     * <ul>
     *   <li>Minimal edit distance (with ties broken by:)</li>
     *   <li>Largest count / frequency in the dictionary (with ties broken by:)</li>
     *   <li>Ascending alphabetic order</li>
     * </ul>
     * @param word The word we are comparing against the closest in the dictionary
     * @param n The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant (String word, int n) {

        PriorityQueue<givenWordTieBreaker> wordPriorityQueue = new PriorityQueue<>();
        Map<String, Integer> editDistMap = new HashMap<>(); // INITIALIZES Map <"Dictionary word", EditDistance to given word>:

        // CREATES Map <"Word", EditDistance>:
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            int currEditDist = editDistance(word, entry.getKey());
            editDistMap.put(entry.getKey(), currEditDist);
        }

        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            givenWordTieBreaker curr = new givenWordTieBreaker(entry.getKey(), editDistMap.get(entry.getKey()), entry.getValue());
            wordPriorityQueue.add(curr);
        }

        Set<String> output = new HashSet<>();
        for (int i = 0; i < n; i++) {
            givenWordTieBreaker curr = wordPriorityQueue.poll();
            if ( !(curr == null) ) {
                output.add(curr.givenWord);
            }
        }

        return output;
    }
    /**
     * Creates a class used to compare two words in the given dictionary
     */
    public static class givenWordTieBreaker implements Comparable<givenWordTieBreaker> {

        String givenWord;
        int editDist;
        int frequency;

        public givenWordTieBreaker (String givenWord, int editDist, int frequency) {
            this.givenWord = givenWord;
            this.editDist = editDist;
            this.frequency = frequency;

        }
        /**
         * Outputs which one is closer to the given word. This is based off of
         * edit distance, with ties broken by frequency, and then lastly with ties
         * broken by alphabetical order.
         * @param other = inputed word to compare
         * @return returns -1, 0, or 1 depending on the output of the if-statement logic...
         * in priority queue containing 'givenWordTieBreaker' objects
         */
        public int compareTo (givenWordTieBreaker other) {
            if (this.editDist == other.editDist && this.frequency == other.frequency) {
                int compare = this.givenWord.compareTo(other.givenWord);
                if (compare < 0) {
                    return -1;
                } else if (compare > 0) {
                    return 1;
                }
            } else if (this.editDist == other.editDist) {
                if (this.frequency < other.frequency) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (this.editDist < other.editDist) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 0;
        }

    }





    /**
     * Returns the set of n most frequent words in the dictionary to occur with
     * edit distance distMax or less compared to the given word. Ties in
     * max frequency are broken with ascending alphabetic order.
     * @param word The word to compare to those in the dictionary
     * @param n The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested / returned 
     * words from the dictionary can stray from the given word
     * @return The set of n suggested words from the dictionary with edit distance
     * distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance (String word, int n, int distMax) {

        Set<String> possibleWordList = new HashSet<>();
        possibleWordList.add(word);

        getInitialInsertionWords(word, possibleWordList, LETTERS);
        getInitialDeletionWords(word, possibleWordList);
        getInitialReplacementWords(word, possibleWordList, LETTERS);
        if (word.length() > 1) {
            getInitialTranspositionWords(word, possibleWordList);
        }

        Set<String> tempWordList = new HashSet<>();

        if (distMax > 1) {
            for (int count = 1; count < distMax; count++) {

                getInsertionWords(possibleWordList, tempWordList, LETTERS);
                getDeletionWords(possibleWordList, tempWordList);
                getReplacementWords(possibleWordList, tempWordList, LETTERS);
                getTranspositionWords(possibleWordList, tempWordList);

                possibleWordList.addAll(tempWordList);
                tempWordList.clear();
            }
        }
        possibleWordList = filterWords(possibleWordList, n);

        return possibleWordList;
    }


    public void getInitialInsertionWords (String word, Set<String> possibleWordList, char[] alphabet) {

        StringBuilder str = new StringBuilder();
        str.append(word);
        for (int i = 0; i < word.length() + 1; i++) {
            for (char c : alphabet) {
                str.insert(i, c);
                possibleWordList.add(str.toString());
                str.delete(i, i + 1);
            }
        }
        str.delete(0, word.length());
    }
    public void getInsertionWords (Set<String> possibleWordList, Set<String> tempWordList, char[] alphabet) {

        StringBuilder str = new StringBuilder();

        for (String curr : possibleWordList) {
            str.append(curr);
            for (int i = 0; i < curr.length(); i++) {
                for (char c : alphabet) {
                    str.insert(i, c);
                    tempWordList.add(str.toString());
                    str.delete(i, i + 1);
                }
            }
            str.delete(0, curr.length());
        }
    }

    public void getInitialDeletionWords (String word, Set<String> possibleWordList) {

        StringBuilder str = new StringBuilder();
        str.append(word);
        for (int i = 0; i < word.length(); i++) {
            str.delete(i, i+1);
            possibleWordList.add(str.toString());
            str.delete(0, word.length());
            str.append(word);
        }
        str.delete(0, word.length());
    }
    public void getDeletionWords(Set<String> possibleWordList, Set<String> tempWordList) {

        StringBuilder str = new StringBuilder();

        for (String curr : possibleWordList) {
            str.append(curr);
            for (int i = 0; i < curr.length(); i++) {
                str.delete(i, i+1);
                tempWordList.add(str.toString());
                str.delete(0, curr.length());
                str.append(curr);
            }
            str.delete(0, curr.length());
        }
    }

    public void getInitialReplacementWords (String word, Set<String> possibleWordList, char[] alphabet) {

        StringBuilder str = new StringBuilder();
        str.append(word);
        for (int i = 0; i < word.length(); i++) {
            for (char c : alphabet) {
                str.delete(i, i + 1);
                str.insert(i, c);
                possibleWordList.add(str.toString());

                str.delete(0, word.length());
                str.append(word);
            }
        }
    }
    public void getReplacementWords (Set<String> possibleWordList, Set<String> tempWordList, char[] alphabet) {

        StringBuilder str = new StringBuilder();

        for (String curr : possibleWordList) {
            str.append(curr);
            for (int i = 0; i < curr.length(); i++) {
                for (char c : alphabet) {
                    str.delete(i, i + 1);
                    str.insert(i, c);
                    tempWordList.add(str.toString());
                    str.delete(0, curr.length());
                    str.append(curr);
                }
            }
            str.delete(0, curr.length()); // Resets 'curr' for the next word in 'possibleWordList'
        }
    }

    public void getInitialTranspositionWords (String word, Set<String> possibleWordList) {

        StringBuilder str = new StringBuilder();
        str.append(word);
        for (int i = 0; i < word.length() - 1; i++) {
            char temp = str.charAt(i + 1);
            str.replace(i+1, i+2, Character.toString(str.charAt(i)));
            str.replace(i, i+1, Character.toString(temp));
            possibleWordList.add(str.toString());

            str.delete(0, word.length());
            str.append(word);
        }
    }
    public void getTranspositionWords (Set<String> possibleWordList, Set<String> tempWordList) {

        StringBuilder str = new StringBuilder();

        for (String curr : possibleWordList) {
            str.append(curr);
            for (int i = 0; i < curr.length() - 1; i++) {
                char temp = str.charAt(i + 1);
                str.replace(i+1, i+2, Character.toString(str.charAt(i)));
                str.replace(i, i+1, Character.toString(temp));
                tempWordList.add(str.toString());

                str.delete(0, curr.length());
                str.append(curr);
            }
            str.delete(0, curr.length()); // Resets 'curr' for the next word in 'possibleWordList'
        }
    }

    public Set<String> filterWords (Set<String> possibleWordList, int nWords) {

        Map<String, Integer> frequencyMap = new HashMap<>();
        PriorityQueue<givenWordTieBreaker> wordPriorityQueue = new PriorityQueue<>();

        for (String curr : possibleWordList) {
            if ( dict.containsKey(curr) ) {
                frequencyMap.put(curr, dict.get(curr));
            }
        }

        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            givenWordTieBreaker newWord = new givenWordTieBreaker(entry.getKey(), 0, entry.getValue());
            wordPriorityQueue.add(newWord);
        }

        Set<String> output = new HashSet<>();
        for (int i = 0; i < nWords; i++) {
            if ( !wordPriorityQueue.isEmpty() ) {
                givenWordTieBreaker curr = wordPriorityQueue.poll();
                output.add(curr.givenWord);
            }
        }
        return output;

    }


} // END OF SpellEx