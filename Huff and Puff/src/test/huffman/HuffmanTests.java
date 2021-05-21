package test.huffman;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import main.huffman.*;

public class HuffmanTests {

    // =================================================
    // Test Configuration
    // =================================================

    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    //@Rule
    //public Timeout globalTimeout = Timeout.seconds(2);

    // Grade record-keeping
    static int possible = 0, passed = 0;

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

    // Compression Tests
    // -----------------------------------------------
    @Test
    public void comp_t0() {
        Huffman h = new Huffman("A");
        // byte 0: 1000 0000 (0 = ETB, 1 = 'A')
        // [!] Only first 2 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b10000000};
        assertArrayEquals(compressed, h.compress("A"));
    }

    @Test
    public void comp_t1() {
        Huffman h = new Huffman("AB");
        // byte 0: 1101 0000 (10 = ETB, 11 = 'A', 0 = 'B')
        // [!] Only first 5 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b11010000};
        assertArrayEquals(compressed, h.compress("AB"));
    }

    @Test
    public void comp_t2() {
        Huffman h = new Huffman("AB");
        // byte 0: 0111 0000 (10 = ETB, 11 = 'A', 0 = 'B')
        // [!] Only first 5 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b01110000};
        assertArrayEquals(compressed, h.compress("BA"));
    }

    @Test
    public void comp_t3() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 1010 0011 (100 = ETB, 101 = 'A', 0 = 'B', 11 = 'C')
        // byte 1: 1110 0000
        // [!] Only first 5 bits of byte 1 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b10100011, (byte) 0b11100000};
        assertArrayEquals(compressed, h.compress("ABBBCC"));
    }

    @Test
    public void comp_t4() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0101 0110 (100 = ETB, 101 = 'A', 0 = 'B', 11 = 'C')
        // byte 1: 1110 0000
        // [!] Only first 5 bits of byte 1 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b01010110, (byte) 0b11100000};
        assertArrayEquals(compressed, h.compress("BABCBC"));
    }


    // Decompression Tests
    // -----------------------------------------------
    @Test
    public void decomp_t0() {
        Huffman h = new Huffman("A");
        // byte 0: 1000 0000 (0 = ETB, 1 = 'A')
        // [!] Only first 2 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b10000000};
        assertEquals("A", h.decompress(compressed));
    }

    @Test
    public void decomp_t1() {
        Huffman h = new Huffman("AB");
        // byte 0: 1101 0000 (10 = ETB, 11 = 'A', 0 = 'B')
        // [!] Only first 5 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b11010000};
        assertEquals("AB", h.decompress(compressed));
    }

    @Test
    public void decomp_t2() {
        Huffman h = new Huffman("AB");
        // byte 0: 0111 0000 (10 = ETB, 11 = 'A', 0 = 'B')
        // [!] Only first 5 bits of byte 0 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b01110000};
        assertEquals("BA", h.decompress(compressed));
    }

    @Test
    public void decomp_t3() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 1010 0011 (100 = ETB, 101 = 'A', 0 = 'B', 11 = 'C')
        // byte 1: 1110 0000
        // [!] Only first 5 bits of byte 1 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b10100011, (byte) 0b11100000};
        assertEquals("ABBBCC", h.decompress(compressed));
    }

    @Test
    public void decomp_t4() {
        Huffman h = new Huffman("ABBBCC");
        // byte 0: 0101 0110 (100 = ETB, 101 = 'A', 0 = 'B', 11 = 'C')
        // byte 1: 1110 0000
        // [!] Only first 5 bits of byte 1 are meaningful (rest are padding)
        byte[] compressed = {(byte) 0b01010110, (byte) 0b11100000};
        assertEquals("BABCBC", h.decompress(compressed));
    }

}