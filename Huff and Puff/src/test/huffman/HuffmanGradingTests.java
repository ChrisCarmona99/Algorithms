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

public class HuffmanGradingTests {

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

    @Test
    public void comp_t5() {
        Huffman h = new Huffman("");
        byte[] compressed = {(byte) 0b00000000};
        assertArrayEquals(compressed, h.compress(""));
    }

    @Test
    public void comp_t6() {
        Huffman h = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertArrayEquals(compressed, h.compress("ABCDE"));
    }

    @Test
    public void comp_t7() {
        Huffman h = new Huffman("EDCBA");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertArrayEquals(compressed, h.compress("ABCDE"));
    }

    @Test
    public void comp_t8() {
        Huffman h = new Huffman("BACED");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertArrayEquals(compressed, h.compress("ABCDE"));
    }

    @Test
    public void comp_t9() {
        Huffman h = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b01001111, (byte) 0b10101100};
        assertArrayEquals(compressed, h.compress("EDCBA"));
    }

    @Test
    public void comp_t10() {
        Huffman h = new Huffman("ABCDE"),
                h2 = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertArrayEquals(compressed, h.compress("ABCDE"));
        // Make sure it works in repetition -- no weird static vars right?
        assertArrayEquals(compressed, h.compress("ABCDE"));
        assertArrayEquals(compressed, h2.compress("ABCDE"));
    }

    @Test
    public void comp_t11() {
        Huffman h = new Huffman("AAAAABBBBCCCDDE");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertArrayEquals(compressed, h.compress("AAAAABBBBCCCDDE"));
    }

    @Test
    public void comp_t12() {
        Huffman h = new Huffman("EDDCCCBBBBAAAAA");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertArrayEquals(compressed, h.compress("AAAAABBBBCCCDDE"));
    }

    @Test
    public void comp_t13() {
        Huffman h = new Huffman("ABCDEABCDABCABA");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertArrayEquals(compressed, h.compress("AAAAABBBBCCCDDE"));
    }

    @Test
    public void comp_t14() {
        Huffman h = new Huffman("AAAAABBBBCCCDDE");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b01010110, (byte) 0b11000000, (byte) 0b10101010, (byte) 0b11111111, (byte) 0b11010000};
        assertArrayEquals(compressed, h.compress("EDDCCCBBBBAAAAA"));
    }

    @Test
    public void comp_t15() {
        Huffman h = new Huffman("AABBCCDDEEFF");
        // (010 = ETB, 011 = 'A', 100 = 'B', 101 = 'C', 110 = 'D', 111 = 'E', 00 = 'F')
        byte[] compressed = {(byte) 0b01101110, (byte) 0b01001011, (byte) 0b01110110, (byte) 0b11111100, (byte) 0b00010000};
        assertArrayEquals(compressed, h.compress("AABBCCDDEEFF"));
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

    @Test
    public void decomp_t5() {
        Huffman h = new Huffman("");
        byte[] compressed = {(byte) 0b00000000};
        assertEquals("", h.decompress(compressed));
    }

    @Test
    public void decomp_t6() {
        Huffman h = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertEquals("ABCDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t7() {
        Huffman h = new Huffman("EDCBA");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertEquals("ABCDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t8() {
        Huffman h = new Huffman("BACED");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        assertEquals("ABCDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t9() {
        Huffman h = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b01001111, (byte) 0b10101100};
        assertEquals("EDCBA", h.decompress(compressed));
    }

    @Test
    public void decomp_t10() {
        Huffman h = new Huffman("ABCDE"),
                h2 = new Huffman("ABCDE");
        // (100 = ETB, 101 = 'A', 110 = 'B', 111 = 'C', 00 = 'D', 01 = 'E')
        byte[] compressed = {(byte) 0b10111011, (byte) 0b10001100};
        // Make sure it works in repetition -- no weird static vars right?
        assertEquals("ABCDE", h.decompress(compressed));
        assertEquals("ABCDE", h.decompress(compressed));
        assertEquals("ABCDE", h2.decompress(compressed));
    }

    @Test
    public void decomp_t11() {
        Huffman h = new Huffman("AAAAABBBBCCCDDE");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertEquals("AAAAABBBBCCCDDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t12() {
        Huffman h = new Huffman("EDDCCCBBBBAAAAA");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertEquals("AAAAABBBBCCCDDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t13() {
        Huffman h = new Huffman("ABCDEABCDABCABA");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b11111111, (byte) 0b11101010, (byte) 0b10000000, (byte) 0b01101101, (byte) 0b01010000};
        assertEquals("AAAAABBBBCCCDDE", h.decompress(compressed));
    }

    @Test
    public void decomp_t14() {
        Huffman h = new Huffman("AAAAABBBBCCCDDE");
        // (0100 = ETB, 11 = 'A', 10 = 'B', 00 = 'C', 011 = 'D', 0101 = 'E')
        byte[] compressed = {(byte) 0b01010110, (byte) 0b11000000, (byte) 0b10101010, (byte) 0b11111111, (byte) 0b11010000};
        assertEquals("EDDCCCBBBBAAAAA", h.decompress(compressed));
    }

    @Test
    public void decomp_t15() {
        Huffman h = new Huffman("AABBCCDDEEFF");
        // (010 = ETB, 011 = 'A', 100 = 'B', 101 = 'C', 110 = 'D', 111 = 'E', 00 = 'F')
        byte[] compressed = {(byte) 0b01101110, (byte) 0b01001011, (byte) 0b01110110, (byte) 0b11111100, (byte) 0b00010000};
        assertEquals("AABBCCDDEEFF", h.decompress(compressed));
    }

}
