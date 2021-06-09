package test.spellex;

import static org.junit.Assert.*;
import static main.spellex.SpellEx.*;
import main.spellex.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;

public class SpellExTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
    static Map<String, Integer> tinyDict = new TreeMap<>(),
                                bigDict  = new TreeMap<>();
    
    /**
     * Loads a word-frequency file into the given dictionary
     * @param filename Name of the word-frequency file, a tab-separated
     * word frequency mapping
     * @param dict The map of words to their frequencies to be populated from
     * the given file
     * @throws FileNotFoundException
     */
    public static void populateDictFromFile (String filename, Map<String, Integer> dict) throws FileNotFoundException {
        File file = new File("./src/test/spellex/" + filename);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split("=");
            String word = line[0].toLowerCase();
            int count = Integer.parseInt(line[3]);
            if (!dict.containsKey(word)) {
                dict.put(word, count);
            }
        }
        sc.close();
    }
    
    // the @BeforeClass is run once before the tests start
    @BeforeClass
    public static void makeDicts () {
        // Small sample dictionary with just a few words and
        // frequencies -- good for early debugging
        tinyDict.put("ab", 1);
        tinyDict.put("bat", 3);
        tinyDict.put("ball", 2);
        tinyDict.put("mall", 5);
        tinyDict.put("cat", 4);
        tinyDict.put("dog", 1000);
        
        // Large dictionary built from huge text corpus --
        // good for testing efficiency
        try {
            populateDictFromFile("anc.txt", bigDict);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    // =================================================
    // Unit Tests
    // =================================================
    
    
    /**
     * Basic edit distance tests between two arbitrary Strings
     */
    
    @Test
    public void editDist_t0() {
        assertEquals(0, editDistance("", ""));
        assertEquals(0, editDistance("a", "a"));
        assertEquals(0, editDistance("abc", "abc"));
    }
    
    @Test
    public void editDist_t1() {
        assertEquals(1, editDistance("a", ""));
        assertEquals(1, editDistance("", "a"));
        assertEquals(2, editDistance("aa", ""));
        assertEquals(2, editDistance("", "aa"));
        assertEquals(2, editDistance("ab", "abcd"));
    }
    
    @Test
    public void editDist_t2() {
        assertEquals(1, editDistance("a", "b"));
        assertEquals(1, editDistance("b", "a"));
        assertEquals(2, editDistance("ab", "cd"));
        assertEquals(3, editDistance("cat", "dog"));
    }
    
    @Test
    public void editDist_t3() {
        assertEquals(1, editDistance("ab", "ba"));
        assertEquals(1, editDistance("bar", "bra"));
    }
    
    
    /**
     * Basic tests to make sure SpellEx can obtain correct
     * results from a tiny dictionary
     */
    
    @Test
    public void LeastDistantTest_t0() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("ab")), se.getNLeastDistant("ab", 1));
        assertEquals(new HashSet<String>(Arrays.asList("bat")), se.getNLeastDistant("ba", 1));
    }
    
    @Test
    public void LeastDistantTest_t1() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("ab", "bat")), se.getNLeastDistant("ba", 2));
        assertEquals(new HashSet<String>(Arrays.asList("bat", "cat")), se.getNLeastDistant("dat", 2));
    }
    
    @Test
    public void LeastDistantTest_t2() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("dog", "cat", "bat")), se.getNLeastDistant("wtfisthis", 3));
    }
    
    @Test
    public void NBestUnderDistanceTest_t0() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("ab")), se.getNBestUnderDistance("a", 1, 1));
        assertEquals(new HashSet<String>(Arrays.asList("ab")), se.getNBestUnderDistance("a", 2, 1));
    }
    
    @Test
    public void NBestUnderDistanceTest_t1() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("cat", "bat")), se.getNBestUnderDistance("a", 2, 2));
    }
    
    @Test
    public void NBestUnderDistanceTest_t2() {
        SpellEx se = new SpellEx(tinyDict);
        assertEquals(new HashSet<String>(Arrays.asList("ball", "mall")), se.getNBestUnderDistance("call", 2, 1));
    }
    
    /**
     * Basic tests to make sure SpellEx can obtain correct
     * results from a tiny dictionary
     * [!] NOTE: getNLeastDistant will NEVER be called on the bigDict
     * since it's exhaustive and will take awhile; getNBestUnderDistance,
     * however, will be, and should complete well under the time cap
     */
    
    @Test
    public void NBestUnderDistanceTest_big_t0() {
        SpellEx se = new SpellEx(bigDict);
        assertEquals(new HashSet<String>(Arrays.asList("a")), se.getNBestUnderDistance("a", 1, 1));
        assertEquals(new HashSet<String>(Arrays.asList()), se.getNBestUnderDistance("irspellbad", 2, 2));
    }
    
    @Test
    public void NBestUnderDistanceTest_big_t1() {
        SpellEx se = new SpellEx(bigDict);
        assertEquals(new HashSet<String>(Arrays.asList("for", "found", "words")), se.getNBestUnderDistance("forns", 3, 2));
        assertEquals(new HashSet<String>(Arrays.asList("chrysanthemum", "chrysanthemums")), se.getNBestUnderDistance("chysanthemum", 5, 2));
    }
    
    // ... more for you to test!
    
}